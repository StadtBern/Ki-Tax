import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {TSRole} from '../../models/enums/TSRole';
import {TSWizardStepName, getTSWizardStepNameValues} from '../../models/enums/TSWizardStepName';
import TSWizardStep from '../../models/TSWizardStep';
import WizardStepRS from './WizardStepRS.rest';
import {TSWizardStepStatus} from '../../models/enums/TSWizardStepStatus';
import {TSAntragTyp} from '../../models/enums/TSAntragTyp';
import {TSAntragStatus} from '../../models/enums/TSAntragStatus';
import TSGesuch from '../../models/TSGesuch';
import IPromise = angular.IPromise;
import IQService = angular.IQService;
import {TSRoleUtil} from '../../utils/TSRoleUtil';

export default class WizardStepManager {

    private allowedSteps: Array<TSWizardStepName> = [];
    private hiddenSteps: Array<TSWizardStepName> = []; // alle Steps die obwohl allowed, ausgeblendet werden muessen
    private wizardSteps: Array<TSWizardStep> = [];
    private currentStepName: TSWizardStepName; // keeps track of the name of the current step

    private wizardStepsSnapshot: Array<TSWizardStep> = [];


    static $inject = ['AuthServiceRS', 'WizardStepRS', '$q'];
    /* @ngInject */
    constructor(private authServiceRS: AuthServiceRS, private wizardStepRS: WizardStepRS, private $q: IQService) {
        this.setAllowedStepsForRole(authServiceRS.getPrincipalRole());
    }

    public getCurrentStep(): TSWizardStep {
        return this.getStepByName(this.currentStepName);
    }

    public setCurrentStep(stepName: TSWizardStepName): void {
        this.currentStepName = stepName;
    }

    public getCurrentStepName(): TSWizardStepName {
        return this.currentStepName;
    }

    /**
     * Initializes WizardSteps with one single Step GESUCH_ERSTELLEN which status is IN_BEARBEITUNG.
     * This method must be called only when the Gesuch doesn't exist yet.
     */
    public initWizardSteps() {
        this.wizardSteps = [new TSWizardStep(undefined, TSWizardStepName.GESUCH_ERSTELLEN, TSWizardStepStatus.IN_BEARBEITUNG, undefined, true)];
        this.wizardSteps.push(new TSWizardStep(undefined, TSWizardStepName.FAMILIENSITUATION, TSWizardStepStatus.UNBESUCHT, 'init dummy', false));
        this.currentStepName = TSWizardStepName.GESUCH_ERSTELLEN;
    }

    public getAllowedSteps(): Array<TSWizardStepName> {
        return this.allowedSteps;
    }

    public getWizardSteps(): Array<TSWizardStep> {
        return this.wizardSteps;
    }

    public getVisibleSteps(): Array<TSWizardStepName> {
        return this.allowedSteps.filter(element =>
            !this.isStepHidden(element)
        );
    }

    public setAllowedStepsForRole(role: TSRole): void {
        if (TSRoleUtil.getTraegerschaftInstitutionRoles().indexOf(role) > -1) {
            this.setAllowedStepsForInstitutionTraegerschaft();
        } else {
            this.setAllAllowedSteps();
        }
    }

    private setAllowedStepsForInstitutionTraegerschaft(): void {
        this.allowedSteps = [];
        this.allowedSteps.push(TSWizardStepName.FAMILIENSITUATION);
        this.allowedSteps.push(TSWizardStepName.GESUCHSTELLER);
        this.allowedSteps.push(TSWizardStepName.UMZUG);
        this.allowedSteps.push(TSWizardStepName.BETREUUNG);
        this.allowedSteps.push(TSWizardStepName.ABWESENHEIT);
        this.allowedSteps.push(TSWizardStepName.VERFUEGEN);
    }

    private setAllAllowedSteps(): void {
        this.allowedSteps = getTSWizardStepNameValues();
    }

    /**
     * Sollten keine WizardSteps gefunden werden, wird die Methode initWizardSteps aufgerufen, um die
     * minimale Steps herzustellen. Die erlaubten Steps fuer den aktuellen Benutzer werden auch gesetzt
     * @param gesuchId
     * @returns {IPromise<void>}
     */
    public findStepsFromGesuch(gesuchId: string): IPromise<void> {
        return this.wizardStepRS.findWizardStepsFromGesuch(gesuchId).then((response: Array<any>) => {
            if (response != null && response.length > 0) {
                this.wizardSteps = response;
            } else {
                this.initWizardSteps();
            }
            this.backupCurrentSteps();
            this.setAllowedStepsForRole(this.authServiceRS.getPrincipalRole());
        });
    }

    public getStepByName(stepName: TSWizardStepName): TSWizardStep {
        return this.wizardSteps.filter((step: TSWizardStep) => {
            return step.wizardStepName === stepName;
        })[0];
    }

