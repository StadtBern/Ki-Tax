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
import {IState, IStateParamsService} from 'angular-ui-router';
import GesuchModelManager from '../gesuch/service/gesuchModelManager';
import TSGesuch from '../models/TSGesuch';
import IQService = angular.IQService;
import ILogService = angular.ILogService;
import IPromise = angular.IPromise;

gesuchstellerDashboardRun.$inject = ['RouterHelper'];

/* @ngInject */
export function gesuchstellerDashboardRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/start');
}

function getStates(): IState[] {
    return [
        new EbeguGesuchstellerDashboardState(),
        new EbeguCreateAngebotState()
    ];
}

//STATES

export class EbeguGesuchstellerDashboardState implements IState {
    name = 'gesuchstellerDashboard';
    template = '<gesuchsteller-dashboard-view class="layout-column flex-100">';
    url = '/gesuchstellerDashboard';
}

export class EbeguCreateAngebotState implements IState {
    name = 'createAngebot';
    template = '<create-angebot-view class="layout-column flex-100">';
    url = '/createAngebotView/:type/:gesuchId';

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class IAngebotStateParams implements IStateParamsService {
    gesuchId: string;
    type: string;
}

getGesuchModelManager.$inject = ['GesuchModelManager', '$stateParams', '$q', '$log'];

/* @ngInject */
export function getGesuchModelManager(gesuchModelManager: GesuchModelManager, $stateParams: IAngebotStateParams, $q: IQService,
                                      $log: ILogService): IPromise<TSGesuch> {
    if ($stateParams) {
        let gesuchIdParam = $stateParams.gesuchId;
        if (gesuchIdParam) {
            if (!gesuchModelManager.getGesuch() || gesuchModelManager.getGesuch() && gesuchModelManager.getGesuch().id !== gesuchIdParam
                || gesuchModelManager.getGesuch().emptyCopy) {
                // Wenn die antrags id im GescuchModelManager nicht mit der GesuchId ueberreinstimmt wird das gesuch neu geladen
                // Ebenfalls soll das Gesuch immer neu geladen werden, wenn es sich beim Gesuch im Gesuchmodelmanager um eine leere Mutation handelt
                // oder um ein leeres Erneuerungsgesuch

                return gesuchModelManager.openGesuch(gesuchIdParam);
            } else {
                let deferred = $q.defer<TSGesuch>();
                deferred.resolve(gesuchModelManager.getGesuch());
                return deferred.promise;
            }

        }
    }
    $log.warn('keine stateParams oder keine gesuchId, gebe undefined zurueck');
    let deferred = $q.defer<TSGesuch>();
    deferred.resolve(undefined);
    return deferred.promise;
}

