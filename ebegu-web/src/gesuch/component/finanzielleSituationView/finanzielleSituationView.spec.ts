import '../../../bootstrap.ts';
import 'angular-mocks';
import {EbeguWebGesuch} from '../../gesuch.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import TSFamiliensituation from '../../../models/TSFamiliensituation';
import TSGesuchsteller from '../../../models/TSGesuchsteller';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;
import TSFamiliensituationContainer from '../../../models/TSFamiliensituationContainer';
import TSGesuchstellerContainer from '../../../models/TSGesuchstellerContainer';

describe('finanzielleSituationView', function () {

    let gesuchModelManager: GesuchModelManager;
    let berechnungsManager: BerechnungsManager;

    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    var component: any;
    var scope: angular.IScope;
    var $componentController: any;

    beforeEach(angular.mock.inject(function ($injector: any) {
        $componentController = $injector.get('$componentController');
        gesuchModelManager = $injector.get('GesuchModelManager');
        let wizardStepManager = $injector.get('WizardStepManager');
        spyOn(wizardStepManager, 'updateWizardStepStatus').and.returnValue({});
        berechnungsManager = $injector.get('BerechnungsManager');
        let $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
    }));

    beforeEach(function () {
        gesuchModelManager.initGesuch(false);
        gesuchModelManager.getGesuch().familiensituationContainer = new TSFamiliensituationContainer();
        gesuchModelManager.getGesuch().familiensituationContainer.familiensituationJA = new TSFamiliensituation();
        gesuchModelManager.getGesuch().gesuchsteller1 = new TSGesuchstellerContainer(new TSGesuchsteller());
    });

    it('should be defined', function () {
        spyOn(berechnungsManager, 'calculateFinanzielleSituation').and.returnValue({});
        var bindings: {};
        component = $componentController('finanzielleSituationView', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });
});
