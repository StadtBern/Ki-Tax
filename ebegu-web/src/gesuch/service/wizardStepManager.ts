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

export default class WizardStepManager {

    private allowedSteps: Array<TSWizardStepName> = [];
    private wizardSteps: Array<TSWizardStep> = [];
    private currentStepName: TSWizardStepName; // keeps track of the name of the current step

    private wizardStepsSnapshot: Array<TSWizardStep> = [];


    static $inject = ['AuthServiceRS', 'WizardStepRS'];
    /* @ngInject */
    constructor(private authServiceRS: AuthServiceRS, private wizardStepRS: WizardStepRS) {
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

    public setAllowedStepsForRole(role: TSRole): void {
        if (TSRole.SACHBEARBEITER_INSTITUTION === role || TSRole.SACHBEARBEITER_TRAEGERSCHAFT === role) {
            this.setAllowedStepsForInstitutionTraegerschaft();
        } else {
            this.setAllAllowedSteps();
        }
    }

    private setAllowedStepsForInstitutionTraegerschaft(): void {
        this.allowedSteps = [];
        this.allowedSteps.push(TSWizardStepName.FAMILIENSITUATION);
        this.allowedSteps.push(TSWizardStepName.GESUCHSTELLER);
        this.allowedSteps.push(TSWizardStepName.BETREUUNG);
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
     * @param stepStatus
     * @returns {any}
     */
    private updateWizardStepStatus(stepName: TSWizardStepName, stepStatus: TSWizardStepStatus): IPromise<void> {
        let step: TSWizardStep = this.getStepByName(stepName);
        step.verfuegbar = true;
        step.wizardStepStatus = this.maybeChangeStatus(step.wizardStepStatus, stepStatus);
        if (step.wizardStepStatus === stepStatus) { // nur wenn der Status sich geaendert hat updaten und steps laden
            return this.wizardStepRS.updateWizardStep(step).then((response: TSWizardStep) => {
                return this.findStepsFromGesuch(response.gesuchId);
            });
        }
        return undefined;
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
     * Den Status einer Seite setzen auf newStepStatus wenn dies erlaubt ist. Wenn nicht kommt der alte zurueck.
     * Im normalen Fall wird diese Methode gebraucht um von UNBESUCHT auf IN_BEARBEITUNG zu wechseln.
     *
     * @param oldStepStatus
     * @param newStepStatus
     * @returns {TSWizardStepStatus}
     */
    private maybeChangeStatus(oldStepStatus: TSWizardStepStatus, newStepStatus: TSWizardStepStatus): TSWizardStepStatus {
        //wenn wir vorher auf was anderem sind als unbesucht dann bleiben wir da statt auf IN_BEARBEITUNG zu gehen.
        if ((newStepStatus === TSWizardStepStatus.IN_BEARBEITUNG || newStepStatus === TSWizardStepStatus.WARTEN)
            && oldStepStatus !== TSWizardStepStatus.UNBESUCHT) {
            return oldStepStatus;
        }
        return newStepStatus;
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
        let allStepNames = this.getAllowedSteps();
        let currentPosition: number = allStepNames.indexOf(this.getCurrentStepName()) + 1;
        for (let i = currentPosition; i < allStepNames.length; i++) {
            if (this.isStepAvailableViaBtn(allStepNames[i], gesuch)) {
                return allStepNames[i];
            }
        }
        return undefined;
    }

    /**
     * iterate through the existing steps and get the previous one based on the current position
     */
    public getPreviousStep(gesuch: TSGesuch): TSWizardStepName {
        var allStepNames = this.getAllowedSteps();
        let currentPosition: number = allStepNames.indexOf(this.getCurrentStepName()) - 1;
        for (let i = currentPosition; i >= 0; i--) {
            if (this.isStepAvailableViaBtn(allStepNames[i], gesuch)) {
                return allStepNames[i];
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
            if (!this.authServiceRS.isOneOfRoles(TSRole.ADMIN, TSRole.SACHBEARBEITER_JA)
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
                if (this.wizardSteps[i].wizardStepStatus !== TSWizardStepStatus.OK
                    && this.wizardSteps[i].wizardStepStatus !== TSWizardStepStatus.PLATZBESTAETIGUNG) {
                    return false;
                }

            } else if (this.wizardSteps[i].wizardStepName === TSWizardStepName.DOKUMENTE) {
                if (this.wizardSteps[i].wizardStepStatus === TSWizardStepStatus.NOK) {
                    return false;
                }

            } else if (this.wizardSteps[i].wizardStepName !== TSWizardStepName.VERFUEGEN
                && this.wizardSteps[i].wizardStepStatus !== TSWizardStepStatus.OK) {
                return false;
            }
        }
        return true;
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
}
