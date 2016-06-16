import '../../../bootstrap.ts';
import 'angular-mocks';
import {EbeguWebGesuch} from '../../gesuch.module';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;

describe('familiensituationView', function () {

    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    var component : any;
    var scope : angular.IScope;
    var $componentController : any;

    beforeEach(angular.mock.inject(function ($injector: any) {
        $componentController = $injector.get('$componentController');
        let $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
    }));

    it('should be defined', function () {
    });
});
