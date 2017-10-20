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
import {EbeguWebCore} from '../../../core/core.module';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import UserRS from '../../../core/service/userRS.rest';
import {TSEingangsart} from '../../../models/enums/TSEingangsart';
import TSFall from '../../../models/TSFall';
import TSGesuch from '../../../models/TSGesuch';
import TSUser from '../../../models/TSUser';
import EbeguUtil from '../../../utils/EbeguUtil';
import {IGesuchStateParams} from '../../gesuch.route';
import BerechnungsManager from '../../service/berechnungsManager';
import FallRS from '../../service/fallRS.rest';
import GesuchModelManager from '../../service/gesuchModelManager';
import GesuchRS from '../../service/gesuchRS.rest';
import {GesuchToolbarController} from './gesuchToolbar';

describe('gesuchToolbar', function () {

    let gesuchModelManager: GesuchModelManager;
    let gesuchToolbarController: GesuchToolbarController;
    let userRS: UserRS;
    let authServiceRS: AuthServiceRS;
    let ebeguUtil: EbeguUtil;
    let CONSTANTS: any;
    let gesuchRS: GesuchRS;
    let berechnungsManager: BerechnungsManager;
    let $state: angular.ui.IStateService;
    let $stateParams: IGesuchStateParams;
    let $scope: angular.IScope;
    let $rootScope: angular.IRootScopeService;
    let user: TSUser;
    let $mdSidenav: angular.material.ISidenavService;
    let gesuchsperiodeRS: GesuchsperiodeRS;
    let fallRS: FallRS;
    let dvDialog: DvDialog;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        userRS = $injector.get('UserRS');
        authServiceRS = $injector.get('AuthServiceRS');
        ebeguUtil = $injector.get('EbeguUtil');
        CONSTANTS = $injector.get('CONSTANTS');
        gesuchRS = $injector.get('GesuchRS');
        berechnungsManager = $injector.get('BerechnungsManager');
        $state = $injector.get('$state');
        ebeguUtil = $injector.get('EbeguUtil');
        $stateParams = $injector.get('$stateParams');
        $rootScope = $injector.get('$rootScope');
        $mdSidenav = $injector.get('$mdSidenav');
        $scope = $rootScope.$new();
        user = new TSUser('Emiliano', 'Camacho');
        $stateParams.gesuchId = '123456789';
        gesuchsperiodeRS = $injector.get('GesuchsperiodeRS');
        fallRS = $injector.get('FallRS');
        dvDialog = $injector.get('DvDialog');

        gesuchToolbarController = new GesuchToolbarController(userRS, ebeguUtil,
            CONSTANTS, gesuchRS, $state, $stateParams, $scope, gesuchModelManager,
            authServiceRS, $mdSidenav, undefined, gesuchsperiodeRS, fallRS, dvDialog, undefined);
    }));

    describe('getVerantwortlicherFullName', () => {
        it('returns empty string for empty verantwortlicher', () => {
            expect(gesuchToolbarController.getVerantwortlicherFullName()).toEqual('');
        });

        it('returns the fullname of the verantwortlicher', () => {
            let verantwortlicher: TSUser = new TSUser('Emiliano', 'Camacho');
            spyOn(authServiceRS, 'getPrincipal').and.returnValue(verantwortlicher);
            spyOn(authServiceRS, 'isOneOfRoles').and.returnValue(true);
            gesuchModelManager.initGesuch(true, TSEingangsart.PAPIER);
            expect(gesuchToolbarController.getVerantwortlicherFullName()).toEqual('Emiliano Camacho');
        });
    });
    describe('setVerantwortlicher()', () => {
        it('does nothing if the passed user is empty, verantwortlicher remains as it was before', () => {
            createGesuch();
            spyOn(gesuchModelManager, 'setUserAsFallVerantwortlicher');
            spyOn(gesuchModelManager, 'updateFall');

            gesuchToolbarController.setVerantwortlicher(undefined);
            expect(gesuchModelManager.getGesuch().fall.verantwortlicher).toBe(user);
        });
        it('sets the user as the verantwortlicher of the current fall', () => {
            createGesuch();
            spyOn(gesuchModelManager, 'setUserAsFallVerantwortlicher');
            spyOn(gesuchModelManager, 'updateFall');

            let newUser: TSUser = new TSUser('Adolfo', 'Contreras');
            gesuchToolbarController.setVerantwortlicher(newUser);
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
