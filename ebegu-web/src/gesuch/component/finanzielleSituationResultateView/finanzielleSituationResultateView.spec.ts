import '../../../bootstrap.ts';
import 'angular-mocks';
import {EbeguWebGesuch} from '../../gesuch.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;

describe('finanzielleSituationResultateView', function () {

    let gesuchModelManager: GesuchModelManager;
    let berechnungsManager: BerechnungsManager;

    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    var component : any;
    var scope : angular.IScope;
    var $componentController : any;

    beforeEach(angular.mock.inject(function ($injector: any) {
        $componentController = $injector.get('$componentController');
        gesuchModelManager = $injector.get('GesuchModelManager');
        berechnungsManager = $injector.get('BerechnungsManager');
        let $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
    }));

    beforeEach(function () {
        gesuchModelManager.initGesuch(false);
    });

    it('should be defined', function () {
        spyOn(berechnungsManager, 'calculateFinanzielleSituation').and.returnValue({});
        var bindings: {};
        component = $componentController('finanzielleSituationResultateView', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });
});
