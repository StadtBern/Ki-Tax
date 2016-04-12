import {IRootScopeService, IScope} from "angular";

describe('dvErrorMessages', function () {

    beforeEach(angular.mock.module('ebeguWeb.core'));

    let component: any;
    let scope: IScope;
    let $componentController: any;

    beforeEach(inject(function (_$componentController_: any, $rootScope: IRootScopeService) {
        $componentController = _$componentController_;
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
