import {IScope, IRootScopeService} from "angular";

describe('familiensituationView', function () {

    beforeEach(angular.mock.module('ebeguWeb.gesuch'));

    let component: any;
    let scope: IScope;
    let $componentController: any;

    beforeEach(angular.mock.inject(function (_$componentController_: any, $rootScope: IRootScopeService) {
        $componentController = _$componentController_;
        scope = $rootScope.$new();
    }));

    it('should be defined', function () {
        /*
         To initialise your component controller you have to setup your (mock) bindings and
         pass them to $componentController.
         */
        var bindings: {};
        component = $componentController('familiensituationView', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });
});
