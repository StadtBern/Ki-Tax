import {EbeguWebCore} from '../../core.module';

describe('dvBisher', function () {

    beforeEach(angular.mock.module(EbeguWebCore.name));

    let component: any;
    let scope: angular.IScope;
    let $componentController: angular.IComponentControllerService;

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        $componentController = $injector.get('$componentController');
        const $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
    }));

    it('should be defined', function () {
        /*
         To initialise your component controller you have to setup your (mock) bindings and
         pass them to $componentController.
         */
        const bindings = {};
        component = $componentController('dvBisher', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });
});
