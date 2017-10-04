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

import {IRootScopeService, IWindowService} from 'angular';
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
