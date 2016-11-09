import {NavigatorController} from './dv-navigation';
import {EbeguWebCore} from '../../core.module';
import WizardStepManager from '../../../gesuch/service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {IStateService} from 'angular-ui-router';
import TestDataUtil from '../../../utils/TestDataUtil';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import IQService = angular.IQService;
import IScope = angular.IScope;
import TSGesuch from '../../../models/TSGesuch';
import {TSAntragTyp} from '../../../models/enums/TSAntragTyp';
describe('dvNavigation', function () {

    let navController: NavigatorController;
    let wizardStepManager: WizardStepManager;
    let $state: IStateService;
    let $q: IQService;
    let $rootScope: IScope;
    let gesuchModelManager: GesuchModelManager;
    let authServiceRS: AuthServiceRS;
    let isStatusVerfuegen: boolean;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($injector.get('$httpBackend'));
        $q = $injector.get('$q');
        $rootScope = $injector.get('$rootScope');
        wizardStepManager = $injector.get('WizardStepManager');
        $state = $injector.get('$state');
        gesuchModelManager = $injector.get('GesuchModelManager');
        authServiceRS = $injector.get('AuthServiceRS');
        navController = new NavigatorController(wizardStepManager, $state, gesuchModelManager,
            $injector.get('$translate'), $injector.get('ErrorService'));
        navController.dvSave = () => {
            return $q.when({});
        };
        isStatusVerfuegen = true;
        spyOn(gesuchModelManager, 'isGesuchStatusVerfuegenVerfuegt').and.callFake(() => {
            return isStatusVerfuegen;
        });
    }));

    describe('getNextButtonName', function () {
        it('returns WEITER_UPPER if dvSave exists', () => {
            isStatusVerfuegen = false;
            expect(navController.getNextButtonName()).toEqual('SPEICHERN UND WEITER');
        });
        it('returns WEITER_ONLY_UPPER if status is VERFUEGEN', () => {
            expect(navController.getNextButtonName()).toEqual('WEITER');
        });
        it('returns WEITER_ONLY_UPPER if dvSave does not exist', () => {
            isStatusVerfuegen = false;
            navController.dvSave = undefined;
            expect(navController.getNextButtonName()).toEqual('WEITER');
        });
    });

    describe('getNextButtonName', function () {
        it('returns ZURUECK_UPPER if dvSave exists', () => {
            isStatusVerfuegen = false;
            expect(navController.getPreviousButtonName()).toEqual('SPEICHERN UND ZURÜCK');
        });
        it('returns ZURUECK_ONLY_UPPER if status is VERFUEGEN', () => {
            expect(navController.getPreviousButtonName()).toEqual('ZURÜCK');
        });
        it('returns ZURUECK_ONLY_UPPER if dvSave does not exist', () => {
            isStatusVerfuegen = false;
            navController.dvSave = undefined;
            expect(navController.getPreviousButtonName()).toEqual('ZURÜCK');
        });
    });
    describe('nextStep', function () {
        it('moves to gesuch.familiensituation when coming from GESUCH_ERSTELLEN', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.GESUCH_ERSTELLEN);
            spyOn(wizardStepManager, 'getNextStep').and.returnValue(TSWizardStepName.FAMILIENSITUATION);
            mockGesuch();
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.familiensituation', { gesuchId: '123' });
        });
        it('moves to gesuch.stammdaten when coming from FAMILIENSITUATION', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.FAMILIENSITUATION);
            spyOn(wizardStepManager, 'getNextStep').and.returnValue(TSWizardStepName.GESUCHSTELLER);
            mockGesuch();
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.stammdaten', {gesuchstellerNumber: '1', gesuchId: '123' });
        });
        it('moves to gesuch.stammdaten 2 when coming from GESUCHSTELLER 1', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.GESUCHSTELLER);
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(1);
            spyOn(gesuchModelManager, 'isGesuchsteller2Required').and.returnValue(true);
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.stammdaten', {gesuchstellerNumber: '2', gesuchId: '' });
        });
        it('moves to gesuch.betreuungen when coming from GESUCHSTELLER and institution', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.GESUCHSTELLER);
            spyOn(wizardStepManager, 'getNextStep').and.returnValue(TSWizardStepName.BETREUUNG);
            mockGesuch();
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(1);
            spyOn(gesuchModelManager, 'isGesuchsteller2Required').and.returnValue(false);
            spyOn(authServiceRS, 'isRole').and.returnValue(true);
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen', { gesuchId: '123' });
        });
        it('moves to gesuch.umzug when coming from GESUCHSTELLER', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.GESUCHSTELLER);
            spyOn(wizardStepManager, 'getNextStep').and.returnValue(TSWizardStepName.UMZUG);
            mockGesuch();
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(1);
            spyOn(gesuchModelManager, 'isGesuchsteller2Required').and.returnValue(false);
            spyOn(authServiceRS, 'isRole').and.returnValue(false);
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.umzug', { gesuchId: '123' });
        });
        it('moves to gesuch.kinder when coming from UMZUG substep 1', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.UMZUG);
            spyOn(wizardStepManager, 'getNextStep').and.returnValue(TSWizardStepName.KINDER);
            mockGesuch();
            navController.dvSubStep = 1;
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.kinder', { gesuchId: '123' });
        });
        it('moves to gesuch.betreuungen when coming from KINDER substep 1', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.KINDER);
            spyOn(wizardStepManager, 'getNextStep').and.returnValue(TSWizardStepName.BETREUUNG);
            mockGesuch();
            navController.dvSubStep = 1;
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen', { gesuchId: '123' });
        });
        it('moves to gesuch.kinder when coming from KINDER substep 2', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.KINDER);
            navController.dvSubStep = 2;
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.kinder', { gesuchId: '' });
        });
        it('moves to gesuch.verfuegen when coming from BETREUUNG substep 1 and Institution', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.BETREUUNG);
            spyOn(wizardStepManager, 'getNextStep').and.returnValue(TSWizardStepName.VERFUEGEN);
            mockGesuch();
            navController.dvSubStep = 1;
            spyOn(authServiceRS, 'isRole').and.returnValue(true);
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.verfuegen', { gesuchId: '123' });
        });
        it('moves to gesuch.erwerbsPensen when coming from BETREUUNG substep 1 and no Institution', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.BETREUUNG);
            spyOn(wizardStepManager, 'getNextStep').and.returnValue(TSWizardStepName.ERWERBSPENSUM);
            mockGesuch();
            navController.dvSubStep = 1;
            spyOn(authServiceRS, 'isRole').and.returnValue(false);
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.erwerbsPensen', { gesuchId: '123' });
        });
        it('moves to gesuch.finanzielleSituationStart when coming from ERWERBSPENSUM substep 1 and 2GS required', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.ERWERBSPENSUM);
            spyOn(wizardStepManager, 'isNextStepBesucht').and.returnValue(true);
            spyOn(wizardStepManager, 'isNextStepEnabled').and.returnValue(true);
            navController.dvSubStep = 1;
            mockGesuch();
            spyOn(gesuchModelManager, 'isGesuchsteller2Required').and.returnValue(true);
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.finanzielleSituationStart', { gesuchId: '123' });
        });
        it('moves to gesuch.dokumente when coming from ERWERBSPENSUM substep 1 and disabled', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.ERWERBSPENSUM);
            spyOn(wizardStepManager, 'getNextStep').and.returnValue(TSWizardStepName.DOKUMENTE);
            mockGesuch();
            spyOn(wizardStepManager, 'isNextStepBesucht').and.returnValue(true);
            spyOn(wizardStepManager, 'isNextStepEnabled').and.returnValue(false);
            spyOn(wizardStepManager, 'updateCurrentWizardStepStatus').and.returnValue($q.when({}));
            navController.dvSubStep = 1;
            callNextStep();
            $rootScope.$apply();
            expect($state.go).toHaveBeenCalledWith('gesuch.dokumente', { gesuchId: '123' });
        });
        it('moves to gesuch.finanzielleSituation when coming from ERWERBSPENSUM substep 1 and 2GS not required', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.ERWERBSPENSUM);
            spyOn(wizardStepManager, 'getNextStep').and.returnValue(TSWizardStepName.FINANZIELLE_SITUATION);
            spyOn(wizardStepManager, 'isNextStepBesucht').and.returnValue(true);
            spyOn(wizardStepManager, 'isNextStepEnabled').and.returnValue(true);
            spyOn(wizardStepManager, 'updateCurrentWizardStepStatus').and.returnValue($q.when({}));
            navController.dvSubStep = 1;
            mockGesuch();
            spyOn(gesuchModelManager, 'isGesuchsteller2Required').and.returnValue(false);
            callNextStep();
            $rootScope.$apply();
            expect($state.go).toHaveBeenCalledWith('gesuch.finanzielleSituation', {gesuchstellerNumber: '1',  gesuchId: '123' });
        });
        it('moves to gesuch.erwerbsPensen when coming from ERWERBSPENSUM substep 2', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.ERWERBSPENSUM);
            navController.dvSubStep = 2;
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.erwerbsPensen', { gesuchId: '' });
        });
        it('moves to gesuch.finanzielleSituation when coming from FINANZIELLE_SITUATION substep 1 with GS1 and 2GS required', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.FINANZIELLE_SITUATION);
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(1);
            spyOn(gesuchModelManager, 'isGesuchsteller2Required').and.returnValue(true);
            navController.dvSubStep = 1;
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.finanzielleSituation', {gesuchstellerNumber: '2',  gesuchId: '' });
        });
        it('moves to gesuch.finanzielleSituationResultate when coming from FINANZIELLE_SITUATION substep 1 with GS1 and 2GS NOT required', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.FINANZIELLE_SITUATION);
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(1);
            spyOn(gesuchModelManager, 'isGesuchsteller2Required').and.returnValue(false);
            navController.dvSubStep = 1;
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.finanzielleSituationResultate', { gesuchId: '' });
        });
        it('moves to gesuch.finanzielleSituation when coming from FINANZIELLE_SITUATION substep 2', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.FINANZIELLE_SITUATION);
            navController.dvSubStep = 2;
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue('1');
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.finanzielleSituation', {gesuchstellerNumber: '1' , gesuchId: '' });
        });
        it('moves to gesuch.einkommensverschlechterungInfo when coming from FINANZIELLE_SITUATION substep 3', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.FINANZIELLE_SITUATION);
            spyOn(wizardStepManager, 'getNextStep').and.returnValue(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
            mockGesuch();
            navController.dvSubStep = 3;
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.einkommensverschlechterungInfo', { gesuchId: '123' });
        });
        it('moves to gesuch.einkommensverschlechterungSteuern when coming from EINKOMMENSVERSCHLECHTERUNG substep 1 with EV and 2GS required', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
            spyOn(gesuchModelManager, 'getEinkommensverschlechterungsInfo').and.returnValue({einkommensverschlechterung: true});
            spyOn(gesuchModelManager, 'isGesuchsteller2Required').and.returnValue(true);
            navController.dvSubStep = 1;
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.einkommensverschlechterungSteuern', { gesuchId: '' });
        });
        it('moves to gesuch.einkommensverschlechterung when coming from EINKOMMENSVERSCHLECHTERUNG substep 1 with EV and 2GS NOT required', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
            spyOn(gesuchModelManager, 'getEinkommensverschlechterungsInfo').and.returnValue({einkommensverschlechterung: true});
            spyOn(gesuchModelManager, 'isGesuchsteller2Required').and.returnValue(false);
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue('1');
            navController.dvSubStep = 1;
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '1',  basisjahrPlus: '1', gesuchId: '' });
        });
        it('moves to gesuch.dokumente when coming from EINKOMMENSVERSCHLECHTERUNG substep 1 without EV', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
            spyOn(wizardStepManager, 'getNextStep').and.returnValue(TSWizardStepName.DOKUMENTE);
            spyOn(wizardStepManager, 'updateCurrentWizardStepStatus').and.returnValue($q.when({}));
            mockGesuch();
            spyOn(gesuchModelManager, 'getEinkommensverschlechterungsInfo').and.returnValue({einkommensverschlechterung: false});
            navController.dvSubStep = 1;
            callNextStep();
            $rootScope.$apply();
            expect($state.go).toHaveBeenCalledWith('gesuch.dokumente', { gesuchId: '123' });
        });
        it('moves to gesuch.dokumente when coming from EINKOMMENSVERSCHLECHTERUNG substep 1 without EV', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
            spyOn(wizardStepManager, 'getNextStep').and.returnValue(TSWizardStepName.DOKUMENTE);
            mockGesuch();
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue('1');
            navController.dvSubStep = 3;
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.einkommensverschlechterung', {
                gesuchstellerNumber: '1',
                basisjahrPlus: '1',
                gesuchId: '123'
            });
        });
        it('moves to gesuch.verfuegen when coming from DOKUMENTE', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.DOKUMENTE);
            spyOn(wizardStepManager, 'getNextStep').and.returnValue(TSWizardStepName.VERFUEGEN);
            mockGesuch();
            callNextStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.verfuegen', { gesuchId: '123' });
        });
    });

    describe('previousStep', function () {
        it('moves to gesuch.fallcreation when coming from FAMILIENSITUATION', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.FAMILIENSITUATION);
            spyOn(wizardStepManager, 'getPreviousStep').and.returnValue(TSWizardStepName.GESUCH_ERSTELLEN);
            mockGesuch();
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.fallcreation', {createNew: 'false', gesuchId: '123' });
        });
        it('moves to gesuch.stammdaten when coming from GESUCHSTELLER from 2GS', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.GESUCHSTELLER);
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(2);
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.stammdaten', {gesuchstellerNumber: '1', gesuchId: '' });
        });
        it('moves to gesuch.familiensituation when coming from GESUCHSTELLER from 1GS', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.GESUCHSTELLER);
            spyOn(wizardStepManager, 'getPreviousStep').and.returnValue(TSWizardStepName.FAMILIENSITUATION);
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(1);
            mockGesuch();
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.familiensituation', { gesuchId: '123' });
        });
        it('moves to gesuch.stammdaten when coming from UMZUG', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.UMZUG);
            spyOn(wizardStepManager, 'getPreviousStep').and.returnValue(TSWizardStepName.GESUCHSTELLER);
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(1);
            mockGesuch();
            navController.dvSubStep = 1;
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.stammdaten', {gesuchstellerNumber: '1',  gesuchId: '123' });
        });
        it('moves to gesuch.umzug when coming from KINDER substep 1', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.KINDER);
            spyOn(wizardStepManager, 'getPreviousStep').and.returnValue(TSWizardStepName.UMZUG);
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(1);
            navController.dvSubStep = 1;
            mockGesuch();
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.umzug', {gesuchId: '123' });
        });
        it('moves to gesuch.umzug when coming from KINDER substep 1', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.KINDER);
            spyOn(wizardStepManager, 'getPreviousStep').and.returnValue(TSWizardStepName.UMZUG);
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(2);
            navController.dvSubStep = 1;
            mockGesuch();
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.umzug', {gesuchId: '123' });
        });
        it('moves to gesuch.kinder when coming from KINDER substep 2', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.KINDER);
            navController.dvSubStep = 2;
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.kinder', { gesuchId: '' });
        });
        it('moves to gesuch.stammdaten when coming from BETREUUNG substep 1', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.BETREUUNG);
            spyOn(wizardStepManager, 'getPreviousStep').and.returnValue(TSWizardStepName.GESUCHSTELLER);
            spyOn(authServiceRS, 'isRole').and.returnValue(true);
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(1);
            navController.dvSubStep = 1;
            mockGesuch();
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.stammdaten', {gesuchstellerNumber: '1',  gesuchId: '123' });
        });
        it('moves to gesuch.stammdaten when coming from BETREUUNG substep 1', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.BETREUUNG);
            spyOn(wizardStepManager, 'getPreviousStep').and.returnValue(TSWizardStepName.GESUCHSTELLER);
            spyOn(authServiceRS, 'isRole').and.returnValue(true);
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(1);
            navController.dvSubStep = 1;
            mockGesuch();
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.stammdaten', {gesuchstellerNumber: '1', gesuchId: '123' });
        });
        it('moves to gesuch.kinder when coming from BETREUUNG substep 1', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.BETREUUNG);
            spyOn(wizardStepManager, 'getPreviousStep').and.returnValue(TSWizardStepName.KINDER);
            spyOn(authServiceRS, 'isRole').and.returnValue(false);
            navController.dvSubStep = 1;
            mockGesuch();
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.kinder', { gesuchId: '123' });
        });
        it('moves to gesuch.betreuungen when coming from BETREUUNG substep 2', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.BETREUUNG);
            navController.dvSubStep = 2;
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen', { gesuchId: '' });
        });
        it('moves to gesuch.betreuungen when coming from ERWERBSPENSUM substep 1', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.ERWERBSPENSUM);
            spyOn(wizardStepManager, 'getPreviousStep').and.returnValue(TSWizardStepName.BETREUUNG);
            navController.dvSubStep = 1;
            mockGesuch();
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen', { gesuchId: '123' });
        });
        it('moves to gesuch.erwerbsPensen when coming from ERWERBSPENSUM substep 2', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.ERWERBSPENSUM);
            navController.dvSubStep = 2;
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.erwerbsPensen', { gesuchId: '' });
        });
        it('moves to gesuch.finanzielleSituation when coming from FINANZIELLE_SITUATION substep 1', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.FINANZIELLE_SITUATION);
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(2);
            navController.dvSubStep = 1;
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.finanzielleSituation', {gesuchstellerNumber: '1' ,  gesuchId: '' });
        });
        it('moves to gesuch.finanzielleSituationStart when coming from FINANZIELLE_SITUATION substep 1', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.FINANZIELLE_SITUATION);
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(1);
            spyOn(gesuchModelManager, 'isGesuchsteller2Required').and.returnValue(true);
            navController.dvSubStep = 1;
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.finanzielleSituationStart', { gesuchId: '' });
        });
        it('moves to gesuch.kinder when coming from FINANZIELLE_SITUATION substep 1', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.FINANZIELLE_SITUATION);
            spyOn(wizardStepManager, 'getPreviousStep').and.returnValue(TSWizardStepName.KINDER);
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(1);
            spyOn(gesuchModelManager, 'isGesuchsteller2Required').and.returnValue(false);
            navController.dvSubStep = 1;
            mockGesuch();
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.kinder', { gesuchId: '123' });
        });
        it('moves to gesuch.erwerbsPensen when coming from FINANZIELLE_SITUATION substep 2', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.FINANZIELLE_SITUATION);
            spyOn(wizardStepManager, 'getPreviousStep').and.returnValue(TSWizardStepName.ERWERBSPENSUM);
            navController.dvSubStep = 2;
            mockGesuch();
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.erwerbsPensen', { gesuchId: '123' });
        });
        it('moves to gesuch.finanzielleSituation when coming from FINANZIELLE_SITUATION substep 3', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.FINANZIELLE_SITUATION);
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(1);
            navController.dvSubStep = 3;
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.finanzielleSituation', {gesuchstellerNumber: '1' ,  gesuchId: '' });
        });
        it('moves to gesuch.finanzielleSituation when coming from FINANZIELLE_SITUATION substep 3', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.FINANZIELLE_SITUATION);
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(2);
            navController.dvSubStep = 3;
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.finanzielleSituation', {gesuchstellerNumber: '2' , gesuchId: '' });
        });
        it('moves to gesuch.finanzielleSituation when coming from EINKOMMENSVERSCHLECHTERUNG substep 1', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
            spyOn(wizardStepManager, 'getPreviousStep').and.returnValue(TSWizardStepName.FINANZIELLE_SITUATION);
            navController.dvSubStep = 1;
            mockGesuch();
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.finanzielleSituation', {gesuchstellerNumber: '1' , gesuchId: '123' });
        });
        it('moves to gesuch.einkommensverschlechterungInfo when coming from EINKOMMENSVERSCHLECHTERUNG substep 3', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
            navController.dvSubStep = 3;
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.einkommensverschlechterungInfo', { gesuchId: '' });
        });
        it('moves to gesuch.betreuungen when coming from VERFUEGEN substep 1', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.VERFUEGEN);
            spyOn(wizardStepManager, 'getPreviousStep').and.returnValue(TSWizardStepName.BETREUUNG);
            spyOn(authServiceRS, 'isRole').and.returnValue(true);
            navController.dvSubStep = 1;
            mockGesuch();
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen', { gesuchId: '123' });
        });
        it('moves to gesuch.dokumente when coming from VERFUEGEN substep 1', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.VERFUEGEN);
            spyOn(wizardStepManager, 'getPreviousStep').and.returnValue(TSWizardStepName.DOKUMENTE);
            spyOn(wizardStepManager, 'updateCurrentWizardStepStatus').and.returnValue($q.when({}));
            spyOn(authServiceRS, 'isRole').and.returnValue(false);
            navController.dvSubStep = 1;
            mockGesuch();
            callPreviousStep();
            $rootScope.$apply();
            expect($state.go).toHaveBeenCalledWith('gesuch.dokumente', { gesuchId: '123' });
        });
        it('moves to gesuch.verfuegen when coming from VERFUEGEN substep 2', () => {
            spyOn(wizardStepManager, 'getCurrentStepName').and.returnValue(TSWizardStepName.VERFUEGEN);
            navController.dvSubStep = 2;
            callPreviousStep();
            expect($state.go).toHaveBeenCalledWith('gesuch.verfuegen', { gesuchId: '' });
        });
    });

    function mockGesuch() {
        let gesuch: TSGesuch = new TSGesuch();
        gesuch.typ = TSAntragTyp.GESUCH;
        gesuch.id = '123';
        spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);
    }

    function callPreviousStep() {
        spyOn($state, 'go').and.returnValue({}); // do nothing
        navController.previousStep();
        $rootScope.$apply();
    }

    function callNextStep() {
        spyOn($state, 'go').and.returnValue({}); // do nothing
        navController.nextStep();
        $rootScope.$apply();
    }
});
