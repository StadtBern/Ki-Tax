import '../../../../bootstrap.ts';
import 'angular-mocks';
import IScope = angular.IScope;
import ICompileService = angular.ICompileService;
import IRootScopeService = angular.IRootScopeService;

describe('dvbErrorMessages', function () {


    let component: any;
    let scope: angular.IScope;
    let $componentController: any;

    beforeEach(angular.mock.module('dvbAngular.errors'));

    beforeEach(angular.mock.inject(function ($injector: any) {
        $componentController = $injector.get('$componentController');
        let $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
    }));

    it('should ...', function () {
        let bindings: {};
        component = $componentController('dvbErrorMessagesPanel', {$scope: scope}, bindings);
        expect(component).toBeDefined();

    });
});
