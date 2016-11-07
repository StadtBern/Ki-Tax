import '../../../bootstrap.ts';
import 'angular-mocks';
import {EbeguAuthentication} from '../../authentication.module';
import {TSAuthEvent} from '../../../models/enums/TSAuthEvent';
import IInjectorService = angular.auto.IInjectorService;
import IRootScopeService = angular.IRootScopeService;
import IScope = angular.IScope;
import IScope = angular.IScope;

describe('startView', function () {

    beforeEach(angular.mock.module(EbeguAuthentication.name));

    let component: any;
    let $rootScope: IRootScopeService;
    let scope: IScope;
    let $componentController: any;

    beforeEach(angular.mock.inject(function ($injector: any) {
        $componentController = $injector.get('$componentController');
        $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
    }));

    it('should be defined', function () {
        /*
         To initialise your component controller you have to setup your (mock) bindings and
         pass them to $componentController.
         */
        let bindings = {};
        component = $componentController('startView', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });

    it('should  broadcast "AUTH_EVENTS.notAuthenticated" if no principal is available', function () {
        expect($rootScope.$broadcast).toHaveBeenCalledWith(TSAuthEvent.NOT_AUTHENTICATED, 'not logged in on startpage');
    });
});
