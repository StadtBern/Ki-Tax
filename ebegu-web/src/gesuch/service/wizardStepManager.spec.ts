import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {TSRole} from '../../models/enums/TSRole';
import WizardStepManager from './wizardStepManager';
import {EbeguWebCore} from '../../core/core.module';
import {TSWizardStepName} from '../../models/enums/TSWizardStepName';
import WizardStepRS from './WizardStepRS.rest';
import TSWizardStep from '../../models/TSWizardStep';
import {IScope, IQService} from 'angular';
import {TSWizardStepStatus} from '../../models/enums/TSWizardStepStatus';
import {TSAntragTyp} from '../../models/enums/TSAntragTyp';
import TSGesuch from '../../models/TSGesuch';
import {TSAntragStatus} from '../../models/enums/TSAntragStatus';

describe('wizardStepManager', function () {

    let authServiceRS: AuthServiceRS;
    let wizardStepManager: WizardStepManager;
    let wizardStepRS: WizardStepRS;
    let scope: IScope;
    let $q: IQService;

    let gesuchAntrag: TSGesuch = new TSGesuch;


    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        authServiceRS = $injector.get('AuthServiceRS');
        wizardStepRS = $injector.get('WizardStepRS');
        scope = $injector.get('$rootScope').$new();
        $q = $injector.get('$q');
        wizardStepManager = $injector.get('WizardStepManager');
    }));

    beforeEach(() => {
        gesuchAntrag.typ = TSAntragTyp.GESUCH;
    });

    describe('construct the object', function() {
        it('constructs the steps for Institution', function() {
            spyOn(authServiceRS, 'getPrincipalRole').and.returnValue(TSRole.SACHBEARBEITER_INSTITUTION);
            wizardStepManager.getAllowedSteps().splice(0);
            wizardStepManager.setAllowedStepsForRole(TSRole.SACHBEARBEITER_INSTITUTION);
            expect(wizardStepManager.getAllowedSteps()).toBeDefined();
            expect(wizardStepManager.getAllowedSteps().length).toBe(5);
            expect(wizardStepManager.getAllowedSteps()[0]).toBe(TSWizardStepName.FAMILIENSITUATION);
            expect(wizardStepManager.getAllowedSteps()[1]).toBe(TSWizardStepName.GESUCHSTELLER);
            expect(wizardStepManager.getAllowedSteps()[2]).toBe(TSWizardStepName.UMZUG);
            expect(wizardStepManager.getAllowedSteps()[3]).toBe(TSWizardStepName.BETREUUNG);
            expect(wizardStepManager.getAllowedSteps()[3]).toBe(TSWizardStepName.ABWESENHEIT);
            expect(wizardStepManager.getAllowedSteps()[4]).toBe(TSWizardStepName.VERFUEGEN);
        });
        it('constructs the steps for JA', function() {
            spyOn(authServiceRS, 'getPrincipalRole').and.returnValue(TSRole.SACHBEARBEITER_JA);
            wizardStepManager.getAllowedSteps().splice(0);
            wizardStepManager.setAllowedStepsForRole(TSRole.SACHBEARBEITER_JA);
            expect(wizardStepManager.getAllowedSteps().length).toBe(11);
            expect(wizardStepManager.getAllowedSteps()[0]).toBe(TSWizardStepName.GESUCH_ERSTELLEN);
            expect(wizardStepManager.getAllowedSteps()[1]).toBe(TSWizardStepName.FAMILIENSITUATION);
            expect(wizardStepManager.getAllowedSteps()[2]).toBe(TSWizardStepName.GESUCHSTELLER);
            expect(wizardStepManager.getAllowedSteps()[3]).toBe(TSWizardStepName.UMZUG);
            expect(wizardStepManager.getAllowedSteps()[4]).toBe(TSWizardStepName.KINDER);
            expect(wizardStepManager.getAllowedSteps()[5]).toBe(TSWizardStepName.BETREUUNG);
            expect(wizardStepManager.getAllowedSteps()[5]).toBe(TSWizardStepName.ABWESENHEIT);
            expect(wizardStepManager.getAllowedSteps()[6]).toBe(TSWizardStepName.ERWERBSPENSUM);
            expect(wizardStepManager.getAllowedSteps()[7]).toBe(TSWizardStepName.FINANZIELLE_SITUATION);
            expect(wizardStepManager.getAllowedSteps()[8]).toBe(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
            expect(wizardStepManager.getAllowedSteps()[9]).toBe(TSWizardStepName.DOKUMENTE);
            expect(wizardStepManager.getAllowedSteps()[10]).toBe(TSWizardStepName.VERFUEGEN);
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
        it('does not find any steps im Server -> minimale steps must be set', function() {
            let steps: TSWizardStep[] = [];
            spyOn(wizardStepRS, 'findWizardStepsFromGesuch').and.returnValue($q.when(steps));

            wizardStepManager.findStepsFromGesuch('123');
            scope.$apply();

            expect(wizardStepRS.findWizardStepsFromGesuch).toHaveBeenCalledWith('123');
            expect(wizardStepManager.getWizardSteps()).toBeDefined();
            expect(wizardStepManager.getWizardSteps().length).toBe(2); //erste 2 states sind definiert
            expect(wizardStepManager.getWizardSteps()[0].wizardStepName).toBe(TSWizardStepName.GESUCH_ERSTELLEN);
            expect(wizardStepManager.getWizardSteps()[0].wizardStepStatus).toBe(TSWizardStepStatus.IN_BEARBEITUNG);
            expect(wizardStepManager.getWizardSteps()[0].verfuegbar).toBe(true);
            expect(wizardStepManager.getCurrentStep()).toBe(wizardStepManager.getWizardSteps()[0]);
        });
    });
    describe('isNextStepBesucht', function() {
        it('next step is available because status != UNBESUCHT', function() {
            createAllSteps(TSWizardStepStatus.OK);
            wizardStepManager.getAllowedSteps().splice(0);
            wizardStepManager.setAllowedStepsForRole(TSRole.SACHBEARBEITER_JA);

            wizardStepManager.setCurrentStep(TSWizardStepName.GESUCH_ERSTELLEN);
            expect(wizardStepManager.isNextStepBesucht(gesuchAntrag)).toBe(true);
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
    describe('getNextStep', function() {
        it('returns ERWERBSPENSUM coming from BETREUUNG for SACHBEARBEITER_JA', function() {
            createAllSteps(TSWizardStepStatus.OK);
            wizardStepManager.setCurrentStep(TSWizardStepName.BETREUUNG);
            wizardStepManager.getAllowedSteps().splice(0);
            wizardStepManager.setAllowedStepsForRole(TSRole.SACHBEARBEITER_JA);
            expect(wizardStepManager.getNextStep(gesuchAntrag)).toBe(TSWizardStepName.ERWERBSPENSUM);
        });
        it('returns VERFUEGEN coming from BETREUUNG for SACHBEARBEITER_INSTITUTION IF VERFUEGT', function() {
            createAllSteps(TSWizardStepStatus.OK);
            gesuchAntrag.status = TSAntragStatus.VERFUEGT;
            wizardStepManager.setCurrentStep(TSWizardStepName.BETREUUNG);
            wizardStepManager.getAllowedSteps().splice(0);
            wizardStepManager.setAllowedStepsForRole(TSRole.SACHBEARBEITER_INSTITUTION);
            expect(wizardStepManager.getNextStep(gesuchAntrag)).toBe(TSWizardStepName.VERFUEGEN);
        });
        it('returns undefined coming from BETREUUNG for SACHBEARBEITER_INSTITUTION IF Not VERFUEGT', function() {
            createAllSteps(TSWizardStepStatus.OK);
            gesuchAntrag.status = TSAntragStatus.IN_BEARBEITUNG_GS;
            wizardStepManager.setCurrentStep(TSWizardStepName.BETREUUNG);
            wizardStepManager.getAllowedSteps().splice(0);
            wizardStepManager.setAllowedStepsForRole(TSRole.SACHBEARBEITER_INSTITUTION);
            expect(wizardStepManager.getNextStep(gesuchAntrag)).toBe(undefined);
        });
    });
    describe('getPreviousStep', function() {
        it('returns BETREUUNG coming from ERWERBSPENSUM for SACHBEARBEITER_JA', function() {
            createAllSteps(TSWizardStepStatus.OK);
            wizardStepManager.setCurrentStep(TSWizardStepName.ERWERBSPENSUM);
            wizardStepManager.getAllowedSteps().splice(0);
            wizardStepManager.setAllowedStepsForRole(TSRole.SACHBEARBEITER_JA);
            expect(wizardStepManager.getPreviousStep(gesuchAntrag)).toBe(TSWizardStepName.BETREUUNG);
        });
        it('returns BETREUUNG coming from VERFUEGEN for SACHBEARBEITER_INSTITUTION', function() {
            createAllSteps(TSWizardStepStatus.OK);
            wizardStepManager.setCurrentStep(TSWizardStepName.VERFUEGEN);
            wizardStepManager.getAllowedSteps().splice(0);
            wizardStepManager.setAllowedStepsForRole(TSRole.SACHBEARBEITER_INSTITUTION);
            expect(wizardStepManager.getPreviousStep(gesuchAntrag)).toBe(TSWizardStepName.BETREUUNG);
        });
    });
    describe('isStepVisible', function() {
        it('returns true for all steps allowed for role and false if the step is hidden', function() {
            createAllSteps(TSWizardStepStatus.OK);
            wizardStepManager.getAllowedSteps().splice(0);
            wizardStepManager.setAllowedStepsForRole(TSRole.SACHBEARBEITER_JA);
            expect(wizardStepManager.isStepVisible(TSWizardStepName.GESUCH_ERSTELLEN)).toBe(true);

            wizardStepManager.hideStep(TSWizardStepName.GESUCH_ERSTELLEN);
            expect(wizardStepManager.isStepVisible(TSWizardStepName.GESUCH_ERSTELLEN)).toBe(false);

            wizardStepManager.unhideStep(TSWizardStepName.GESUCH_ERSTELLEN);
            expect(wizardStepManager.isStepVisible(TSWizardStepName.GESUCH_ERSTELLEN)).toBe(true);
        });
        it('returns false for NOT allowed steps whether they are hidden or not', function() {
            createAllSteps(TSWizardStepStatus.OK);
            wizardStepManager.getAllowedSteps().splice(0);
            wizardStepManager.setAllowedStepsForRole(TSRole.SACHBEARBEITER_INSTITUTION);
            expect(wizardStepManager.isStepVisible(TSWizardStepName.FINANZIELLE_SITUATION)).toBe(false);

            wizardStepManager.hideStep(TSWizardStepName.GESUCH_ERSTELLEN);
            expect(wizardStepManager.isStepVisible(TSWizardStepName.FINANZIELLE_SITUATION)).toBe(false);

            wizardStepManager.unhideStep(TSWizardStepName.GESUCH_ERSTELLEN);
            expect(wizardStepManager.isStepVisible(TSWizardStepName.FINANZIELLE_SITUATION)).toBe(false);
        });
    });

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
