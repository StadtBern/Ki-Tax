import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {TSRole} from '../../models/enums/TSRole';
import {TSWizardStepName, getTSWizardStepNameValues} from '../../models/enums/TSWizardStepName';
import TSWizardStep from '../../models/TSWizardStep';
import WizardStepRS from './WizardStepRS.rest';
import {TSWizardStepStatus} from '../../models/enums/TSWizardStepStatus';
import IPromise = angular.IPromise;

export default class WizardStepManager {

    private allowedSteps: Array<TSWizardStepName> = [];
    private wizardSteps: Array<TSWizardStep> = [];
    private currentStepName: TSWizardStepName; // keeps track of the name of the current step


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

    /**
     * Initializes WizardSteps with one single Step GESUCH_ERSTELLEN which status is IN_BEARBEITUNG.
     * This method must be called only when the Gesuch doesn't exist yet.
     */
    public initWizardSteps() {
        this.wizardSteps = [new TSWizardStep(undefined, TSWizardStepName.GESUCH_ERSTELLEN, TSWizardStepStatus.IN_BEARBEITUNG, undefined, true)];
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
            this.setAllSteps();
        }
    }

    private setAllowedStepsForInstitutionTraegerschaft(): void {
        this.allowedSteps.push(TSWizardStepName.FAMILIENSITUATION);
        this.allowedSteps.push(TSWizardStepName.GESUCHSTELLER);
        this.allowedSteps.push(TSWizardStepName.BETREUUNG);
        this.allowedSteps.push(TSWizardStepName.VERFUEGEN);
    }

    private setAllSteps(): void {
        this.allowedSteps = getTSWizardStepNameValues();
    }

    public findStepsFromGesuch(gesuchId: string): IPromise<void> {
        return this.wizardStepRS.findWizardStepsFromGesuch(gesuchId).then((response) => {
            this.wizardSteps = response;
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
        if (newStepStatus === TSWizardStepStatus.IN_BEARBEITUNG && oldStepStatus !== TSWizardStepStatus.UNBESUCHT) {
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
    public isNextStepAvailable(): boolean {
        return this.getNextStep().wizardStepStatus !== TSWizardStepStatus.UNBESUCHT;
    }

    private getNextStep(): TSWizardStep {
        let nextStepName: TSWizardStepName = TSWizardStepName.GESUCH_ERSTELLEN;
        switch (this.currentStepName) {
            case TSWizardStepName.GESUCH_ERSTELLEN: nextStepName = TSWizardStepName.FAMILIENSITUATION; break;
            case TSWizardStepName.FAMILIENSITUATION: nextStepName = TSWizardStepName.GESUCHSTELLER; break;
            case TSWizardStepName.GESUCHSTELLER: nextStepName = TSWizardStepName.KINDER; break;
            case TSWizardStepName.KINDER: nextStepName = TSWizardStepName.BETREUUNG; break;
            case TSWizardStepName.BETREUUNG: nextStepName = TSWizardStepName.ERWERBSPENSUM; break;
            case TSWizardStepName.ERWERBSPENSUM: nextStepName = TSWizardStepName.FINANZIELLE_SITUATION; break;
            case TSWizardStepName.FINANZIELLE_SITUATION: nextStepName = TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG; break;
            case TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG: nextStepName = TSWizardStepName.DOKUMENTE; break;
            case TSWizardStepName.DOKUMENTE: nextStepName = TSWizardStepName.VERFUEGEN; break;
            case TSWizardStepName.VERFUEGEN: nextStepName = TSWizardStepName.VERFUEGEN;
        }
        return this.getStepByName(nextStepName);
    }
}
