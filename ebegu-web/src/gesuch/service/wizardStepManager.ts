import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {TSRole} from '../../models/enums/TSRole';
import {TSWizardStepName, getTSWizardStepNameValues} from '../../models/enums/TSWizardStepName';
import TSWizardStep from '../../models/TSWizardStep';
import WizardStepRS from './WizardStepRS.rest';

export default class WizardStepManager {

    private allowedSteps: Array<TSWizardStepName> = [];
    private wizardSteps: Array<TSWizardStep> = [];


    static $inject = ['AuthServiceRS', 'WizardStepRS'];
    /* @ngInject */
    constructor(private authServiceRS: AuthServiceRS, private wizardStepRS: WizardStepRS) {
        this.setAllowedStepsForRole(authServiceRS.getPrincipalRole());
    }

    public getAllowedSteps(): Array<TSWizardStepName> {
        return this.allowedSteps;
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
}
