import {TSRole} from '../models/enums/TSRole';
/**
 * Hier findet man unterschiedliche Hilfsmethoden, um die Rollen von TSRole zu holen
 */
export class TSRoleUtil {

    static getAllRolesButGesuchsteller(): Array<string> {
        return TSRoleUtil.getAllRoles().filter(element =>
            element !== TSRole[TSRole.GESUCHSTELLER]
        );
    }

    public static getAllRoles(): Array<string> {
        // return Object.keys(TSRole).map(k => TSRole[k]);
        let result: Array<string> = [];
        for (var prop in TSRole) {
            if ((isNaN(parseInt(prop)))) {
                result.push(prop);
            }
        }
        return result;
    }

    public static getTraegerschaftInstitutionRoles(): Array<TSRole> {
        return [TSRole.SACHBEARBEITER_INSTITUTION, TSRole.SACHBEARBEITER_TRAEGERSCHAFT];
    }
}
