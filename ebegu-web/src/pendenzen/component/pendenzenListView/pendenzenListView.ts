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

import {IComponentOptions} from 'angular';
import TSAntragDTO from '../../../models/TSAntragDTO';
import PendenzRS from '../../service/PendenzRS.rest';
import * as moment from 'moment';
import ITimeoutService = angular.ITimeoutService;
import Moment = moment.Moment;
import TSUser from '../../../models/TSUser';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
let template = require('./pendenzenListView.html');
require('./pendenzenListView.less');

export class PendenzenListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = PendenzenListViewController;
    controllerAs = 'vm';
}

export class PendenzenListViewController {

    private pendenzenList: Array<TSAntragDTO>;
    totalResultCount: string = '0';

    static $inject: string[] = ['PendenzRS', 'CONSTANTS', 'AuthServiceRS'];

    constructor(public pendenzRS: PendenzRS, private CONSTANTS: any, private authServiceRS: AuthServiceRS) {
        this.initViewModel();
    }

    private initViewModel() {
        // Initial werden die Pendenzen des eingeloggten Benutzers geladen
        this.updatePendenzenList(this.authServiceRS.getPrincipal().username);
    }

    private updatePendenzenList(username: string) {
        this.pendenzRS.getPendenzenListForUser(username).then((response: any) => {
            this.pendenzenList = angular.copy(response);
            if (this.pendenzenList && this.pendenzenList.length) {
                this.totalResultCount = this.pendenzenList.length.toString();
            } else {
                this.totalResultCount = '0';
            }
        });
    }

    public getPendenzenList(): Array<TSAntragDTO> {
        return this.pendenzenList;
    }

    public userChanged(user: TSUser): void {
        if (user) {
            this.updatePendenzenList(user.username);
        }
    }
}