    /**
     * Der Step wird aktualisiert und die Liste von Steps wird nochmal aus dem Server geholt. Sollte der Status gleich sein,
     * wird nichts gemacht und undefined wird zurueckgegeben. Der Status wird auch auf verfuegbar gesetzt
     * @param stepName
     * @param newStepStatus
     * @returns {any}
     */
    private updateWizardStepStatus(stepName: TSWizardStepName, newStepStatus: TSWizardStepStatus): IPromise<void> {
        let step: TSWizardStep = this.getStepByName(stepName);
        step.verfuegbar = true;
        if (this.needNewStatusSave(step.wizardStepStatus, newStepStatus)) { // nur wenn der Status sich geaendert hat updaten und steps laden
            step.wizardStepStatus = newStepStatus;
            return this.wizardStepRS.updateWizardStep(step).then((response: TSWizardStep) => {
                return this.findStepsFromGesuch(response.gesuchId);
            });
        }
        return this.$q.when();
    }

    private needNewStatusSave(oldStepStatus: TSWizardStepStatus, newStepStatus: TSWizardStepStatus) {
        if (oldStepStatus === newStepStatus) {
            return false;
        }

        if ((newStepStatus === TSWizardStepStatus.IN_BEARBEITUNG || newStepStatus === TSWizardStepStatus.WARTEN)
            && oldStepStatus !== TSWizardStepStatus.UNBESUCHT) {
            return false;
        }

        if (newStepStatus === TSWizardStepStatus.OK && oldStepStatus === TSWizardStepStatus.MUTIERT) {
            return false;
        }

        return true;
    }

    /**
     * Der aktuelle Step wird aktualisiert und die Liste von Steps wird nochmal aus dem Server geholt. Sollte der Status gleich sein,
     * nichts wird gemacht und undefined wird zurueckgegeben.
     * @param stepStatus
     * @returns {IPromise<void>}
     */
    public updateCurrentWizardStepStatus(stepStatus: TSWizardStepStatus): IPromise<void> {
        return this.updateWizardStepStatus(this.currentStepName, stepStatus);
    }

    /**
     * Just updates the current step as is
     * @returns {IPromise<void>}
     */
    public updateCurrentWizardStep(): IPromise<void> {
        return this.wizardStepRS.updateWizardStep(this.getCurrentStep()).then((response: TSWizardStep) => {
            return this.findStepsFromGesuch(response.gesuchId);
        });
    }


    /**
     * Diese Methode ist eine Ausnahme. Im ersten Step haben wir das Problem, dass das Gesuch noch nicht existiert. Deswegen koennen
     * wir die Kommentare nicht direkt speichern. Die Loesung ist: nach dem das Gesuch erstellt wird und somit auch die WizardSteps,
     * holen wir diese aus der Datenbank, aktualisieren den Step GESUCH_ERSTELLEN mit den Kommentaren und speichern dieses nochmal.
     * @param gesuchId
     * @returns {IPromise<void>}
     */
    public updateFirstWizardStep(gesuchId: string): IPromise<void> {
        let firstStepBemerkungen = angular.copy(this.getCurrentStep().bemerkungen);
        return this.findStepsFromGesuch(gesuchId).then(() => {
            this.getCurrentStep().bemerkungen = firstStepBemerkungen;
            return this.updateCurrentWizardStep();
        });
    }

    /**
     * Gibt true zurueck wenn der Status vom naechsten Step != UNBESUCHT ist. D.h. wenn es verfuegbar ist
     * @returns {boolean}
     */
    public isNextStepBesucht(gesuch: TSGesuch): boolean {
        return this.getStepByName(this.getNextStep(gesuch)).wizardStepStatus !== TSWizardStepStatus.UNBESUCHT;
    }

    /**
     * Gibt true zurueck wenn der naechste Step enabled (verfuegbar) ist
     * @returns {boolean}
     */
    public isNextStepEnabled(gesuch: TSGesuch): boolean {
        return this.isStepAvailableViaBtn(this.getNextStep(gesuch), gesuch);
        // return this.getStepByName(this.getNextStep(gesuch)).verfuegbar;
    }

    public getNextStep(gesuch: TSGesuch): TSWizardStepName {
        let allVisibleStepNames = this.getVisibleSteps();
        let currentPosition: number = allVisibleStepNames.indexOf(this.getCurrentStepName()) + 1;
        for (let i = currentPosition; i < allVisibleStepNames.length; i++) {
            if (this.isStepAvailableViaBtn(allVisibleStepNames[i], gesuch)) {
                return allVisibleStepNames[i];
            }
        }
        return undefined;
    }

    /**
     * iterate through the existing steps and get the previous one based on the current position
     */
    public getPreviousStep(gesuch: TSGesuch): TSWizardStepName {
        var allVisibleStepNames = this.getVisibleSteps();
        let currentPosition: number = allVisibleStepNames.indexOf(this.getCurrentStepName()) - 1;
        for (let i = currentPosition; i >= 0; i--) {
            if (this.isStepAvailableViaBtn(allVisibleStepNames[i], gesuch)) {
                return allVisibleStepNames[i];
            }
        }
        return undefined;
    }

