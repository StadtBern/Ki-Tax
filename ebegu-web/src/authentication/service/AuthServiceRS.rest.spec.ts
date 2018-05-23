/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import {EbeguWebCore} from '../../core/core.module';
import GesuchModelManager from '../../gesuch/service/gesuchModelManager';
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
    let gesuchModelManager: GesuchModelManager;

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
        gesuchModelManager = $injector.get('GesuchModelManager');
        spyOn(gesuchModelManager, 'getGesuchsperiode').and.returnValue(TestDataUtil.createGesuchsperiode20162017());
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
            // Der Inhalt der Cookie muss nicht unbedingt ein TSUser sein. Deswegen machen wir hier ein Objekt mit dem Inhalt, den die Cookie braucht
            let cookieContent: any = {vorname: 'Emma', nachname: 'Gerber', username: 'geem', email: 'emma.gerber@example.com', role: 'GESUCHSTELLER'};
            let encodedUser = base64.encode(JSON.stringify(cookieContent).split('_').join(''));
            spyOn($cookies, 'get').and.returnValue(encodedUser);

            let cookieUser: TSUser;
            //if we can decode the cookie the client application assumes the user is logged in for ui purposes
            TestDataUtil.mockLazyGesuchModelManagerHttpCalls($httpBackend);
            let user: TSUser = new TSUser('Emma', 'Gerber', 'geem', 'password5', 'emma.gerber@example.com', undefined, TSRole.GESUCHSTELLER);
            authServiceRS.loginRequest(user).then((response: TSUser) => {
                cookieUser = response;
            });
            $rootScope.$apply();
            $timeout.flush();

            expect($http.post).toHaveBeenCalled();
            expect(cookieUser.vorname).toEqual(cookieContent.vorname);
            expect(cookieUser.nachname).toEqual(cookieContent.nachname);
            expect(cookieUser.password).toEqual('');
            expect(cookieUser.email).toEqual(cookieContent.email);
            expect(cookieUser.currentBerechtigung.role).toEqual(cookieContent.role);
        });
        it('sends a logrequest to server', () => {
            authServiceRS.logoutRequest();
            $rootScope.$apply();
            expect($http.post).toHaveBeenCalledWith('/ebegu/api/v1/auth/logout', null);
            expect(authServiceRS.getPrincipal()).toBeUndefined();
        });
    });

});
