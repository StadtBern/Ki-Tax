import {TSRole} from '../models/enums/TSRole';
/**
 * Hier findet man unterschiedliche Hilfsmethoden, um die Rollen von TSRole zu holen
 */
export class TSRoleUtil {

    public static getAllRolesButGesuchsteller(): Array<string> {
        return TSRoleUtil.getAllRoles().filter(element =>
            element !== TSRole[TSRole.GESUCHSTELLER]
        );
    }

    public static getAllRoles(): Array<string> {
        let result: Array<string> = [];
        for (var prop in TSRole) {
            if ((isNaN(parseInt(prop)))) {
                result.push(prop);
            }
        }
        return result;
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

    public static getGesuchstellerJugendamtRoles(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.GESUCHSTELLER, TSRole.SACHBEARBEITER_JA, TSRole.ADMIN];
    }

    public static getAdministratorJugendamtRole(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN, TSRole.SACHBEARBEITER_JA];
    }

    public static getJugendamtRole(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN, TSRole.SACHBEARBEITER_JA, TSRole.JURIST, TSRole.REVISOR];
    }
    public static getJugendamtAndSchulamtRole(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN, TSRole.SACHBEARBEITER_JA, TSRole.JURIST, TSRole.REVISOR, TSRole.SCHULAMT];
    }

    public static getAdministratorJugendamtSchulamtRoles(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN, TSRole.SACHBEARBEITER_JA, TSRole.SCHULAMT];
    }

    public static getAllButAdministratorJugendamtRole(): Array<string> {
        return TSRoleUtil.getAllRoles().filter(element =>
            element !== TSRole[TSRole.SACHBEARBEITER_JA] &&
            element !== TSRole[TSRole.ADMIN] &&
            element !== TSRole[TSRole.SUPER_ADMIN]
        );
    }

    public static getAllRolesButTraegerschaftInstitution(): Array<string> {
        return TSRoleUtil.getAllRoles().filter(element =>
            element !== TSRole[TSRole.SACHBEARBEITER_INSTITUTION] && element !== TSRole[TSRole.SACHBEARBEITER_TRAEGERSCHAFT]
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

    public static getAllRolesForKommentarSpalte(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN, TSRole.SACHBEARBEITER_JA, TSRole.STEUERAMT, TSRole.SCHULAMT];
    }
}
