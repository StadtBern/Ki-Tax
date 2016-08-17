import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {TSRole} from '../../models/enums/TSRole';

export class DVRoleElementController {

    dvAllowedRoles: Array<TSRole>;
    dvExpression: boolean;

    static $inject: string[] = ['AuthServiceRS'];

    /* @ngInject */
    constructor(private authServiceRS: AuthServiceRS) {
    }

    /**
     * Gibt true zurueck wenn die Rolle der Benutzer eraubt ist den Element zu sehen und die zusaetzliche Expression true ist.
     */
    public checkValidity(): boolean {
        return this.checkRoles() && this.checkExpression();
    }

    /**
     * Die Rollen muessen gesetzt sein, wenn diese Direktive verwendet wird. Sollten die Rollen nicht gesetzt sein, wird das Element ausgeblendet
     * @returns {boolean}
     */
    private checkRoles(): boolean {
        if (this.dvAllowedRoles) {
            for (let role of this.dvAllowedRoles) {
                if (this.authServiceRS.getPrincipalRole() === role) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Diese Methode gibt einfach den Wert von expression zurueck. Hier koennte man aber auch etwas berechnen wenn noetig
     * @returns {boolean} wenn die expression is null oder undefined gibt es true zurueck. Sonst gibt es den Wert von expression zurueck
     */
    private checkExpression(): boolean {
        if (this.dvExpression === undefined || this.dvExpression === null) {
            return true;
        }
        return (this.dvExpression === true);
    }
}
