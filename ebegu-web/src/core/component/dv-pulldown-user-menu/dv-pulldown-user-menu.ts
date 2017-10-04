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
import {IStateService} from 'angular-ui-router';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
require('./dv-pulldown-user-menu.less');
let template = require('./dv-pulldown-user-menu.html');

export class DvPulldownUserMenuComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = DvPulldownUserMenuController;
    controllerAs = 'vm';
}

export class DvPulldownUserMenuController {

    static $inject: any[] = ['$state', 'AuthServiceRS'];
    TSRoleUtil = TSRoleUtil;

    constructor(private $state: IStateService, private authServiceRS: AuthServiceRS) {
        this.TSRoleUtil = TSRoleUtil;
    }

    public logout(): void {
        this.$state.go('login', {type: 'logout'});
    }

    public getPrincipal() {
        return this.authServiceRS.getPrincipal();
    }

    public getVersion(): string {
        return VERSION;
    }

    public getBuildtimestamp(): string {
        return BUILDTSTAMP;
    }

}
