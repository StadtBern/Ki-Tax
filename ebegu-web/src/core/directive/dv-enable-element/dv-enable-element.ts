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

import {IAttributes, IAugmentedJQuery, IDirective, IDirectiveFactory, IScope} from 'angular';
import {DVRoleElementController} from '../../controller/DVRoleElementController';

/**
 * Attribute Directive um Elementen zu enable/disable.
 * Die Direktive muss folgendermasse benutzt werden:
 *     dv-enable-element - diese Attribute muss in jedem Element gesetzt werden, das die Direktive braucht
 *     dv-enable-allowed-roles="[vm.TSRole.X, vm.TSRole.Y, ...]" - Array mit allen Rollen, für die das Element enabled werden muss. Um diese Syntax
 *                                                          zu verwenden, muss der Kontroller eine Subklasse von AbstractGesuchViewController sein.
 *                                                          Diese Attribute ist pflicht, darf aber auch auch ein leeres Array sein. Man kann auch eine
 *                                                          Methode oder eine Variable uebergeben
 *     dv-enable-expression - optionale Attribute, mit der man einen extra boolean Wert uebergeben kann. Man kann auch eine
 *                            Methode oder eine Variable uebergeben
 *
 * ACHTUNG! Diese Direktive darf nicht mit disable zusammen benutzt werden
 */
export class DVEnableElement implements IDirective {
    restrict = 'A';
    controller = DVRoleElementController;
    // kind bindToController und kein controllerAs weil sonst wird der scope ueberschrieben, da wir mit attribute Direktiven arbeiten

    link = (scope: IScope, element: IAugmentedJQuery, attributes: IAttributes, controller: DVRoleElementController) => {
        // attributes.$observe funktioniert nicht. Siehe dv-show-element.ts
        scope.$watch(attributes['dvEnableAllowedRoles'], (newValue: any, oldValue: any, scope: any) => {
            controller.dvAllowedRoles = newValue;
            this.enableElement(controller, attributes);
        }, true);
        scope.$watch(attributes['dvEnableExpression'], (newValue: any, oldValue: any) => {
            controller.dvExpression = newValue;
            this.enableElement(controller, attributes);
        }, true);
    }

    /**
     * Sets the attribute disabled to true or false of the element.
     * @param controller
     * @param attributes
     */
    private enableElement(controller: DVRoleElementController, attributes: any) {
        if (controller.checkValidity()) {
            attributes.$set('disabled');
        } else {
            attributes.$set('disabled', 'disabled');
        }
    }

    static factory(): IDirectiveFactory {
        const directive = () => new DVEnableElement();
        directive.$inject = [];
        return directive;
    }
}
