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

import {ILogService} from 'angular';
import IRootScopeService = angular.IRootScopeService;

/**
 * Dieser Service soll helfen die Events die vom angular-ui-route Service geworfen werden zu debuggen
 */
export class NavigationLogger {

    active: boolean = false;

    static $inject = ['$rootScope', '$log'];
    /* @ngInject */
    constructor($rootScope: IRootScopeService, private $log: ILogService) {

        $rootScope.$on('$stateChangeStart',  (event, toState, toParams, fromState, fromParams)  => {
            if (this.active) {
                $log.debug('$stateChangeStart --- event, toState, toParams, fromState, fromParams');
                $log.debug(event, toState, toParams, fromState, fromParams);
            }
        });
        $rootScope.$on('$stateChangeError',  (event, toState, toParams, fromState, fromParams, error) => {
            if (this.active) {
                $log.debug('$stateChangeError --- event, toState, toParams, fromState, fromParams, error');
                $log.debug(event, toState, toParams, fromState, fromParams, error);
            }
        });
        $rootScope.$on('$stateChangeSuccess',  (event, toState, toParams, fromState, fromParams) => {
            if (this.active) {
                $log.debug('$stateChangeSuccess --- event, toState, toParams, fromState, fromParams');
                $log.debug(event, toState, toParams, fromState, fromParams);
            }
        });
        $rootScope.$on('$viewContentLoading',  (event, viewConfig) => {
            if (this.active) {
                $log.debug('$viewContentLoading --- event, viewConfig');
                $log.debug(event, viewConfig);
            }
        });
        $rootScope.$on('$viewContentLoaded',  (event) => {
            if (this.active) {
                $log.debug('$viewContentLoaded --- event');
                $log.debug(event);
            }
        });
        $rootScope.$on('$stateNotFound',  (event, unfoundState, fromState, fromParams) => {
            if (this.active) {
                $log.debug('$stateNotFound --- event, unfoundState, fromState, fromParams');
                $log.debug(event, unfoundState, fromState, fromParams);
            }
        });

    }

    public toggle() {
        this.active = !this.active;
    }
}
