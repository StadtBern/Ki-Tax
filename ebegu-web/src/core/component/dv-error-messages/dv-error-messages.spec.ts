import {EbeguWebCore} from '../../core.module';

describe('dvErrorMessages', function () {

    beforeEach(angular.mock.module(EbeguWebCore.name));

    let component: any;
    let scope: angular.IScope;
    let $componentController: angular.IComponentControllerService;

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        $componentController = $injector.get('$componentController');
        let $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
    }));

    it('should be defined', function () {
        /*
         To initialise your component controller you have to setup your (mock) bindings and
         pass them to $componentController.
         */
        let bindings = {};
        component = $componentController('dvErrorMessages', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });
});
