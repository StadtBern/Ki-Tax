import '../../../bootstrap.ts';
import 'angular-mocks';
import {EbeguWebGesuch} from '../../gesuch.module';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;

describe('familiensituationView', function () {

    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    let component: any;
    let scope: angular.IScope;
    let $componentController: any;

    beforeEach(angular.mock.inject(function ($injector: any) {
        $componentController = $injector.get('$componentController');
        let $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
    }));

    it('should be defined', function () {
    });
});