    /**
     * gibt true zurueck wenn step mit next/prev button erreichbar sein soll
     */
    private isStepAvailableViaBtn(stepName: TSWizardStepName, gesuch: TSGesuch): boolean {
        let step: TSWizardStep = this.getStepByName(stepName);
        if (step !== undefined) {
            return (this.isStepClickableForCurrentRole(step, gesuch)
            || (gesuch.typ === TSAntragTyp.GESUCH && step.wizardStepStatus === TSWizardStepStatus.UNBESUCHT)
            || (gesuch.typ === TSAntragTyp.MUTATION && step.wizardStepName === TSWizardStepName.FAMILIENSITUATION));
        }
        return false;  // wenn der step undefined ist geben wir mal verfuegbar zurueck
    }

    /**
     * gibt true zurueck wenn eins step fuer die aktuelle rolle disabled ist.
     * Wenn es keine sonderregel gibt wird der default der aus dem server empfangen wurde
     * zurueckgegeben
     */
    public isStepClickableForCurrentRole(step: TSWizardStep, gesuch: TSGesuch) {
        if (step.wizardStepName === TSWizardStepName.VERFUEGEN) {
            //verfuegen step fuer alle ausser admin und sachbearbeiter nur verfuegbar wenn status verfuegt
            if (!this.authServiceRS.isOneOfRoles(TSRoleUtil.getAdministratorJugendamtRole())
                && gesuch.status !== TSAntragStatus.VERFUEGT) {
                return false;    //disabled
            }
        }
        return step.verfuegbar === true;  //wenn keine Sonderbedingung gehen wir davon aus dass der step nicht disabled ist
    }

    /**
     * Gibt true zurueck, nur wenn alle Steps den Status OK haben.
     *  - Dokumente duerfen allerdings IN_BEARBEITUNG sein
     *  - Bei BETREUUNGEN darf es WARTEN sein
     *  - Der Status von VERFUEGEN wird gar nicht beruecksichtigt
     */
    public areAllStepsOK(): boolean {
        for (let i = 0; i < this.wizardSteps.length; i++) {
            if (this.wizardSteps[i].wizardStepName === TSWizardStepName.BETREUUNG) {
                if (!this.isStatusOk(this.wizardSteps[i].wizardStepStatus)
                    && this.wizardSteps[i].wizardStepStatus !== TSWizardStepStatus.PLATZBESTAETIGUNG) {
                    return false;
                }

            } else if (this.wizardSteps[i].wizardStepName === TSWizardStepName.DOKUMENTE) {
                if (this.wizardSteps[i].wizardStepStatus === TSWizardStepStatus.NOK) {
                    return false;
                }

            } else if (this.wizardSteps[i].wizardStepName !== TSWizardStepName.VERFUEGEN
                && this.wizardSteps[i].wizardStepName !== TSWizardStepName.ABWESENHEIT
                && this.wizardSteps[i].wizardStepName !== TSWizardStepName.UMZUG
                && !this.isStatusOk(this.wizardSteps[i].wizardStepStatus)) {
                return false;
            }
        }
        return true;
    }

    private isStatusOk(wizardStepStatus: TSWizardStepStatus) {
        return wizardStepStatus === TSWizardStepStatus.OK || wizardStepStatus === TSWizardStepStatus.MUTIERT;
    }

    /**
     * Prueft fuer den gegebenen Step ob sein Status OK oder MUTIERT ist
     */
    public isStepStatusOk(wizardStepName: TSWizardStepName) {
        return this.hasStepGivenStatus(wizardStepName, TSWizardStepStatus.OK)
        || this.hasStepGivenStatus(wizardStepName, TSWizardStepStatus.MUTIERT);
    }

    /**
     * Gibt true zurueck wenn der Step existiert und sein Status OK ist
     * @param stepName
     * @param status
     * @returns {boolean}
     */
    public hasStepGivenStatus(stepName: TSWizardStepName, status: TSWizardStepStatus): boolean {
        if (this.getStepByName(stepName)) {
            return this.getStepByName(stepName).wizardStepStatus === status;
        }
        return false;
    }

    public backupCurrentSteps(): void {
        this.wizardStepsSnapshot = angular.copy(this.wizardSteps);
    }

    public restorePreviousSteps(): void {
        this.wizardSteps = this.wizardStepsSnapshot;
    }

    /**
     * Guckt zuerst dass der Step in der Liste von allowedSteps ist. wenn ja wird es geguckt
     * ob der Step in derl Liste hiddenSteps ist.
     * allowed und nicht hidden Steps -> true
     * alle anderen -> false
     */
    public isStepVisible(stepName: TSWizardStepName): boolean {
        return (this.allowedSteps.indexOf(stepName) >= 0 && !this.isStepHidden(stepName));
    }

    public hideStep(stepName: TSWizardStepName): void {
        if (!this.isStepHidden(stepName)) {
            this.hiddenSteps.push(stepName);
        }
    }

    /**
     * Obwohl das Wort unhide nicht existiert, finde ich den Begriff ausfuehrlicher fuer diesen Fall als show
     */
    public unhideStep(stepName: TSWizardStepName): void {
        if (this.isStepHidden(stepName)) {
            this.hiddenSteps.splice(this.hiddenSteps.indexOf(stepName), 1);
        }
    }

    private isStepHidden(stepName: TSWizardStepName): boolean {
       return this.hiddenSteps.indexOf(stepName) >= 0;
    }
}
