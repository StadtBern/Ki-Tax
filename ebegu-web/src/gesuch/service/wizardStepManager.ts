import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {TSRole} from '../../models/enums/TSRole';
import {TSWizardStepName, getTSWizardStepNameValues} from '../../models/enums/TSWizardStepName';
import TSWizardStep from '../../models/TSWizardStep';
import WizardStepRS from './WizardStepRS.rest';
import {TSWizardStepStatus} from '../../models/enums/TSWizardStepStatus';

export default class WizardStepManager {

    private allowedSteps: Array<TSWizardStepName> = [];
    private wizardSteps: Array<TSWizardStep> = [];


    static $inject = ['AuthServiceRS', 'WizardStepRS'];
    /* @ngInject */
    constructor(private authServiceRS: AuthServiceRS, private wizardStepRS: WizardStepRS) {
        this.setAllowedStepsForRole(authServiceRS.getPrincipalRole());
    }

    /**
     * Initializes WizardSteps with one single Step GESUCH_ERSTELLEN which status is IN_BEARBEITUNG.
     * This method must be called only when the Gesuch doesn't exist yet.
     */
    public initWizardSteps() {
        this.wizardSteps = [new TSWizardStep(undefined, TSWizardStepName.GESUCH_ERSTELLEN, TSWizardStepStatus.IN_BEARBEITUNG, undefined)];
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

    public findStepsFromGesuch(gesuchId: string) {
        console.log('11111111111111111111111111111:::', gesuchId);
        this.wizardStepRS.findWizardStepsFromGesuch(gesuchId).then((response) => {
            console.log('222222222222222222222222222:::', response);
            this.wizardSteps = response;
        });
    }

}
