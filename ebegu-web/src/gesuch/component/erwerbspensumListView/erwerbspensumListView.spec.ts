import {ErwerbspensumListViewComponentConfig} from './erwerbspensumListView';

describe('erwerbspensumListView', function () {

    beforeEach(angular.mock.module('ebeguWeb.gesuch'));

    let component: ErwerbspensumListViewComponentConfig;
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
        component = $componentController('erwerbspensumListView', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });
});
