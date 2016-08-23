import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {TSRole} from '../../models/enums/TSRole';
import WizardStepManager from './wizardStepManager';
import {EbeguWebCore} from '../../core/core.module';
import {TSWizardStepName} from '../../models/enums/TSWizardStepName';
import WizardStepRS from './WizardStepRS.rest';
import TSWizardStep from '../../models/TSWizardStep';
import {IScope, IQService} from 'angular';

describe('wizardStepManager', function () {

    let authServiceRS: AuthServiceRS;
    let wizardStepManager: WizardStepManager;
    let wizardStepRS: WizardStepRS;
    let scope: IScope;
    let $q: IQService;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        authServiceRS = $injector.get('AuthServiceRS');
        wizardStepRS = $injector.get('WizardStepRS');
        scope = $injector.get('$rootScope').$new();
        $q = $injector.get('$q');
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
    describe('findStepsFromGesuch', function() {
        it('retrieves the steps from server', function() {
            wizardStepManager = new WizardStepManager(authServiceRS, wizardStepRS);
            let step: TSWizardStep = new TSWizardStep();
            step.bemerkungen = 'step1';
            let steps: TSWizardStep[] = [step];
            spyOn(wizardStepRS, 'findWizardStepsFromGesuch').and.returnValue($q.when(steps));

            wizardStepManager.findStepsFromGesuch('123');
            scope.$apply();

            expect(wizardStepRS.findWizardStepsFromGesuch).toHaveBeenCalledWith('123');
            expect(wizardStepManager.getWizardSteps()).toBeDefined();
            expect(wizardStepManager.getWizardSteps().length).toBe(1);
            expect(wizardStepManager.getWizardSteps()[0]).toBe(step);
        });
    });
});
