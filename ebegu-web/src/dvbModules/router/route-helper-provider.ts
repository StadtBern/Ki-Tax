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

import {IState, IUrlRouterProvider, IStateProvider} from 'angular-ui-router';
import {IServiceProvider, ILocationProvider} from 'angular';

export class RouterHelper {
    static $inject = ['$stateProvider', '$urlRouterProvider'];

    hasOtherwise: boolean;
    stateProvider: IStateProvider;
    urlRouterProvider: IUrlRouterProvider;

    /* @ngInject */
    constructor($stateProvider: IStateProvider, $urlRouterProvider: IUrlRouterProvider) {
        this.hasOtherwise = false;
        this.stateProvider = $stateProvider;
        this.urlRouterProvider = $urlRouterProvider;
    }

    public configureStates(states: IState[], otherwisePath?: string): void {
        states.forEach((state) => {
            this.stateProvider.state(state);
        });
        if (otherwisePath && !this.hasOtherwise) {
            this.hasOtherwise = true;
            this.urlRouterProvider.otherwise(otherwisePath);
        }
    }

    public getStates(): IState[] {
        return this.stateProvider.$get();
    }
}

export default class RouterHelperProvider implements IServiceProvider {
    static $inject = ['$locationProvider', '$stateProvider', '$urlRouterProvider'];

    private routerHelper: RouterHelper;

    /* @ngInject */
    constructor($locationProvider: ILocationProvider, $stateProvider: IStateProvider, $urlRouterProvider: IUrlRouterProvider) {
        $locationProvider.html5Mode(false);
        this.routerHelper = new RouterHelper($stateProvider, $urlRouterProvider);
    }

    $get(): RouterHelper {
        return this.routerHelper;
    }
}
