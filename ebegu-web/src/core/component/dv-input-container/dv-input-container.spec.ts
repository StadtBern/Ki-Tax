import {IComponentControllerService, IRootScopeService} from 'angular';
import {DvInputContainerComponentConfig} from './dv-input-container';

describe('dvInputContainer', function () {

    beforeEach(angular.mock.module('ebeguWeb.core'));

    let component: DvInputContainerComponentConfig;
    let scope: angular.IScope;
    let $componentController: IComponentControllerService;

    beforeEach(angular.mock.inject(function (_$componentController_: IComponentControllerService,
                                             $rootScope: IRootScopeService) {
        $componentController = _$componentController_;
        scope = $rootScope.$new();
    }));

    it('should be defined', function () {
        /*
         To initialise your component controller you have to setup your (mock) bindings and
         pass them to $componentController.
         */
        let bindings: {};
        component = $componentController('dvInputContainer', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });
});
