import '../../../bootstrap.ts';
import 'angular-mocks';
import GesuchModelManager from '../../service/gesuchModelManager';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;
import IScope = angular.IScope;

describe('stammdatenView', function () {

    let gesuchModelManager: GesuchModelManager;

    beforeEach(angular.mock.module('ebeguWeb.gesuch'));

    let component: any;
    let scope: IScope;
    let $componentController: any;

    beforeEach(angular.mock.inject(function ($injector: any) {
        $componentController = $injector.get('$componentController');
        gesuchModelManager = $injector.get('GesuchModelManager');
        spyOn(gesuchModelManager, 'initGesuchstellerStatus').and.returnValue({});
        let $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
    }));

    beforeEach(function () {
        gesuchModelManager.initGesuch(false);
    });

    it('should be defined', function () {
        /*
         To initialise your component controller you have to setup your (mock) bindings and
         pass them to $componentController.
         */
        let bindings: {};
        component = $componentController('stammdatenView', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });
});
