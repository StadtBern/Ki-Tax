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
import {IState} from 'angular-ui-router';

gesuchstellerDashboardRun.$inject = ['RouterHelper'];

/* @ngInject */
export function gesuchstellerDashboardRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/start');
}

function getStates(): IState[] {
    return [
        new EbeguGesuchstellerDashboardState()
    ];
}

//STATES

export class EbeguGesuchstellerDashboardState implements IState {
    name = 'gesuchstellerDashboard';
    template = '<gesuchsteller-dashboard-view class="layout-column flex-100">';
    url = '/gesuchstellerDashboard';
}
