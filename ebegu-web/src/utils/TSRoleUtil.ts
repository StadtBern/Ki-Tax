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

    public static getAdministratorRoles(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN];
    }

    public static getTraegerschaftInstitutionRoles(): Array<TSRole> {
        return this.getTraegerschaftInstitutionRoles(false);
    }

    public static getTraegerschaftInstitutionRoles(excludeSuperAdmin: boolean): Array<TSRole> {
        if (excludeSuperAdmin) {
            return [TSRole.SACHBEARBEITER_INSTITUTION, TSRole.SACHBEARBEITER_TRAEGERSCHAFT];
        } else {
            return [TSRole.SUPER_ADMIN, TSRole.SACHBEARBEITER_INSTITUTION, TSRole.SACHBEARBEITER_TRAEGERSCHAFT];
        }
    }

    public static getGesuchstellerJugendamtRoles(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.GESUCHSTELLER, TSRole.SACHBEARBEITER_JA, TSRole.ADMIN]; // TODO Superadmin
    }

    public static getAdministratorJugendamtRole(): Array<TSRole> {
        return this.getAdministratorJugendamtRole(false);
    }

    public static getAdministratorJugendamtRole(excludeSuperAdmin :boolean): Array<TSRole> {
        if (excludeSuperAdmin) {
            return [TSRole.ADMIN, TSRole.SACHBEARBEITER_JA];
        } else {
            return [TSRole.SUPER_ADMIN, TSRole.ADMIN, TSRole.SACHBEARBEITER_JA];
        }
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

    public static getSchulamtRoles(): Array<TSRole> {
        return this.getSchulamtRoles(false);
    }

    public static getSchulamtRoles(excludeSuperAdmin :boolean): Array<TSRole> {
        if (excludeSuperAdmin) {
            return [TSRole.SCHULAMT];
        } else {
            return [TSRole.SUPER_ADMIN, TSRole.SCHULAMT];
        }
    }

    public static getGesuchstellerRoles(): Array<TSRole> {
        return this.getGesuchstellerRoles(false);
    }

    public static getGesuchstellerRoles(excludeSuperAdmin :boolean): Array<TSRole> {
        if (excludeSuperAdmin) {
            return [TSRole.GESUCHSTELLER];
        } else {
            return [TSRole.SUPER_ADMIN, TSRole.GESUCHSTELLER];
        }
    }

    public static getAllRolesForKommentarSpalte(): Array<TSRole> {
        return [TSRole.SUPER_ADMIN, TSRole.ADMIN, TSRole.SACHBEARBEITER_JA, TSRole.STEUERAMT, TSRole.SCHULAMT]; //TODO Superadmin
    }
}
