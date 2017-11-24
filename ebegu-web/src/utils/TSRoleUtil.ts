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

import {getTSRoleValues, TSRole} from '../models/enums/TSRole';

/**
 * Hier findet man unterschiedliche Hilfsmethoden, um die Rollen von TSRole zu holen
 */
export class TSRoleUtil {

    public static getAllRolesButGesuchsteller(): Array<TSRole> {
        return TSRoleUtil.getAllRoles().filter(element =>
            element !== TSRole.GESUCHSTELLER
        );
    }

    public static getAllRolesForMenuAlleVerfuegungen(): Array<TSRole> {
        return TSRoleUtil.getAllRoles().filter(element =>
            element !== TSRole.SCHULAMT && element !== TSRole.STEUERAMT
        );
    }

    public static getAllRoles(): Array<TSRole> {
        return getTSRoleValues();
    }

    public static getSuperAdminRoles(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN];
    }

    public static getAdministratorRoles(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN];
    }

    public static getTraegerschaftInstitutionRoles(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.SACHBEARBEITER_INSTITUTION, TSRole.SACHBEARBEITER_TRAEGERSCHAFT];
    }

    public static getTraegerschaftInstitutionOnlyRoles(): Array<TSRole> {
        return [TSRole.SACHBEARBEITER_INSTITUTION, TSRole.SACHBEARBEITER_TRAEGERSCHAFT];
    }

    public static getTraegerschaftInstitutionSteueramtOnlyRoles(): Array<TSRole> {
        return [TSRole.SACHBEARBEITER_INSTITUTION, TSRole.SACHBEARBEITER_TRAEGERSCHAFT, TSRole.STEUERAMT];
    }

    public static getGesuchstellerJugendamtRoles(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.GESUCHSTELLER, TSRole.SACHBEARBEITER_JA, TSRole.ADMIN];
    }

    public static getAdministratorJugendamtRole(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN, TSRole.SACHBEARBEITER_JA];
    }

    public static getAdministratorRevisorRole(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN, TSRole.REVISOR];
    }

    public static getJugendamtRole(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN, TSRole.SACHBEARBEITER_JA, TSRole.JURIST, TSRole.REVISOR];
    }

    public static getGesuchstellerJugendamtSchulamtOtherAmtRoles(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN, TSRole.SACHBEARBEITER_JA, TSRole.JURIST, TSRole.REVISOR, TSRole.GESUCHSTELLER, TSRole.SCHULAMT];
    }

    public static getGesuchstellerJugendamtOtherAmtRoles(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN, TSRole.SACHBEARBEITER_JA, TSRole.JURIST, TSRole.REVISOR, TSRole.GESUCHSTELLER];
    }

    public static getJugendamtAndSchulamtRole(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN, TSRole.SACHBEARBEITER_JA, TSRole.JURIST, TSRole.REVISOR, TSRole.SCHULAMT];
    }

    public static getAdministratorJugendamtSchulamtRoles(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN, TSRole.SACHBEARBEITER_JA, TSRole.SCHULAMT];
    }

    public static getAdministratorJugendamtSchulamtSteueramtRoles(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN, TSRole.SACHBEARBEITER_JA, TSRole.SCHULAMT, TSRole.STEUERAMT];
    }

    public static getAllButAdministratorJugendamtRole(): Array<TSRole> {
        return TSRoleUtil.getAllRoles().filter(element =>
            element !== TSRole.SACHBEARBEITER_JA &&
            element !== TSRole.ADMIN  &&
            element !== TSRole.SUPER_ADMIN
        );
    }

    public static getAllButAdministratorJugendamtRoleAsRoles(): Array<TSRole> {
        return getTSRoleValues().filter(element =>
            element !== TSRole.SACHBEARBEITER_JA &&
            element !== TSRole.ADMIN &&
            element !== TSRole.SUPER_ADMIN
        );
    }

    public static getAllRolesButTraegerschaftInstitution(): Array<TSRole> {
        return TSRoleUtil.getAllRoles().filter(element =>
            element !== TSRole.SACHBEARBEITER_INSTITUTION && element !== TSRole.SACHBEARBEITER_TRAEGERSCHAFT
        );
    }

    public static getAllRolesButTraegerschaftInstitutionSteueramt(): Array<TSRole> {
        return TSRoleUtil.getAllRoles().filter(element =>
            element !== TSRole.SACHBEARBEITER_INSTITUTION && element !== TSRole.SACHBEARBEITER_TRAEGERSCHAFT
            && element !== TSRole.STEUERAMT
        );
    }

    public static getAllRolesButSteueramt(): Array<TSRole> {
        return TSRoleUtil.getAllRoles().filter(element =>
            element !== TSRole.STEUERAMT
        );
    }

    public static getSchulamtOnlyRoles(): Array<TSRole> {
        return [TSRole.SCHULAMT];
    }

    public static getGesuchstellerRoles(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.GESUCHSTELLER];
    }

    public static getGesuchstellerOnlyRoles(): Array<TSRole> {
        return [TSRole.GESUCHSTELLER];
    }

    public static getSteueramtOnlyRoles(): Array<TSRole> {
        return [TSRole.STEUERAMT];
    }

    public static getJuristOnlyRoles(): Array<TSRole> {
        return [TSRole.JURIST];
    }

    public static getRevisorOnlyRoles(): Array<TSRole> {
        return [TSRole.REVISOR];
    }

    public static getReadOnlyRoles(): Array<TSRole> {
        return [TSRole.REVISOR, TSRole.JURIST, TSRole.SCHULAMT, TSRole.STEUERAMT];
    }

    public static getAllRolesForKommentarSpalte(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN, TSRole.SACHBEARBEITER_JA, TSRole.STEUERAMT, TSRole.SCHULAMT, TSRole.JURIST, TSRole.REVISOR];
    }
}
