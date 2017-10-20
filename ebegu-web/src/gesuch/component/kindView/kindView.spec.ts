describe('kindView', function () {

    beforeEach(angular.mock.module('ebeguWeb.gesuch'));

    let component: any;
    let scope: angular.IScope;
    let $componentController: angular.IComponentControllerService;

    beforeEach(angular.mock.inject(function (_$componentController_: angular.IComponentControllerService,
                                             $rootScope: angular.IRootScopeService) {
        $componentController = _$componentController_;
        scope = $rootScope.$new();
    }));

    it('should be defined', function () {

    });
});
