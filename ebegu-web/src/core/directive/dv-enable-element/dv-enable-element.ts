import {IDirective, IDirectiveFactory, IAugmentedJQuery} from 'angular';
import {DVRoleElementController} from '../../controller/DVRoleElementController';
import IScope = angular.IScope;


/**
 * Attribute Directive um Elementen zu enable/disable.
 * Die Direktive muss folgendermasse benutzt werden:
 *     dv-enable-element - diese Attribute muss in jedem Element gesetzt werden, das die Direktive braucht
 *     dv-enable-allowed-roles="[vm.TSRole.X, vm.TSRole.Y, ...]" - Array mit allen Rollen, für die das Element enabled werden muss. Um diese Syntax
 *                                                          zu verwenden, muss der Kontroller eine Subklasse von AbstractGesuchViewController sein.
 *                                                          Diese Attribute ist pflicht, darf aber auch auch ein leeres Array sein
 *     dv-enable-expression - optionale Attribute, mit der man einen extra boolean Wert uebergeben kann
 */
export class DVEnableElement implements IDirective {
    restrict = 'A';
    controller = DVRoleElementController;
    // kind bindToController und kein controllerAs weil sonst wird der scope ueberschrieben, da wir mit attribute Direktiven arbeiten

    link = (scope: IScope, element: IAugmentedJQuery, attributes: any, controller: DVRoleElementController) => {
        attributes.$observe('dvEnableAllowedRoles', (value: any) => {
            let roles = scope.$eval(value);
            controller.dvAllowedRoles = roles;
            this.enableElement(controller, attributes);
        });
        attributes.$observe('dvEnableExpression', (value: any) => {
            let expression = scope.$eval(value);
            controller.dvExpression = expression;
            this.enableElement(controller, attributes);
        });
    };

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
