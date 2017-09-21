import {IComponentControllerService, IRootScopeService} from 'angular';
import {DvRadioContainerComponentConfig} from './dv-radio-container';

describe('dvRadioContainer', function () {

    beforeEach(angular.mock.module('ebeguWeb.core'));

    let component: DvRadioContainerComponentConfig;
    let scope: IScope;
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
        component = $componentController('dvRadioContainer', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });
});
