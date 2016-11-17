import {IComponentOptions, IFormController} from 'angular';
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
import {TSRole} from '../../../models/enums/TSRole';
import DateUtil from '../../../utils/DateUtil';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import Moment = moment.Moment;
let template = require('./betreuungView.html');
require('./betreuungView.less');

export class BetreuungViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = BetreuungViewController;
    controllerAs = 'vm';
}

export class BetreuungViewController extends AbstractGesuchViewController {
    betreuungsangebot: any;
    betreuungsangebotValues: Array<any>;
    instStammId: string; //der ausgewaehlte instStammId wird hier gespeichert und dann in die entsprechende InstitutionStammdaten umgewandert
    isSavingData: boolean; // Semaphore
    initialBetreuung: TSBetreuung;
    flagErrorVertrag: boolean;

    static $inject = ['$state', 'GesuchModelManager', 'EbeguUtil', 'CONSTANTS', '$scope', 'BerechnungsManager', 'ErrorService',
        'AuthServiceRS', 'WizardStepManager'];
    /* @ngInject */
    constructor(private $state: IStateService, gesuchModelManager: GesuchModelManager, private ebeguUtil: EbeguUtil, private CONSTANTS: any,
                private $scope: any, berechnungsManager: BerechnungsManager, private errorService: ErrorService,
                private authServiceRS: AuthServiceRS, wizardStepManager: WizardStepManager) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.initialBetreuung = angular.copy(this.getBetreuungModel());
        this.setBetreuungsangebotTypValues();
        this.betreuungsangebot = undefined;
        this.initViewModel();

