import EbeguWebAdmin from '../../admin.module';

describe('adminView', () => {

    beforeEach(angular.mock.module(EbeguWebAdmin.name));

    let component: any;
    let scope: angular.IScope;
    let $componentController: angular.IComponentControllerService;

    beforeEach(angular.mock.inject(($injector: angular.auto.IInjectorService) => {
        $componentController = $injector.get('$componentController');
        let $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
    }));

    it('should be defined', () => {
        /*
         To initialise your component controller you have to setup your (mock) bindings and
         pass them to $componentController.
         */
        let bindings = {};
        component = $componentController('dvAdminView', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });
});
