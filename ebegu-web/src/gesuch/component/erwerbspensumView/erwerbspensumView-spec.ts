describe('erwerbspensumView', function () {

    beforeEach(angular.mock.module('ebeguWeb.gesuch'));

    var component: any;
    var scope: angular.IScope;
    var $componentController: any;

    beforeEach(angular.mock.inject(function (_$componentController_, $rootScope) {
        $componentController = _$componentController_;
        scope = $rootScope.$new();
    }));

    it('should be defined', function () {
        /*
         To initialise your component controller you have to setup your (mock) bindings and
         pass them to $componentController.
         */
        var bindings: {};
        component = $componentController('erwerbspensumView', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });
});