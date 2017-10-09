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
import {ApplicationPropertyRS} from '../admin/service/applicationPropertyRS.rest';
import IStateParamsService = angular.ui.IStateParamsService;
import IQService = angular.IQService;
import ILogService = angular.ILogService;
import IPromise = angular.IPromise;

authenticationRun.$inject = ['RouterHelper'];

/* @ngInject */
export function authenticationRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/start');
}

function getStates(): IState[] {
    return [
        new EbeguLoginState(),
        new EbeguLocalLoginState(),
        new EbeguSchulungState(),
        new EbeguStartState()
    ];
}

//STATES

export class EbeguLoginState implements IState {
    name = 'login';
    template = '<authentication-view>';
    //HINWEIS: Soweit ich sehen kann koennen url navigationen mit mehr als einem einzigen slash am Anfang nicht manuell in der Adressbar aufgerufen werden?
    url = '/login?type&relayPath';
}

export class EbeguLocalLoginState implements IState {
    name = 'locallogin';
    template = '<dummy-authentication-view flex="auto" class="overflow-scroll">';
    url = '/locallogin';
    resolve = {
        dummyLoginEnabled: readDummyLoginEnabled
    };
}

export class EbeguSchulungState implements IState {
    name = 'schulung';
    template = '<schulung-view flex="auto" class="overflow-scroll">';
    url = '/schulung';
    resolve = {
        dummyLoginEnabled: readDummyLoginEnabled
    };
}

export class EbeguStartState implements IState {
    name = 'start';
    template = '<start-view>';
    url = '/start';
}

export class IAuthenticationStateParams implements IStateParamsService {
    relayPath: string;
    type: string;
}

readDummyLoginEnabled.$inject = ['ApplicationPropertyRS', '$state', '$q', '$log'];

/* @ngInject */
export function readDummyLoginEnabled(applicationPropertyRS: ApplicationPropertyRS, $state: IStateService, $q: IQService,
                                      $log: ILogService): IPromise<boolean> {
    return applicationPropertyRS.isDummyMode()
        .then((response: boolean) => {
            if (response === false) {
                $log.debug('page is disabled');
                $state.go('start');
            }
            return response;
        }).catch(() => {
            let deferred = $q.defer<boolean>();
            deferred.resolve(undefined);
            $state.go('login');
            return deferred.promise;
        });

}

