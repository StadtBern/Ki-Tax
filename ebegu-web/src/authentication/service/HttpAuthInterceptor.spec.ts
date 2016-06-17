import {IWindowService, IRootScopeService} from 'angular';
import HttpAuthInterceptor from './HttpAuthInterceptor';
import {EbeguAuthentication} from '../authentication.module';
import {EbeguWebCore} from '../../core/core.module';
import {TSAuthEvent} from '../../models/enums/TSAuthEvent';

describe('HttpAuthInterceptor', function () {

    let httpAuthInterceptor: HttpAuthInterceptor;
    let $rootScope: IRootScopeService;
    let $window: IWindowService;

    let authErrorResponse: any = {
        status: 401,
        data: '',
        statusText: 'Unauthorized'
    };

    beforeEach(angular.mock.module(EbeguWebCore.name));
    beforeEach(angular.mock.module(EbeguAuthentication.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        httpAuthInterceptor = $injector.get('HttpAuthInterceptor');
        $rootScope = $injector.get('$rootScope');
        $window = $injector.get('$window');
        window.onbeforeunload = () => 'Oh no!';
        spyOn($rootScope, '$broadcast').and.callFake(() => {});
    }));

    describe('Public API', function () {
        it('should include a responseError() function', function () {
            expect(httpAuthInterceptor.responseError).toBeDefined();
        });
    });

    describe('API usage', function () {
        beforeEach(function () {
            httpAuthInterceptor.responseError(authErrorResponse);
        });
        it('should capture and broadcast "AUTH_EVENTS.notAuthenticated" on 401', function () {
            expect($rootScope.$broadcast).toHaveBeenCalledWith(TSAuthEvent.NOT_AUTHENTICATED, authErrorResponse);
        });
    });
});
