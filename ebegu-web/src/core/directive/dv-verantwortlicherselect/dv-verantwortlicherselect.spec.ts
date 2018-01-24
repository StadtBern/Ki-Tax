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

import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import {TSEingangsart} from '../../../models/enums/TSEingangsart';
import TSFall from '../../../models/TSFall';
import TSGesuch from '../../../models/TSGesuch';
import TSUser from '../../../models/TSUser';
import {EbeguWebCore} from '../../core.module';
import UserRS from '../../service/userRS.rest';
import {VerantwortlicherselectController} from './dv-verantwortlicherselect';
import ITranslateService = angular.translate.ITranslateService;

describe('gesuchToolbar', function () {

    let gesuchModelManager: GesuchModelManager;
    let verantwortlicherselectController: VerantwortlicherselectController;
    let authServiceRS: AuthServiceRS;
    let userRS: UserRS;
    let user: TSUser;
    let $mdSidenav: angular.material.ISidenavService;
    let $translate: ITranslateService;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        authServiceRS = $injector.get('AuthServiceRS');
        userRS = $injector.get('UserRS');
        $mdSidenav = $injector.get('$mdSidenav');
        user = new TSUser('Emiliano', 'Camacho');
        $translate = $injector.get('$translate');

        verantwortlicherselectController = new VerantwortlicherselectController(userRS,
            authServiceRS, gesuchModelManager, $translate);
    }));

    describe('getVerantwortlicherFullName', () => {
        it('returns empty string for empty verantwortlicher', () => {
            expect(verantwortlicherselectController.getVerantwortlicherFullName()).toEqual('kein Verant.');
        });

        it('returns the fullname of the verantwortlicher', () => {
            let verantwortlicher: TSUser = new TSUser('Emiliano', 'Camacho');
            spyOn(authServiceRS, 'getPrincipal').and.returnValue(verantwortlicher);
            spyOn(authServiceRS, 'isOneOfRoles').and.returnValue(true);
            gesuchModelManager.initGesuch(true, TSEingangsart.PAPIER);
            expect(verantwortlicherselectController.getVerantwortlicherFullName()).toEqual('Emiliano Camacho');
        });
    });
    describe('setVerantwortlicher()', () => {
        it('does nothing if the passed user is empty, verantwortlicher remains as it was before', () => {
            createGesuch();
            spyOn(gesuchModelManager, 'setUserAsFallVerantwortlicher');
            spyOn(gesuchModelManager, 'updateFall');

            verantwortlicherselectController.setVerantwortlicher(undefined);
            expect(gesuchModelManager.getGesuch().fall.verantwortlicher).toBe(user);
        });
        it('sets the user as the verantwortlicher of the current fall', () => {
            createGesuch();
            spyOn(gesuchModelManager, 'setUserAsFallVerantwortlicher');
            spyOn(gesuchModelManager, 'updateFall');

            let newUser: TSUser = new TSUser('Adolfo', 'Contreras');
            verantwortlicherselectController.setVerantwortlicher(newUser);
            expect(gesuchModelManager.getGesuch().fall.verantwortlicher).toBe(newUser);
        });
    });

    function createGesuch() {
        let gesuch: TSGesuch = new TSGesuch();
        let fall: TSFall = new TSFall();
        fall.verantwortlicher = user;
        gesuch.fall = fall;
        gesuchModelManager.setGesuch(gesuch);
    }

});
