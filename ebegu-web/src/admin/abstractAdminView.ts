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

import {TSRole} from '../models/enums/TSRole';
import {TSRoleUtil} from '../utils/TSRoleUtil';
import AuthServiceRS from '../authentication/service/AuthServiceRS.rest';
import TSGesuchsperiode from '../models/TSGesuchsperiode';
import {TSGesuchsperiodeStatus} from '../models/enums/TSGesuchsperiodeStatus';

export default class AbstractAdminViewController {

    TSRole: any;
    TSRoleUtil: any;

    constructor(public authServiceRS: AuthServiceRS) {
        this.TSRole = TSRole;
        this.TSRoleUtil = TSRoleUtil;
    }

    $onInit() {
    }

    public isReadonly(): boolean {
        return !this.authServiceRS.isOneOfRoles(TSRoleUtil.getJAAdministratorRoles());
    }

    public periodenParamsEditableForPeriode(gesuchsperiode: TSGesuchsperiode): boolean {
        if (gesuchsperiode && gesuchsperiode.status) {
            // Fuer SuperAdmin immer auch editierbar, wenn AKTIV oder INAKTIV, sonst nur ENTWURF
            if (TSGesuchsperiodeStatus.GESCHLOSSEN === gesuchsperiode.status) {
                return false;
            } else if (this.authServiceRS.isOneOfRoles(this.TSRoleUtil.getSuperAdminRoles())) {
                return true;
            } else {
                return TSGesuchsperiodeStatus.ENTWURF === gesuchsperiode.status;
            }
        }
        return false;
    }
}