        //Wir verlassen uns hier darauf, dass zuerst das Popup vom unsavedChanges Plugin kommt welches den user fragt ob er die ungesp. changes verwerfen will
        $scope.$on('$stateChangeStart', (navEvent: any, toState: any, toParams: any, fromState: any, fromParams: any) => {
            // wenn der user die changes verwerfen will koennen wir die view resetten, ansonsten machen wir nichts da wir hier bleiben
            if (navEvent.defaultPrevented !== undefined && navEvent.defaultPrevented === false) {
                this.reset();  //Maske wird verlasse, changes resettten auf letzten Speicherpunkt (wenn gespeichert wurde wird auf gespeicherten punkt resetted)
            }
        });
    }

    private initViewModel() {
        this.gesuchModelManager.initGesuch(false); //wird aufgerufen um einen restorepunkt des aktullen gesuchs zu machen
        this.isSavingData = false;
        this.flagErrorVertrag = false;
        if (this.getInstitutionSD()) {
            this.instStammId = this.getInstitutionSD().id;
            this.betreuungsangebot = this.getBetreuungsangebotFromInstitutionList();
        }
        this.startEmptyListOfBetreuungspensen();
        //institutionen lazy laden
        if (this.gesuchModelManager.getInstitutionenList() || this.gesuchModelManager.getInstitutionenList().length <= 0) {
            this.gesuchModelManager.updateInstitutionenList();
        }
        this.wizardStepManager.setCurrentStep(TSWizardStepName.BETREUUNG);
    }

    /**
     * Fuer Institutionen und Traegerschaften wird es geprueft ob es schon ein Betreuungspensum existiert,
     * wenn nicht wir die Liste dann mit einem leeren initiallisiert
     */
    private startEmptyListOfBetreuungspensen() {
        if ((!this.getBetreuungspensen() || this.getBetreuungspensen().length === 0)
            && (this.authServiceRS.isRole(TSRole.SACHBEARBEITER_INSTITUTION) || this.authServiceRS.isRole(TSRole.SACHBEARBEITER_TRAEGERSCHAFT))) {
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
        return this.gesuchModelManager.getKindToWorkWith();
    }

    public getBetreuungModel(): TSBetreuung {
        return this.gesuchModelManager.getBetreuungToWorkWith();
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

    private save(newStatus: TSBetreuungsstatus, nextStep: string, form: IFormController): void {
        this.isSavingData = true;
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
            form.$setPristine();
            this.$state.go(nextStep);
        }).catch((exception) => {
            //todo team Fehler anzeigen
            // starting over
            console.log('there was an error saving the betreuung ', this.gesuchModelManager.getBetreuungToWorkWith());
            this.isSavingData = false;
            this.gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus = oldStatus;
            this.startEmptyListOfBetreuungspensen();
            this.$scope.form.$setUntouched();
            this.$scope.form.$setPristine();
            return undefined;
        });
    }

    private setBetreuungsangebotTypValues(): void {
        this.betreuungsangebotValues = this.ebeguUtil.translateStringList(getTSBetreuungsangebotTypValues());
    }

    public cancel(formCtrl: IFormController) {
        this.reset();
        formCtrl.$setPristine();
        this.$state.go('gesuch.betreuungen');
    }

    reset() {
        this.gesuchModelManager.restoreBackupOfPreviousGesuch();
        this.removeBetreuungFromKind();
    }

    private removeBetreuungFromKind(): void {
        if (this.gesuchModelManager.getBetreuungToWorkWith() && !this.gesuchModelManager.getBetreuungToWorkWith().timestampErstellt) {
            //wenn die Betreeung noch nicht erstellt wurde, loeschen wir die Betreuung vom Array
            this.gesuchModelManager.removeBetreuungFromKind();
        }
    }

    public getInstitutionenSDList(): Array<TSInstitutionStammdaten> {
        let result: Array<TSInstitutionStammdaten> = [];
        if (this.betreuungsangebot) {
            this.gesuchModelManager.getInstitutionenList().forEach((instStamm: TSInstitutionStammdaten) => {
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
        this.getBetreuungspensen().push(new TSBetreuungspensumContainer(undefined, new TSBetreuungspensum(false, undefined, new TSDateRange())));
    }

    public removeBetreuungspensum(betreuungspensumToDelete: TSBetreuungspensumContainer): void {
        let position: number = this.getBetreuungspensen().indexOf(betreuungspensumToDelete);
        if (position > -1) {
            this.getBetreuungspensen().splice(position, 1);
        }
    }

    public setSelectedInstitutionStammdaten(): void {
        let instStamList = this.gesuchModelManager.getInstitutionenList();
        for (let i: number = 0; i < instStamList.length; i++) {
            if (instStamList[i].id === this.instStammId) {
                this.gesuchModelManager.getBetreuungToWorkWith().institutionStammdaten = instStamList[i];
            }
        }
    }

    public platzAnfordern(form: IFormController): void {
        if (form.$valid && this.getBetreuungModel().vertrag === true) {
            this.flagErrorVertrag = false;
            this.save(TSBetreuungsstatus.WARTEN, 'gesuch.betreuungen', form);
        } else if (this.getBetreuungModel().vertrag !== true) {
            this.flagErrorVertrag = true;
        }
    }

    public platzBestaetigen(form: IFormController): void {
        if (form.$valid) {
            this.getBetreuungModel().datumBestaetigung = DateUtil.today();
            this.save(TSBetreuungsstatus.BESTAETIGT, 'pendenzenInstitution', form);
        }
    }

    /**
     * Wenn ein Betreuungsangebot abgewiesen wird, muss man die neu eingegebenen Betreuungspensen zuruecksetzen, da sie nicht relevant sind.
     * Allerdings muessen der Grund und das Datum der Ablehnung doch gespeichert werden.
     * In diesem Fall machen wir keine Validierung weil die Daten die eingegeben werden muessen, direkt auf dem Server gecheckt werden
     * @param form
     */
    public platzAbweisen(form: IFormController): void {
        //copy values modified by the Institution in initialBetreuung
        this.initialBetreuung.erweiterteBeduerfnisse = this.getBetreuungModel().erweiterteBeduerfnisse;
        this.initialBetreuung.grundAblehnung = this.getBetreuungModel().grundAblehnung;
        //restore initialBetreuung
        this.gesuchModelManager.setBetreuungToWorkWith(angular.copy(this.initialBetreuung));
        this.getBetreuungModel().datumAblehnung = DateUtil.today();
        this.save(TSBetreuungsstatus.ABGEWIESEN, 'pendenzenInstitution', form);
    }

    public platzNichtEingetreten(form: IFormController): void {
        if (form.$valid) {
            this.getBetreuungModel().datumBestaetigung = DateUtil.today();

            for (let i: number = 0; i < this.getBetreuungspensen().length; i++) {
                this.getBetreuungspensum(i).betreuungspensumJA.pensum = 0;
                this.getBetreuungspensum(i).betreuungspensumJA.nichtEingetreten = true;
            }
            this.getBetreuungModel().erweiterteBeduerfnisse = false;

            this.save(TSBetreuungsstatus.NICHT_EINGETRETEN, 'pendenzenInstitution', form);
        }
    }

    public saveSchulamt(form: IFormController): void {
        if (form.$valid) {
            this.save(TSBetreuungsstatus.SCHULAMT, 'gesuch.betreuungen', form);
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
        return TSRole.SACHBEARBEITER_INSTITUTION === this.authServiceRS.getPrincipalRole()
            || TSRole.SACHBEARBEITER_TRAEGERSCHAFT === this.authServiceRS.getPrincipalRole()
            || this.getBetreuungModel().erweiterteBeduerfnisse === true;
    }

    public showFalscheAngaben(): boolean {
        return (this.isBetreuungsstatusBestaetigt() || this.isBetreuungsstatusAbgewiesen()) && !this.isGesuchStatusVerfuegenVerfuegt()
            && !this.isFromMutation();
    }

    public showAngabenKorrigieren(): boolean {
        return (this.isBetreuungsstatusBestaetigt() || this.isBetreuungsstatusAbgewiesen()) && !this.isGesuchStatusVerfuegenVerfuegt()
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
            && !this.isGesuchStatusVerfuegenVerfuegt() && this.isFromMutation();
    }
}
