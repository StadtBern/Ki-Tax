import {IDirective, IDirectiveFactory, IAugmentedJQuery} from 'angular';
import {TSRole} from '../../../models/enums/TSRole';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
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
    controller = DVEnableElementController;
    // kind bindToController und kein controllerAs weil sonst wird der scope ueberschrieben, da wir mit attribute Direktiven arbeiten

    link = (scope: IScope, element: IAugmentedJQuery, attributes: any, controller: DVEnableElementController) => {
        attributes.$observe('dvEnableAllowedRoles', (value: any) => {
            let roles = scope.$eval(value);
            controller.dvEnableAllowedRoles = roles;
            this.enableElement(controller, attributes);
        });
        attributes.$observe('dvEnableExpression', (value: any) => {
            let expression = scope.$eval(value);
            controller.dvEnableExpression = expression;
            this.enableElement(controller, attributes);
        });
    };

    /**
     * Sets the attribute disabled to true or false of the element.
     * @param controller
     * @param attributes
     */
    private enableElement(controller: DVEnableElementController, attributes: any) {
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


export class DVEnableElementController {

    dvEnableAllowedRoles: Array<TSRole>;
    dvEnableExpression: boolean;

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
        if (this.dvEnableAllowedRoles) {
            for (let role of this.dvEnableAllowedRoles) {
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
        if (this.dvEnableExpression === undefined || this.dvEnableExpression === null) {
            return true;
        }
        return (this.dvEnableExpression === true);
    }
}
