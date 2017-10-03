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

import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState, IStateService} from 'angular-ui-router';
import ListResourceRS from './service/listResourceRS.rest';
import {MandantRS} from './service/mandantRS.rest';
import {TSAuthEvent} from '../models/enums/TSAuthEvent';
import AuthServiceRS from '../authentication/service/AuthServiceRS.rest';
import TSUser from '../models/TSUser';
import {TSRoleUtil} from '../utils/TSRoleUtil';
import ErrorService from './errors/service/ErrorService';
import {ApplicationPropertyRS} from '../admin/service/applicationPropertyRS.rest';
import TSApplicationProperty from '../models/TSApplicationProperty';
import GesuchModelManager from '../gesuch/service/gesuchModelManager';
import IRootScopeService = angular.IRootScopeService;
import ITimeoutService = angular.ITimeoutService;
import ILocationService = angular.ILocationService;
import ILogService = angular.ILogService;
import IInjectorService = angular.auto.IInjectorService;
import GesuchsperiodeRS from './service/gesuchsperiodeRS.rest';

appRun.$inject = ['angularMomentConfig', 'RouterHelper', 'ListResourceRS', 'MandantRS', '$injector', '$rootScope', 'hotkeys',
    '$timeout', 'AuthServiceRS', '$state', '$location', '$window', '$log' , 'ErrorService', 'GesuchModelManager', 'GesuchsperiodeRS'];

/* @ngInject */
export function appRun(angularMomentConfig: any, routerHelper: RouterHelper, listResourceRS: ListResourceRS,
                       mandantRS: MandantRS, $injector: IInjectorService, $rootScope: IRootScopeService, hotkeys: any, $timeout: ITimeoutService,
                       authServiceRS: AuthServiceRS, $state: IStateService, $location: ILocationService, $window: ng.IWindowService,
                       $log: ILogService, errorService: ErrorService, gesuchModelManager: GesuchModelManager,
                       gesuchsperiodeRS: GesuchsperiodeRS) {
    // navigationLogger.toggle();

    // Fehler beim Navigieren ueber ui-route ins Log schreiben
    $rootScope.$on('$stateChangeError',  (event, toState, toParams, fromState, fromParams, error) => {
        $log.error('Fehler beim Navigieren');
        $log.error('$stateChangeError --- event, toState, toParams, fromState, fromParams, error');
        $log.error(event, toState, toParams, fromState, fromParams, error);
    });
    //Normale Benutzer duefen nicht auf admin Seite
    $rootScope.$on('$stateChangeStart',
        (event, toState, toParams, fromState, fromParams, options) => {
            let principal: TSUser = authServiceRS.getPrincipal();
            let forbiddenPlaces = ['admin', 'institution', 'parameter', 'traegerschaft'];
            let isAdmin: boolean = authServiceRS.isOneOfRoles(TSRoleUtil.getAdministratorRevisorRole());
            if (toState && forbiddenPlaces.indexOf(toState.name) !== -1 && authServiceRS.getPrincipal() && !isAdmin) {
                errorService.addMesageAsError('ERROR_UNAUTHORIZED');
                $log.debug('prevented navigation to page because user is not admin');
                event.preventDefault();
            }
        });
    $rootScope.$on('$stateChangeSuccess',  (event, toState, toParams, fromState, fromParams) => {
        errorService.clearAll();
    });

    routerHelper.configureStates(getStates(), '/start');
    angularMomentConfig.format = 'DD.MM.YYYY';
    // dieser call macht mit tests probleme, daher wird er fuer test auskommentiert


    // not used anymore?
    $rootScope.$on(TSAuthEvent[TSAuthEvent.LOGIN_SUCCESS], () => {
        //do stuff if needed
        if (ENV !== 'test') {
            listResourceRS.getLaenderList();  //initial aufruefen damit cache populiert wird
            mandantRS.getFirst();
        }
        //since we will need these lists anyway we already load on login
        gesuchsperiodeRS.updateActiveGesuchsperiodenList();
        gesuchsperiodeRS.updateNichtAbgeschlosseneGesuchsperiodenList();
        gesuchModelManager.updateFachstellenList();
        gesuchModelManager.updateActiveInstitutionenList();
    });


    $rootScope.$on(TSAuthEvent[TSAuthEvent.NOT_AUTHENTICATED], () => {
        //user is not yet authenticated, show loginpage

        let currentPath = angular.copy($location.absUrl());
        console.log('going to login page wiht current path ', currentPath);

        //wenn wir schon auf der lognseite oder im redirect sind redirecten wir nicht
        if (currentPath.indexOf('fedletSSOInit') === -1
            && ($state.current !== undefined && $state.current.name !== 'login')
            && ($state.current !== undefined && $state.current.name !== 'locallogin')
            && ($state.current !== undefined && $state.current.name !== 'schulung')
            && currentPath.indexOf('sendRedirectForValidation') === -1) {
            $state.go('login', {relayPath: currentPath, type: 'login'});
        } else {
            console.log('supressing redirect to ', currentPath);
        }

    });


    // Attempt to restore a user session upon startup
    if (authServiceRS.initWithCookie()) {
        $log.debug('logged in from cookie');
    }

    if (ENV !== 'test') {
        //Hintergrundfarbe anpassen (testsystem kann zB andere Farbe haben)
        let applicationPropertyRS = $injector.get<ApplicationPropertyRS>('ApplicationPropertyRS');
        applicationPropertyRS.getBackgroundColor().then((prop: TSApplicationProperty) => {
            if (prop && prop.value !== '#FFFFFF') {
                angular.element('#Intro').css('background-color', prop.value);
                angular.element('.user-menu').find('button').first().css('background', prop.value);
            }
        });
    }

    // Wir meochten eigentlich ueberall mit einem hotkey das formular submitten koennen
    //https://github.com/chieffancypants/angular-hotkeys#angular-hotkeys-
    hotkeys.add({
        combo: 'ctrl+shift+x',
        description: 'Press the last button with style class .next',
        callback: function () {
            $timeout(() => angular.element('.next').last().click());
        }
    });

}

function getStates(): IState[] {
    return [
        /* Add New States Above */
    ];
}
