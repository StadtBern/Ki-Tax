import {EbeguWebCore} from '../../core/core.module';
import {TSRole} from '../../models/enums/TSRole';
import TSUser from '../../models/TSUser';
import TestDataUtil from '../../utils/TestDataUtil';
import {EbeguAuthentication} from '../authentication.module';
import AuthServiceRS from './AuthServiceRS.rest';

describe('AuthServiceRS', function () {

    let authServiceRS: AuthServiceRS;
    let $http: angular.IHttpService;
    let $httpBackend: angular.IHttpBackendService;
    let $q: angular.IQService;
    let $rootScope: angular.IScope;
    let $timeout: angular.ITimeoutService;
    let $cookies: angular.cookies.ICookiesService;
    let base64: any;

    beforeEach(angular.mock.module(EbeguWebCore.name));
    beforeEach(angular.mock.module(EbeguAuthentication.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        authServiceRS = $injector.get('AuthServiceRS');
        $http = $injector.get('$http');
        $httpBackend = $injector.get('$httpBackend');
        $rootScope = $injector.get('$rootScope');
        $q = $injector.get('$q');
        $timeout = $injector.get('$timeout');
        $cookies = $injector.get('$cookies');
        base64 = $injector.get('base64');
    }));

    describe('API usage', function () {
        beforeEach(() => {
            spyOn($http, 'post').and.returnValue($q.when({}));
        });
        it('does not nothing for an undefined user', function () {
            expect(authServiceRS.loginRequest(undefined)).toBeUndefined();
            expect($http.post).not.toHaveBeenCalled();
        });
        it('receives a loginRequest and handles the incoming cookie', function () {
            let user: TSUser = new TSUser('Emma', 'Gerber', 'geem', 'password5', 'emma.gerber@myemail.ch', undefined, TSRole.GESUCHSTELLER);
            let encodedUser = base64.encode(JSON.stringify(user).split('_').join(''));
            spyOn($cookies, 'get').and.returnValue(encodedUser);

            let cookieUser: TSUser;
            //if we can decode the cookie the client application assumes the user is logged in for ui purposes
            TestDataUtil.mockLazyGesuchModelManagerHttpCalls($httpBackend);
            authServiceRS.loginRequest(user).then((response: TSUser) => {
                cookieUser = response;
            });
            $rootScope.$apply();
            $timeout.flush();

            expect($http.post).toHaveBeenCalled();
            expect(cookieUser.vorname).toEqual(user.vorname);
            expect(cookieUser.nachname).toEqual(user.nachname);
            expect(cookieUser.password).toEqual('');
            expect(cookieUser.email).toEqual(user.email);
            expect(cookieUser.role).toEqual(user.role);
        });
        it('sends a logrequest to server', () => {
            authServiceRS.logoutRequest();
            $rootScope.$apply();
            expect($http.post).toHaveBeenCalledWith('/ebegu/api/v1/auth/logout', null);
            expect(authServiceRS.getPrincipal()).toBeUndefined();
        });
    });

});
