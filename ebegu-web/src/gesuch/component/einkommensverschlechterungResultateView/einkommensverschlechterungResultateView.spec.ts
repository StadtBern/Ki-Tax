import '../../../bootstrap.ts';
import 'angular-mocks';
import {EbeguWebGesuch} from '../../gesuch.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import {EinkommensverschlechterungResultateViewController} from './einkommensverschlechterungResultateView';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
import WizardStepManager from '../../service/wizardStepManager';
import TSFinanzModel from '../../../models/TSFinanzModel';
import TSGesuchstellerContainer from '../../../models/TSGesuchstellerContainer';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;
import IStateService = angular.ui.IStateService;
import TSGesuchsteller from '../../../models/TSGesuchsteller';

describe('einkommensverschlechterungResultateView', function () {

    let gesuchModelManager: GesuchModelManager;
    let berechnungsManager: BerechnungsManager;
    let ekvrvc: EinkommensverschlechterungResultateViewController;
    let $state: IStateService;

    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    var component: any;
    var scope: angular.IScope;
    var $componentController: any;
    var stateParams: any;
    var consta: any;
    var errorservice: any;
    let wizardStepManager: WizardStepManager;

    beforeEach(angular.mock.inject(function ($injector: any) {
        $componentController = $injector.get('$componentController');
        gesuchModelManager = $injector.get('GesuchModelManager');
        berechnungsManager = $injector.get('BerechnungsManager');
        let $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
        let $q = $injector.get('$q');
        stateParams = $injector.get('$stateParams');
        consta = $injector.get('CONSTANTS');
        errorservice = $injector.get('ErrorService');
        wizardStepManager = $injector.get('WizardStepManager');


        spyOn(berechnungsManager, 'calculateFinanzielleSituation').and.returnValue($q.when({}));

    }));

    beforeEach(function () {
        gesuchModelManager.initGesuch(false);
        gesuchModelManager.initFamiliensituation();
        gesuchModelManager.getGesuch().gesuchsteller1 = new TSGesuchstellerContainer(new TSGesuchsteller());
        gesuchModelManager.getGesuch().gesuchsteller2 = new TSGesuchstellerContainer(new TSGesuchsteller());

    });

    it('should be defined', function () {
        spyOn(berechnungsManager, 'calculateEinkommensverschlechterung').and.returnValue({});
        var bindings: {};
        component = $componentController('einkommensverschlechterungResultateView', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });

    describe('calculateVeraenderung', () => {
        beforeEach(function () {
            ekvrvc = new EinkommensverschlechterungResultateViewController(stateParams, gesuchModelManager,
                berechnungsManager, consta, errorservice, wizardStepManager, null);
            ekvrvc.model = new TSFinanzModel(gesuchModelManager.getBasisjahr(), gesuchModelManager.isGesuchsteller2Required(), null, null);
            ekvrvc.model.copyEkvDataFromGesuch(gesuchModelManager.getGesuch());
            ekvrvc.model.copyFinSitDataFromGesuch(gesuchModelManager.getGesuch());

        });
        it('should return + 100.0%', () => {

            setValues(100, 200);
            expect(ekvrvc.calculateVeraenderung()).toEqual('+ 100.0 %');
        });

        it('should return - 50.0 %', () => {

            setValues(200, 100);
            expect(ekvrvc.calculateVeraenderung()).toEqual('- 50.0 %');
        });

        it('should return - 90.0%', () => {

            setValues(200, 20);
            expect(ekvrvc.calculateVeraenderung()).toEqual('- 90.0 %');
        });

        it('should return - 81.2 %', () => {

            setValues(59720, 11230);
            expect(ekvrvc.calculateVeraenderung()).toEqual('- 81.2 %');
        });

        it('should return - 100 %', () => {

            setValues(59720, 0);
            expect(ekvrvc.calculateVeraenderung()).toEqual('- 100 %');
        });

        it('should return + 100 %', () => {

            setValues(0, 59720);
            expect(ekvrvc.calculateVeraenderung()).toEqual('+ 100 %');
        });

        function setValues( massgebendesEinkommen_vj: number, massgebendesEinkommen_bj: number) {
            let finsint: TSFinanzielleSituationResultateDTO = new TSFinanzielleSituationResultateDTO();
            finsint.massgebendesEinkVorAbzFamGr = massgebendesEinkommen_bj;

            let finsintvj: TSFinanzielleSituationResultateDTO = new TSFinanzielleSituationResultateDTO();
            finsintvj.massgebendesEinkVorAbzFamGr = massgebendesEinkommen_vj;

            spyOn(ekvrvc, 'getResultate').and.returnValue(finsint);
            ekvrvc.resultatVorjahr = finsintvj;
        }

    });
});


