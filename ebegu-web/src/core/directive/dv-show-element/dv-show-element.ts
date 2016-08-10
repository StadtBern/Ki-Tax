import {IDirective, IDirectiveFactory, IAugmentedJQuery, IAttributes} from 'angular';
import {TSRole} from '../../../models/enums/TSRole';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';

/**
 * Attribute Directive um Elementen aus- und einblenden.
 * Die Direktive muss folgendermasse benutzt werden:
 *     dv-show-element - diese Attribute muss in jedem Element gesetzt werden, der die Direktive braucht
 *     dv-allowed-roles="[vm.TSRole.X, vm.TSRole.Y, ...]" - Array mit allen Rollen, für die das Element eingeblendet werden muss. Um diese Syntax
 *                                                          zu verwenden, muss der Kontroller eine Subklasse von AbstractGesuchViewController sein.
 *                                                          Diese Attribute ist pflicht, darf auch auch ein leeres Array sein
 *     dv-expression - optionale Attribute, mit der man einen extra boolean Wert uebergeben kann
 */
export class DVShowElement implements IDirective {
    restrict = 'A';
    controller = DVShowElementController;
    // kind bindToController und kein controllerAs weil sonst wird der scope ueberschrieben, da wir mit attribute Direktiven arbeiten


    link = (scope: any, element: IAugmentedJQuery, attributes: any, controller: DVShowElementController) => {
        attributes.$observe('dvAllowedRoles', (value: any) => {
            let roles = scope.$eval(value);
            controller.dvAllowedRoles = roles;
            controller.updateState(element);
        });
        attributes.$observe('dvExpression', (value: any) => {
            let expression = scope.$eval(value);
            controller.dvExpression = expression;
            controller.updateState(element);
        });
    };

    static factory(): IDirectiveFactory {
        const directive = (ngIfDirective: any) => new DVShowElement();
        directive.$inject = [];
        return directive;
    }
}


export class DVShowElementController {

    dvAllowedRoles: Array<TSRole>;
    dvExpression: boolean;
    ngIf: any;

    static $inject: string[] = ['AuthServiceRS'];

    /* @ngInject */
    constructor(private authServiceRS: AuthServiceRS) {
    }

    public updateState(element: IAugmentedJQuery) {
        if (this.checkRoles() && this.evaluateExpression()) {
            element.show();
        } else {
            element.hide();
        }
    }

    /**
     * Die Rollen muessen gesetzt sein, wenn diese Direktive verwendet wird. Sollten die Rollen nicht gesetzt sein, wird das Element ausgeblendet
     * @returns {boolean}
     */
    public checkRoles(): boolean {
        if (this.dvAllowedRoles) {
            for (let role of this.dvAllowedRoles) {
                if (this.authServiceRS.getPrincipal().role === role) {
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
    public evaluateExpression(): boolean {
        if (this.dvExpression === undefined || this.dvExpression === null) {
            return true;
        }
        return (this.dvExpression === true);
    }
}
