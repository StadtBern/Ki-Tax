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
import Moment = moment.Moment;
import IScope = angular.IScope;
let template = require('./betreuungView.html');
require('./betreuungView.less');

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

    static $inject = ['$state', 'GesuchModelManager', 'EbeguUtil', 'CONSTANTS', '$scope', 'BerechnungsManager', 'ErrorService',
        'AuthServiceRS', 'WizardStepManager', '$stateParams'];
    /* @ngInject */
    constructor(private $state: IStateService, gesuchModelManager: GesuchModelManager, private ebeguUtil: EbeguUtil, private CONSTANTS: any,
                $scope: IScope, berechnungsManager: BerechnungsManager, private errorService: ErrorService,
                private authServiceRS: AuthServiceRS, wizardStepManager: WizardStepManager, $stateParams: IBetreuungStateParams) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.BETREUUNG);
        this.gesuchModelManager.setKindNumber(parseInt($stateParams.kindNumber, 10));
        this.gesuchModelManager.setBetreuungNumber(parseInt($stateParams.betreuungNumber, 10));

        this.model = angular.copy(this.gesuchModelManager.getBetreuungToWorkWith());
        this.initialBetreuung = angular.copy(this.gesuchModelManager.getBetreuungToWorkWith());
        this.setBetreuungsangebotTypValues();
        this.betreuungsangebot = undefined;
        this.initViewModel();


        // just to read!
        this.kindModel = this.gesuchModelManager.getKindToWorkWith();
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
        return this.model;
    }

    public changedAngebot() {
        if (this.getBetreuungModel()) {
            if (this.isTagesschule()) {
                this.getBetreuungModel().betreuungsstatus = TSBetreuungsstatus.SCHULAMT;
            } else {
                this.getBetreuungModel().betreuungsstatus = TSBetreuungsstatus.AUSSTEHEND;
            }
        }
    }

    private save(newStatus: TSBetreuungsstatus, nextStep: string): void {
        this.isSavingData = true;
        this.gesuchModelManager.setBetreuungToWorkWith(this.model); //setze model
        let oldStatus: TSBetreuungsstatus = this.gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus;
        if (this.getBetreuungModel()) {
            if (this.isTagesschule()) {
                this.getBetreuungModel().betreuungspensumContainers = []; // fuer Tagesschule werden keine Betreuungspensum benoetigt, deswegen löschen wir sie vor dem Speichern
            }
        }
        this.errorService.clearAll();
        this.gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus = newStatus;
        this.gesuchModelManager.updateBetreuung(false).then((betreuungResponse: any) => {
            this.isSavingData = false;
            this.form.$setPristine();
            this.$state.go(nextStep);
        }).catch((exception) => {
            //todo team Fehler anzeigen
            // starting over
            console.log('there was an error saving the betreuung ', this.gesuchModelManager.getBetreuungToWorkWith());
            this.isSavingData = false;
            this.gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus = oldStatus;
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
        this.$state.go('gesuch.betreuungen');
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
        //todo kann entfernt werden sobald f5 auf dieser seite funktioniert
        if (!this.getBetreuungModel()) {
            this.errorService.addMesageAsError('Betreuungsmodel ist nicht korrekt initialisiert. Die Seite unterstuetzt noch keine direktnavigation');
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
            this.save(TSBetreuungsstatus.WARTEN, 'gesuch.betreuungen');
        } else if (this.getBetreuungModel().vertrag !== true) {
            this.flagErrorVertrag = true;
        }
    }

    public platzBestaetigen(): void {
        if (this.isGesuchValid()) {
            this.getBetreuungModel().datumBestaetigung = DateUtil.today();
            this.save(TSBetreuungsstatus.BESTAETIGT, 'pendenzenInstitution');
        }
    }

    /**
     * Wenn ein Betreuungsangebot abgewiesen wird, muss man die neu eingegebenen Betreuungspensen zuruecksetzen, da sie nicht relevant sind.
     * Allerdings muessen der Grund und das Datum der Ablehnung doch gespeichert werden.
     * In diesem Fall machen wir keine Validierung weil die Daten die eingegeben werden muessen, direkt auf dem Server gecheckt werden
     * @param form
     */
    public platzAbweisen(): void {
        //copy values modified by the Institution in initialBetreuung
        this.initialBetreuung.erweiterteBeduerfnisse = this.getBetreuungModel().erweiterteBeduerfnisse;
        this.initialBetreuung.grundAblehnung = this.getBetreuungModel().grundAblehnung;
        //restore initialBetreuung
        this.model = angular.copy(this.initialBetreuung);
        this.model.datumAblehnung = DateUtil.today();
        this.save(TSBetreuungsstatus.ABGEWIESEN, 'pendenzenInstitution');
    }

    public platzNichtEingetreten(): void {
        if (this.isGesuchValid()) {
            this.getBetreuungModel().datumBestaetigung = DateUtil.today();

            for (let i: number = 0; i < this.getBetreuungspensen().length; i++) {
                this.getBetreuungspensum(i).betreuungspensumJA.pensum = 0;
                this.getBetreuungspensum(i).betreuungspensumJA.nichtEingetreten = true;
            }
            this.getBetreuungModel().erweiterteBeduerfnisse = false;

            this.save(TSBetreuungsstatus.NICHT_EINGETRETEN, 'pendenzenInstitution');
        }
    }

    public saveSchulamt(): void {
        if (this.isGesuchValid()) {
            this.save(TSBetreuungsstatus.SCHULAMT, 'gesuch.betreuungen');
        }
    }

    /**
     * Returns true when the user is allowed to edit the content. This happens when the status is AUSSTEHEHND or SCHULAMT
     * @returns {boolean}
     */
    public isEnabled(): boolean {
        if (this.getBetreuungModel()) {
            return this.isBetreuungsstatus(TSBetreuungsstatus.AUSSTEHEND)
                || this.isBetreuungsstatus(TSBetreuungsstatus.SCHULAMT);
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
}
