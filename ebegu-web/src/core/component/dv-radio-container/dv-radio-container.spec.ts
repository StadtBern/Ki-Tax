import {DvRadioContainerComponentConfig} from './dv-radio-container';

describe('dvRadioContainer', function () {

    beforeEach(angular.mock.module('ebeguWeb.core'));

    let component: DvRadioContainerComponentConfig;
    let scope: angular.IScope;
    let $componentController: angular.IComponentControllerService;

    beforeEach(angular.mock.inject(function (_$componentController_: angular.IComponentControllerService,
                                             $rootScope: angular.IRootScopeService) {
        $componentController = _$componentController_;
        scope = $rootScope.$new();
    }));

    it('should be defined', function () {
        /*
         To initialise your component controller you have to setup your (mock) bindings and
         pass them to $componentController.
         */
        let bindings = {};
        component = $componentController('dvRadioContainer', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });
});
