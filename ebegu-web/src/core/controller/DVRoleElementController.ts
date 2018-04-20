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
