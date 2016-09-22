import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {TSRole} from '../../models/enums/TSRole';
import WizardStepManager from './wizardStepManager';
import {EbeguWebCore} from '../../core/core.module';
import {TSWizardStepName} from '../../models/enums/TSWizardStepName';
import WizardStepRS from './WizardStepRS.rest';
import TSWizardStep from '../../models/TSWizardStep';
import {IScope, IQService} from 'angular';
import {TSWizardStepStatus} from '../../models/enums/TSWizardStepStatus';

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
        wizardStepManager = $injector.get('WizardStepManager');
    }));

    describe('construct the object', function() {
        it('constructs the steps for Institution', function() {
            spyOn(authServiceRS, 'getPrincipalRole').and.returnValue(TSRole.SACHBEARBEITER_INSTITUTION);
            wizardStepManager.getAllowedSteps().splice(0);
            wizardStepManager.setAllowedStepsForRole(TSRole.SACHBEARBEITER_INSTITUTION);
            expect(wizardStepManager.getAllowedSteps()).toBeDefined();
            expect(wizardStepManager.getAllowedSteps().length).toBe(4);
            expect(wizardStepManager.getAllowedSteps()[0]).toBe(TSWizardStepName.FAMILIENSITUATION);
            expect(wizardStepManager.getAllowedSteps()[1]).toBe(TSWizardStepName.GESUCHSTELLER);
            expect(wizardStepManager.getAllowedSteps()[2]).toBe(TSWizardStepName.BETREUUNG);
            expect(wizardStepManager.getAllowedSteps()[3]).toBe(TSWizardStepName.VERFUEGEN);
        });
        it('constructs the steps for JA', function() {
            spyOn(authServiceRS, 'getPrincipalRole').and.returnValue(TSRole.SACHBEARBEITER_JA);
            wizardStepManager.getAllowedSteps().splice(0);
            wizardStepManager.setAllowedStepsForRole(TSRole.SACHBEARBEITER_JA);
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
    describe('isNextStepAvailable', function() {
        it('next step is available because status != UNBESUCHT', function() {
            createTwoSteps(TSWizardStepName.GESUCH_ERSTELLEN, TSWizardStepName.FAMILIENSITUATION, TSWizardStepStatus.OK);
            wizardStepManager.findStepsFromGesuch('123');
            scope.$apply();

            wizardStepManager.setCurrentStep(TSWizardStepName.GESUCH_ERSTELLEN);
            expect(wizardStepManager.isNextStepAvailable()).toBe(true);
        });
    });
    describe('areAllStepsOK', function() {
        it('returns true when all steps are OK', function() {
            createAllSteps(TSWizardStepStatus.OK);
            expect(wizardStepManager.areAllStepsOK()).toBe(true);
        });
        it('returns true when all steps are OK although Betreuung is still PLATZBESTAETIGUNG', function() {
            createAllSteps(TSWizardStepStatus.OK);
            wizardStepManager.setCurrentStep(TSWizardStepName.BETREUUNG);
            wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.PLATZBESTAETIGUNG);
            expect(wizardStepManager.areAllStepsOK()).toBe(true);
        });
        it('returns false when not all steps are OK', function() {
            createAllSteps(TSWizardStepStatus.OK);
            wizardStepManager.setCurrentStep(TSWizardStepName.BETREUUNG);
            wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.NOK);
            expect(wizardStepManager.areAllStepsOK()).toBe(false);
        });
    });
    describe('hasStepGivenStatus', function() {
        it('returns true if the Step has the given status', function() {
            createAllSteps(TSWizardStepStatus.OK);
            expect(wizardStepManager.hasStepGivenStatus(TSWizardStepName.BETREUUNG, TSWizardStepStatus.OK)).toBe(true);
        });
        it('returns false if the Step does not have the given status', function() {
            createAllSteps(TSWizardStepStatus.OK);
            expect(wizardStepManager.hasStepGivenStatus(TSWizardStepName.BETREUUNG, TSWizardStepStatus.NOK)).toBe(false);
        });
        it('returns false if the Step does not exist', function() {
            wizardStepManager.getWizardSteps().splice(0, wizardStepManager.getWizardSteps().length);
            wizardStepManager.getWizardSteps().push(new TSWizardStep('', TSWizardStepName.GESUCH_ERSTELLEN, TSWizardStepStatus.OK, '', true));
            expect(wizardStepManager.hasStepGivenStatus(TSWizardStepName.BETREUUNG, TSWizardStepStatus.NOK)).toBe(false);
        });
    });


    function createTwoSteps(name1: TSWizardStepName, name2: TSWizardStepName, status: TSWizardStepStatus) {
        let step: TSWizardStep = new TSWizardStep();
        step.wizardStepName = name1;
        let step2: TSWizardStep = new TSWizardStep();
        step2.wizardStepName = name2;
        step2.wizardStepStatus = status;
        let steps: TSWizardStep[] = [step, step2];
        spyOn(wizardStepRS, 'findWizardStepsFromGesuch').and.returnValue($q.when(steps));
    }

    function createAllSteps(status: TSWizardStepStatus): void {
        wizardStepManager.getWizardSteps().splice(0, wizardStepManager.getWizardSteps().length);
        wizardStepManager.getWizardSteps().push(new TSWizardStep('', TSWizardStepName.GESUCH_ERSTELLEN, status, '', true));
        wizardStepManager.getWizardSteps().push(new TSWizardStep('', TSWizardStepName.FAMILIENSITUATION, status, '', true));
        wizardStepManager.getWizardSteps().push(new TSWizardStep('', TSWizardStepName.GESUCHSTELLER, status, '', true));
        wizardStepManager.getWizardSteps().push(new TSWizardStep('', TSWizardStepName.KINDER, status, '', true));
        wizardStepManager.getWizardSteps().push(new TSWizardStep('', TSWizardStepName.BETREUUNG, status, '', true));
        wizardStepManager.getWizardSteps().push(new TSWizardStep('', TSWizardStepName.ERWERBSPENSUM, status, '', true));
        wizardStepManager.getWizardSteps().push(new TSWizardStep('', TSWizardStepName.FINANZIELLE_SITUATION, status, '', true));
        wizardStepManager.getWizardSteps().push(new TSWizardStep('', TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG, status, '', true));
        wizardStepManager.getWizardSteps().push(new TSWizardStep('', TSWizardStepName.DOKUMENTE, status, '', true));
        wizardStepManager.getWizardSteps().push(new TSWizardStep('', TSWizardStepName.VERFUEGEN, status, '', true));
    }
});
