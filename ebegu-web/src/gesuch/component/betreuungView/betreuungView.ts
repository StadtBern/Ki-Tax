import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSKindContainer from '../../../models/TSKindContainer';
import {getTSBetreuungsangebotTypValues, TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import TSInstitutionStammdaten from '../../../models/TSInstitutionStammdaten';
import TSBetreuungspensumContainer from '../../../models/TSBetreuungspensumContainer';
import TSBetreuung from '../../../models/TSBetreuung';
import TSBetreuungspensum from '../../../models/TSBetreuungspensum';
import {TSDateRange} from '../../../models/types/TSDateRange';
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';
import BerechnungsManager from '../../service/berechnungsManager';
import EbeguUtil from '../../../utils/EbeguUtil';
import ErrorService from '../../../core/errors/service/ErrorService';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import DateUtil from '../../../utils/DateUtil';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import {IBetreuungStateParams} from '../../gesuch.route';
import MitteilungRS from '../../../core/service/mitteilungRS.rest';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import {TSAntragStatus} from '../../../models/enums/TSAntragStatus';
import * as moment from 'moment';
import TSBetreuungsmitteilung from '../../../models/TSBetreuungsmitteilung';
import Moment = moment.Moment;
import IScope = angular.IScope;
import ILogService = angular.ILogService;
let template = require('./betreuungView.html');
require('./betreuungView.less');
let removeDialogTemplate = require('../../dialog/removeDialogTemplate.html');

export class BetreuungViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = BetreuungViewController;
    controllerAs = 'vm';
}

export class BetreuungViewController extends AbstractGesuchViewController<TSBetreuung> {
    betreuungsangebot: any;
    betreuungsangebotValues: Array<any>;
    instStammId: string; //der ausgewaehlte instStammId wird hier gespeichert und dann in die entsprechende InstitutionStammdaten umgewandert
    isSavingData: boolean; // Semaphore
    initialBetreuung: TSBetreuung;
    flagErrorVertrag: boolean;
    kindModel: TSKindContainer;
    betreuungIndex: number;
    isMutationsmeldungStatus: boolean;
    mutationsmeldungModel: TSBetreuung;
    existingMutationsMeldung: TSBetreuungsmitteilung;
    isNewestGesuch: boolean;

