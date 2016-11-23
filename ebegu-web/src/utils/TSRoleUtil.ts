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
        return [TSRole.ADMIN]; //TODO (team) Superadmin
    }

    public static getTraegerschaftInstitutionRoles(): Array<TSRole> {
        return [TSRole.SACHBEARBEITER_INSTITUTION, TSRole.SACHBEARBEITER_TRAEGERSCHAFT];
    }

    public static getGesuchstellerJugendamtRoles(): Array<TSRole> {
        return [TSRole.GESUCHSTELLER, TSRole.SACHBEARBEITER_JA, TSRole.ADMIN]; // TODO Superadmin
    }

    public static getAdministratorJugendamtRole(): Array<TSRole> {
        return [TSRole.ADMIN, TSRole.SACHBEARBEITER_JA];
    }

    public static getAllButAdministratorJugendamtRole(): Array<string> {
        return TSRoleUtil.getAllRoles().filter(element =>
            element !== TSRole[TSRole.SACHBEARBEITER_JA] && element !== TSRole[TSRole.ADMIN]
        );
    }

    public static getAllRolesButTraegerschaftInstitution(): Array<string> {
        return TSRoleUtil.getAllRoles().filter(element =>
            element !== TSRole[TSRole.SACHBEARBEITER_INSTITUTION] && element !== TSRole[TSRole.SACHBEARBEITER_TRAEGERSCHAFT]
        );
    }

    public static getSchulamtRoles(): Array<TSRole> {
        return [TSRole.SCHULAMT]; //TODO Superadmin
    }

    public static getGesuchstellerRoles(): Array<TSRole> {
        return [TSRole.GESUCHSTELLER]; //TODO Superadmin?
    }

    public static getAllRolesForKommentarSpalte(): Array<TSRole> {
        return [TSRole.ADMIN, TSRole.SACHBEARBEITER_JA, TSRole.STEUERAMT, TSRole.SCHULAMT]; //TODO Superadmin
    }
}
