import {IRootScopeService, IComponentControllerService} from 'angular';
import {ErwerbspensumListViewComponentConfig} from './erwerbspensumListView';

describe('erwerbspensumListView', function () {

    beforeEach(angular.mock.module('ebeguWeb.gesuch'));

    let component: ErwerbspensumListViewComponentConfig;
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
        component = $componentController('erwerbspensumListView', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });
});
