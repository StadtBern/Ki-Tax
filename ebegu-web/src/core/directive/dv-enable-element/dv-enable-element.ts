import {IDirective, IDirectiveFactory, IAugmentedJQuery, IAttributes, IScope} from 'angular';
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
