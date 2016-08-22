import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {TSRole} from '../../models/enums/TSRole';
import WizardStepManager from './wizardStepManager';
import {EbeguWebCore} from '../../core/core.module';
import {TSWizardStepName} from '../../models/enums/TSWizardStepName';
import WizardStepRS from './WizardStepRS.rest';

describe('wizardStepManager', function () {

    let authServiceRS: AuthServiceRS;
    let wizardStepManager: WizardStepManager;
    let wizardStepRS: WizardStepRS;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        authServiceRS = $injector.get('AuthServiceRS');
        wizardStepRS = $injector.get('WizardStepRS');
    }));

    describe('construct the object', function() {
        it('constructs the steps for Institution', function() {
            spyOn(authServiceRS, 'getPrincipalRole').and.returnValue(TSRole.SACHBEARBEITER_INSTITUTION);
            wizardStepManager = new WizardStepManager(authServiceRS, wizardStepRS);
            expect(wizardStepManager.getAllowedSteps()).toBeDefined();
            expect(wizardStepManager.getAllowedSteps().length).toBe(4);
            expect(wizardStepManager.getAllowedSteps()[0]).toBe(TSWizardStepName.FAMILIENSITUATION);
            expect(wizardStepManager.getAllowedSteps()[1]).toBe(TSWizardStepName.GESUCHSTELLER);
            expect(wizardStepManager.getAllowedSteps()[2]).toBe(TSWizardStepName.BETREUUNG);
            expect(wizardStepManager.getAllowedSteps()[3]).toBe(TSWizardStepName.VERFUEGEN);
        });
        it('constructs the steps for JA', function() {
            spyOn(authServiceRS, 'getPrincipalRole').and.returnValue(TSRole.SACHBEARBEITER_JA);
            wizardStepManager = new WizardStepManager(authServiceRS, wizardStepRS);
            expect(wizardStepManager.getAllowedSteps().length).toBe(10);
            expect(wizardStepManager.getAllowedSteps()[0]).toBe(TSWizardStepName.GESUCH_ERSTELLEN);
            expect(wizardStepManager.getAllowedSteps()[1]).toBe(TSWizardStepName.FAMILIENSITUATION);
            expect(wizardStepManager.getAllowedSteps()[2]).toBe(TSWizardStepName.GESUCHSTELLER);
            expect(wizardStepManager.getAllowedSteps()[3]).toBe(TSWizardStepName.KINDER);
            expect(wizardStepManager.getAllowedSteps()[4]).toBe(TSWizardStepName.BETREUUNG);
            expect(wizardStepManager.getAllowedSteps()[5]).toBe(TSWizardStepName.ERWERBSPENSUM);
            expect(wizardStepManager.getAllowedSteps()[6]).toBe(TSWizardStepName.FINANZIELLE_SITUATION);
            expect(wizardStepManager.getAllowedSteps()[7]).toBe(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
            expect(wizardStepManager.getAllowedSteps()[8]).toBe(TSWizardStepName.DOKUMENTE);
            expect(wizardStepManager.getAllowedSteps()[9]).toBe(TSWizardStepName.VERFUEGEN);
        });
    });
});
