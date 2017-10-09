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

import TSUser from '../models/TSUser';
import {IStateService} from 'angular-ui-router';
import {TSRoleUtil} from './TSRoleUtil';
import {TSRole} from '../models/enums/TSRole';

export default class AuthenticationUtil {

    /**
     *  Navigiert basierend auf der Rolle zu einer anderen Startseite
     */
    public static navigateToStartPageForRole(user: TSUser, $state: IStateService): void {
        if (user.role === TSRole.SUPER_ADMIN) {
            $state.go('faelle');
        } else if (TSRoleUtil.getAdministratorJugendamtRole().indexOf(user.role) > -1) {
            $state.go('pendenzen');
        } else if (TSRoleUtil.getTraegerschaftInstitutionOnlyRoles().indexOf(user.role) > -1) {
            $state.go('pendenzenInstitution');
        } else if (TSRoleUtil.getSchulamtOnlyRoles().indexOf(user.role) > -1) {
            $state.go('faelle');
        } else if (TSRoleUtil.getSteueramtOnlyRoles().indexOf(user.role) > -1) {
            $state.go('pendenzenSteueramt');
        } else if (TSRoleUtil.getGesuchstellerOnlyRoles().indexOf(user.role) > -1) {
            $state.go('gesuchstellerDashboard');
        } else if (TSRoleUtil.getJuristOnlyRoles().indexOf(user.role) > -1) {
            $state.go('faelle');
        } else if (TSRoleUtil.getRevisorOnlyRoles().indexOf(user.role) > -1) {
            $state.go('faelle');
        } else {
            console.error('Achtung, keine Startpage definiert fuer Rolle ', user.getRoleKey(), ', nehme gesuchstellerDashboard');
            $state.go('gesuchstellerDashboard');
        }
    }
}