    static $inject = ['$state', 'GesuchModelManager', 'EbeguUtil', 'CONSTANTS', '$scope', 'BerechnungsManager', 'ErrorService',
        'AuthServiceRS', 'WizardStepManager', '$stateParams', 'MitteilungRS', 'DvDialog', '$log'];
    /* @ngInject */
    constructor(private $state: IStateService, gesuchModelManager: GesuchModelManager, private ebeguUtil: EbeguUtil, private CONSTANTS: any,
                $scope: IScope, berechnungsManager: BerechnungsManager, private errorService: ErrorService,
                private authServiceRS: AuthServiceRS, wizardStepManager: WizardStepManager, private $stateParams: IBetreuungStateParams,
                private mitteilungRS: MitteilungRS, private dvDialog: DvDialog, private $log: ILogService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.BETREUUNG);

    }

    $onInit() {
        this.mutationsmeldungModel = undefined;
        this.isMutationsmeldungStatus = false;
        let kindIndex: number = this.gesuchModelManager.convertKindNumberToKindIndex(parseInt(this.$stateParams.kindNumber, 10));
        if (kindIndex >= 0) {
            this.gesuchModelManager.setKindIndex(kindIndex);
            if (this.$stateParams.betreuungNumber) {
                this.betreuungIndex = this.gesuchModelManager.convertBetreuungNumberToBetreuungIndex(parseInt(this.$stateParams.betreuungNumber));
                this.model = angular.copy(this.gesuchModelManager.getKindToWorkWith().betreuungen[this.betreuungIndex]);
                this.initialBetreuung = angular.copy(this.gesuchModelManager.getKindToWorkWith().betreuungen[this.betreuungIndex]);
                this.gesuchModelManager.setBetreuungIndex(this.betreuungIndex);
            } else {
                //wenn betreuung-nummer nicht definiert ist heisst dass, das wir ein neues erstellen sollten
                this.model = this.initEmptyBetreuung();
                this.initialBetreuung = angular.copy(this.model);
                this.betreuungIndex = this.gesuchModelManager.getKindToWorkWith().betreuungen ? this.gesuchModelManager.getKindToWorkWith().betreuungen.length : 0;
                this.gesuchModelManager.setBetreuungIndex(this.betreuungIndex);
            }

            this.setBetreuungsangebotTypValues();
            this.betreuungsangebot = undefined;
            this.initViewModel();

            // just to read!
            this.kindModel = this.gesuchModelManager.getKindToWorkWith();
        } else {
            this.$log.error('There is no kind available with kind-number:' + this.$stateParams.kindNumber);
        }
        this.gesuchModelManager.isNeuestesGesuch().then((response: boolean) => {
            this.isNewestGesuch = response;
        });

        this.findExistingBetreuungsmitteilung();
    }

    /**
     * Creates a Betreuung for the kind given by the kindNumber attribute of the class.
     * Thus the kindnumber must be set before this method is called.
     */
    public initEmptyBetreuung(): TSBetreuung {
        let tsBetreuung: TSBetreuung = new TSBetreuung();
        tsBetreuung.betreuungsstatus = TSBetreuungsstatus.AUSSTEHEND;
        return tsBetreuung;
    }

    private initViewModel() {
        this.isSavingData = false;
        this.flagErrorVertrag = false;
        if (this.getInstitutionSD()) {
            this.instStammId = this.getInstitutionSD().id;
            this.betreuungsangebot = this.getBetreuungsangebotFromInstitutionList();
        }
        this.startEmptyListOfBetreuungspensen();
        //institutionen lazy laden
        if (!this.gesuchModelManager.getActiveInstitutionenList() || this.gesuchModelManager.getActiveInstitutionenList().length <= 0) {
            this.gesuchModelManager.updateActiveInstitutionenList();
        }
    }

    /**
     * Fuer Institutionen und Traegerschaften wird es geprueft ob es schon ein Betreuungspensum existiert,
     * wenn nicht wir die Liste dann mit einem leeren initiallisiert
     */
    private startEmptyListOfBetreuungspensen() {
        if ((!this.getBetreuungspensen() || this.getBetreuungspensen().length === 0)
            && (this.authServiceRS.isOneOfRoles(TSRoleUtil.getTraegerschaftInstitutionOnlyRoles()))) {
            // nur fuer Institutionen wird ein Betreuungspensum by default erstellt
            this.createBetreuungspensum();
        }
    }

    public getGesuchsperiodeBegin(): moment.Moment {
        return this.gesuchModelManager.getGesuchsperiodeBegin();
    }

    private getBetreuungsangebotFromInstitutionList() {
        return $.grep(this.betreuungsangebotValues, (value: any) => {
            return value.key === this.getInstitutionSD().betreuungsangebotTyp;
        })[0];
    }

    public getKindModel(): TSKindContainer {
        return this.kindModel;
    }

    public getBetreuungModel(): TSBetreuung {
        if (this.isMutationsmeldungStatus && this.mutationsmeldungModel) {
            return this.mutationsmeldungModel;
        }
        return this.model;
    }

    public changedAngebot() {
        if (this.getBetreuungModel()) {
            if (this.isTagesschule()) {
                this.getBetreuungModel().betreuungsstatus = TSBetreuungsstatus.SCHULAMT;
                // Fuer Tagesschule setzen wir eine Dummy-Tagesschule als Institution
                this.instStammId = this.CONSTANTS.INSTITUTIONSSTAMMDATENID_DUMMY_TAGESSCHULE;
                this.setSelectedInstitutionStammdaten();
            } else {
                this.getBetreuungModel().betreuungsstatus = TSBetreuungsstatus.AUSSTEHEND;
            }
        }
    }

    private save(newStatus: TSBetreuungsstatus, nextStep: string, params: any): void {
        this.isSavingData = true;
        this.gesuchModelManager.setBetreuungToWorkWith(this.model); //setze model
        let oldStatus: TSBetreuungsstatus = this.model.betreuungsstatus;
        if (this.getBetreuungModel()) {
            if (this.isTagesschule()) {
                this.getBetreuungModel().betreuungspensumContainers = []; // fuer Tagesschule werden keine Betreuungspensum benoetigt, deswegen löschen wir sie vor dem Speichern
            }
        }
        this.errorService.clearAll();
        this.model.betreuungsstatus = newStatus;
        this.gesuchModelManager.saveBetreuung(this.model, false).then((betreuungResponse: any) => {
            this.isSavingData = false;
            this.form.$setPristine();
            this.$state.go(nextStep, params);
        }).catch((exception) => {
            // starting over
            this.$log.error('there was an error saving the betreuung ', this.model);
            this.isSavingData = false;
            this.model.betreuungsstatus = oldStatus;
            this.startEmptyListOfBetreuungspensen();
            this.form.$setUntouched();
            this.form.$setPristine();
            return undefined;
        });
    }

    private setBetreuungsangebotTypValues(): void {
        this.betreuungsangebotValues = this.ebeguUtil.translateStringList(getTSBetreuungsangebotTypValues());
    }

    public cancel() {
        this.reset();
        this.form.$setPristine();
        this.$state.go('gesuch.betreuungen', {gesuchId: this.getGesuchId()});
    }

    reset() {
        this.removeBetreuungFromKind(); //wenn model existiert und nicht neu ist wegnehmen, sonst resetten
    }

    private removeBetreuungFromKind(): void {
        if (this.model && !this.model.timestampErstellt) {
            //wenn die Betreeung noch nicht erstellt wurde, loeschen wir die Betreuung vom Array
            this.gesuchModelManager.removeBetreuungFromKind();
        }
    }

    public getInstitutionenSDList(): Array<TSInstitutionStammdaten> {
        let result: Array<TSInstitutionStammdaten> = [];
        if (this.betreuungsangebot) {
            this.gesuchModelManager.getActiveInstitutionenList().forEach((instStamm: TSInstitutionStammdaten) => {
                if (instStamm.betreuungsangebotTyp === this.betreuungsangebot.key) {
                    result.push(instStamm);
                }
            });
        }
        return result;
    }

    public getInstitutionSD(): TSInstitutionStammdaten {
        if (this.getBetreuungModel()) {
            return this.getBetreuungModel().institutionStammdaten;
        }
        return undefined;
    }

    public getBetreuungspensen(): Array<TSBetreuungspensumContainer> {
        if (this.getBetreuungModel()) {
            return this.getBetreuungModel().betreuungspensumContainers;
        }
        return undefined;
    }

    public getBetreuungspensum(index: number): TSBetreuungspensumContainer {
        if (this.getBetreuungspensen() && index >= 0 && index < this.getBetreuungspensen().length) {
            return this.getBetreuungspensen()[index];
        }
        return undefined;
    }

    public createBetreuungspensum(): void {
        if (this.getBetreuungModel() && (this.getBetreuungspensen() === undefined || this.getBetreuungspensen() === null)) {
            this.getBetreuungModel().betreuungspensumContainers = [];
        }
        if (!this.getBetreuungModel()) {
            this.errorService.addMesageAsError('Betreuungsmodel ist nicht initialisiert.');
        }
        this.getBetreuungspensen().push(new TSBetreuungspensumContainer(undefined, new TSBetreuungspensum(false, undefined, new TSDateRange())));
    }

    public removeBetreuungspensum(betreuungspensumToDelete: TSBetreuungspensumContainer): void {
        let position: number = this.getBetreuungspensen().indexOf(betreuungspensumToDelete);
        if (position > -1) {
            this.getBetreuungspensen().splice(position, 1);
        }
    }

    public setSelectedInstitutionStammdaten(): void {
        let instStamList = this.gesuchModelManager.getActiveInstitutionenList();
        for (let i: number = 0; i < instStamList.length; i++) {
            if (instStamList[i].id === this.instStammId) {
                this.model.institutionStammdaten = instStamList[i];
            }
        }
    }

    public platzAnfordern(): void {
        if (this.isGesuchValid() && this.getBetreuungModel().vertrag === true) {
            this.flagErrorVertrag = false;
            this.save(TSBetreuungsstatus.WARTEN, 'gesuch.betreuungen', {gesuchId: this.getGesuchId()});
        } else if (this.getBetreuungModel().vertrag !== true) {
            this.flagErrorVertrag = true;
        }
    }

    public platzBestaetigen(): void {
        if (this.isGesuchValid()) {
            this.getBetreuungModel().datumBestaetigung = DateUtil.today();
            this.save(TSBetreuungsstatus.BESTAETIGT, 'pendenzenInstitution', undefined);
        }
    }

    /**
     * Wenn ein Betreuungsangebot abgewiesen wird, muss man die neu eingegebenen Betreuungspensen zuruecksetzen, da sie nicht relevant sind.
     * Allerdings muessen der Grund und das Datum der Ablehnung doch gespeichert werden.
     * In diesem Fall machen wir keine Validierung weil die Daten die eingegeben werden muessen, direkt auf dem Server gecheckt werden
     */
    public platzAbweisen(): void {
        //copy values modified by the Institution in initialBetreuung
        this.initialBetreuung.erweiterteBeduerfnisse = this.getBetreuungModel().erweiterteBeduerfnisse;
        this.initialBetreuung.grundAblehnung = this.getBetreuungModel().grundAblehnung;
        //restore initialBetreuung
        this.model = angular.copy(this.initialBetreuung);
        this.model.datumAblehnung = DateUtil.today();
        this.save(TSBetreuungsstatus.ABGEWIESEN, 'pendenzenInstitution', undefined);
    }

    public gekuendigtVorEintritt(): void {
        if (this.isGesuchValid()) {
            this.getBetreuungModel().datumBestaetigung = DateUtil.today();

            for (let i: number = 0; i < this.getBetreuungspensen().length; i++) {
                this.getBetreuungspensum(i).betreuungspensumJA.pensum = 0;
                this.getBetreuungspensum(i).betreuungspensumJA.nichtEingetreten = true;
            }
            this.getBetreuungModel().erweiterteBeduerfnisse = false;

            this.save(TSBetreuungsstatus.GEKUENDIGT_VOR_EINTRITT, 'pendenzenInstitution', undefined);
        }
    }

    public saveSchulamt(): void {
        if (this.isGesuchValid()) {
            this.save(TSBetreuungsstatus.SCHULAMT, 'gesuch.betreuungen', {gesuchId: this.getGesuchId()});
        }
    }

    /**
     * Returns true when the user is allowed to edit the content. This happens when the status is AUSSTEHEHND
     * or SCHULAMT and we are not yet in the KorrekturmodusJugendamt
     * @returns {boolean}
     */
    public isEnabled(): boolean {
        if (this.getBetreuungModel()) {
            return !this.getBetreuungModel().hasVorgaenger()
                && (this.isBetreuungsstatus(TSBetreuungsstatus.AUSSTEHEND) || (this.isBetreuungsstatus(TSBetreuungsstatus.SCHULAMT)
                && !this.isKorrekturModusJugendamt()));
        }
        return false;
    }

    public isBetreuungsstatusWarten(): boolean {
        return this.isBetreuungsstatus(TSBetreuungsstatus.WARTEN);
    }

    public isBetreuungsstatusAbgewiesen(): boolean {
        return this.isBetreuungsstatus(TSBetreuungsstatus.ABGEWIESEN);
    }

    public isBetreuungsstatusBestaetigt(): boolean {
        return this.isBetreuungsstatus(TSBetreuungsstatus.BESTAETIGT);
    }

    public isBetreuungsstatusNichtEingetreten(): boolean {
        return this.isBetreuungsstatus(TSBetreuungsstatus.NICHT_EINGETRETEN);
    }

    public isGekuendigtVorAntritt(): boolean {
        return this.isBetreuungsstatus(TSBetreuungsstatus.GEKUENDIGT_VOR_EINTRITT);
    }

    public isBetreuungsstatusAusstehend(): boolean {
        return this.isBetreuungsstatus(TSBetreuungsstatus.AUSSTEHEND);
    }

    public isBetreuungsstatusSchulamt(): boolean {
        return this.isBetreuungsstatus(TSBetreuungsstatus.SCHULAMT);
    }

    private isBetreuungsstatus(status: TSBetreuungsstatus): boolean {
        if (this.getBetreuungModel()) {
            return this.getBetreuungModel().betreuungsstatus === status;
        }
        return false;
    }

    public isTagesschule(): boolean {
        return this.isBetreuungsangebottyp(TSBetreuungsangebotTyp.TAGESSCHULE);
    }

    public isTageseltern(): boolean {
        return this.isBetreuungsangebottyp(TSBetreuungsangebotTyp.TAGESELTERN_KLEINKIND) ||
            this.isBetreuungsangebottyp(TSBetreuungsangebotTyp.TAGESELTERN_SCHULKIND);
    }

    private isBetreuungsangebottyp(betAngTyp: TSBetreuungsangebotTyp): boolean {
        if (this.betreuungsangebot) {
            return this.betreuungsangebot.key === TSBetreuungsangebotTyp[betAngTyp];
        }
        return false;
    }

    /**
     * Erweiterte Beduerfnisse wird nur beim Institutionen oder Traegerschaften eingeblendet oder wenn das Feld schon als true gesetzt ist
     * ACHTUNG: Hier benutzen wir die Direktive dv-show-element nicht, da es unterschiedliche Bedingungen für jede Rolle gibt.
     * @returns {boolean}
     */
    public showErweiterteBeduerfnisse(): boolean {
        return this.authServiceRS.isOneOfRoles(TSRoleUtil.getTraegerschaftInstitutionRoles())
            || this.getBetreuungModel().erweiterteBeduerfnisse === true;
    }

    public showFalscheAngaben(): boolean {
        return (this.isBetreuungsstatusBestaetigt() || this.isBetreuungsstatusAbgewiesen()) && !this.isGesuchReadonly()
            && !this.isFromMutation();
    }

    public showAngabenKorrigieren(): boolean {
        return (this.isBetreuungsstatusBestaetigt() || this.isBetreuungsstatusAbgewiesen()) && !this.isGesuchReadonly()
            && this.isFromMutation();
    }

    public isFromMutation(): boolean {
        if (this.getBetreuungModel()) {
            if (this.getBetreuungModel().vorgaengerId) {
                return true;
            }
        }
        return false;
    }

    public showAngabeKorrigieren(): boolean {
        return (this.isBetreuungsstatusBestaetigt() || this.isBetreuungsstatusAbgewiesen())
            && !this.isGesuchReadonly() && this.isFromMutation();
    }

    public mutationsmeldungErstellen(): void {
        //create dummy copy of model
        this.mutationsmeldungModel = angular.copy(this.getBetreuungModel());
        this.isMutationsmeldungStatus = true;
    }

    /**
     * Mutationsmeldungen werden nur bei Mutationen oder bei Gesuchen in Status VERFUEGT erlaubt. Ausserdem muss es
     * sich um letztes bzw. neuestes Gesuch handeln
     */
    public isMutationsmeldungAllowed(): boolean {
        return (this.isMutation() || (this.gesuchModelManager.getGesuch().status === TSAntragStatus.VERFUEGT))
            && this.isNewestGesuch
            && this.getBetreuungModel().betreuungsstatus !== TSBetreuungsstatus.WARTEN;
    }

    public mutationsmeldungSenden(): void {
        // send mutationsmeldung (dummy copy)
        if (this.isGesuchValid() && this.mutationsmeldungModel) {
            this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
                title: 'MUTATIONSMELDUNG_CONFIRMATION',
                deleteText: 'MUTATIONSMELDUNG_BESCHREIBUNG'
            }).then(() => {   //User confirmed removal
                this.mitteilungRS.sendbetreuungsmitteilung(this.gesuchModelManager.getGesuch().fall,
                    this.mutationsmeldungModel).then((response) => {

                    this.form.$setUntouched();
                    this.form.$setPristine();
                    // reset values. is needed??????
                    this.isMutationsmeldungStatus = false;
                    this.mutationsmeldungModel = undefined;
                    this.$state.go('gesuch.betreuungen', {gesuchId: this.getGesuchId()});
                });
            });
        }
    }

    /**
     * Prueft dass das Objekt existingMutationsMeldung existiert und dass es ein sentDatum hat. Das wird gebraucht,
     * um zu vermeiden, dass ein leeres Objekt als gueltiges Objekt erkannt wird
     */
    public showExistingBetreuungsmitteilungInfoBox(): boolean {
        return this.existingMutationsMeldung !== undefined && this.existingMutationsMeldung !== null
            && this.existingMutationsMeldung.sentDatum !== undefined && this.existingMutationsMeldung.sentDatum !== null
            && this.existingMutationsMeldung.applied !== true;
    }

    public getDatumLastBetreuungsmitteilung(): string {
        if (this.showExistingBetreuungsmitteilungInfoBox()) {
            return DateUtil.momentToLocalDateFormat(this.existingMutationsMeldung.sentDatum, 'DD.MM.YYYY');
        }
        return '';
    }

    public openExistingBetreuungsmitteilung(): void {
        this.$state.go('gesuch.mitteilung', {
            fallId: this.gesuchModelManager.getGesuch().fall.id,
            betreuungId: this.getBetreuungModel().id
        });
    }

    private findExistingBetreuungsmitteilung() {
        this.mitteilungRS.getNewestBetreuungsmitteilung(this.getBetreuungModel().id).then((response: TSBetreuungsmitteilung) => {
            this.existingMutationsMeldung = response;
        });
    }
}
