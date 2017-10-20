import {EbeguWebGesuch} from '../../gesuch.module';

describe('familiensituationView', function () {

    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    let component: any;
    let scope: angular.IScope;
    let $componentController: angular.IComponentControllerService;

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        $componentController = $injector.get('$componentController');
        let $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
    }));

    it('should be defined', function () {
    });
});
