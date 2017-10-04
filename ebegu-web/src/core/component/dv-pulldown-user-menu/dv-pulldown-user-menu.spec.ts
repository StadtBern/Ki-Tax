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

import {EbeguWebCore} from '../../core.module';
import {IStateService} from 'angular-ui-router';
import {IQService, IScope} from 'angular';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {DvPulldownUserMenuController} from './dv-pulldown-user-menu';

describe('DvPulldownUserMenuController', function () {

    let authServiceRS: AuthServiceRS;
    let $state: IStateService;
    let controller: DvPulldownUserMenuController;
    let $q: IQService;
    let scope: IScope;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        authServiceRS = $injector.get('AuthServiceRS');
        scope = $injector.get('$rootScope').$new();
        $q = $injector.get('$q');
        $state = $injector.get('$state');
    }));

    beforeEach(() => {
        controller = new DvPulldownUserMenuController($state, authServiceRS);
    });

    describe('API Usage', function () {
        describe('logout()', () => {
            it('must call the logout function and redirect to the login page', () => {
                spyOn($state, 'go');

                controller.logout();
                scope.$apply();
                //actual logout happens on login page
                expect($state.go).toHaveBeenCalledWith('login', {type: 'logout'});
            });
        });
    });

});
