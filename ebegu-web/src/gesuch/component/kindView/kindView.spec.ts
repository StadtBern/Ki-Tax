import IScope = angular.IScope;
describe('kindView', function () {

    beforeEach(angular.mock.module('ebeguWeb.gesuch'));

    let component: any;
    let scope: angular.IScope;
    let $componentController: any;

    beforeEach(angular.mock.inject(function (_$componentController_: any, $rootScope: IScope) {
        $componentController = _$componentController_;
        scope = $rootScope.$new();
    }));

    it('should be defined', function () {

    });
});
