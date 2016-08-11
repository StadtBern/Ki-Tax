import {IDirective, IDirectiveFactory, IAugmentedJQuery} from 'angular';
import {TSRole} from '../../../models/enums/TSRole';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import IScope = angular.IScope;


/**
 * Attribute Directive um Elementen aus- und einblenden.
 * Die Direktive muss folgendermasse benutzt werden:
 *     dv-show-element - diese Attribute muss in jedem Element gesetzt werden, das die Direktive braucht
 *     dv-show-allowed-roles="[vm.TSRole.X, vm.TSRole.Y, ...]" - Array mit allen Rollen, für die das Element eingeblendet werden muss. Um diese Syntax
 *                                                          zu verwenden, muss der Kontroller eine Subklasse von AbstractGesuchViewController sein.
 *                                                          Diese Attribute ist pflicht, darf aber auch auch ein leeres Array sein
 *     dv-show-expression - optionale Attribute, mit der man einen extra boolean Wert uebergeben kann
 */
export class DVShowElement implements IDirective {
    restrict = 'A';
    controller = DVShowElementController;
    // kind bindToController und kein controllerAs weil sonst wird der scope ueberschrieben, da wir mit attribute Direktiven arbeiten

    transclude: any = 'element';
    priority: number; // = 600;
    terminal: boolean; // = true;
    replace = true;
    ngIf: any;

    static $inject: string[] = ['ngIfDirective'];

    /* @ngInject */
    constructor(private ngIfDirective: any) {
        this.ngIf = ngIfDirective[0];
        this.transclude = this.ngIf.transclude;
        this.priority = this.ngIf.priority;
        this.terminal = this.ngIf.terminal;
    }


    link = (scope: IScope, element: IAugmentedJQuery, attributes: any, controller: DVShowElementController, $transclude: any) => {
        // Copy arguments to new array to avoid: The 'arguments' object cannot be referenced in an arrow function in ES3 and ES5.
        // Consider using a standard function expression.
        let arguments2: Array<any> = [scope, element, attributes, controller, $transclude];

        attributes.$observe('dvShowAllowedRoles', (value: any) => {
            let roles = scope.$eval(value);
            controller.dvShowAllowedRoles = roles;
            this.callNgIfThrough(attributes, controller, arguments2); // nur hier aufrufen, sonst dupliziert sich das Element
        });
        attributes.$observe('dvShowExpression', (value: any) => {
            let expression = scope.$eval(value);
            controller.dvShowExpression = expression;
        });
    };

    /**
     * Diese Methode darf nur einmal aufgerufen werden. Sollte diese Methode X-Mal aufgerufen werden, wird das Element dann X-Mall angezeigt
     * @param attributes
     * @param controller
     * @param arguments2
     */
    private callNgIfThrough(attributes: any, controller: DVShowElementController, arguments2: Array<any>) {
        attributes.ngIf = () => {
            return (controller.checkValidity());
        };
        this.ngIf.link.apply(this.ngIf, arguments2);
    }

    static factory(): IDirectiveFactory {
        const directive = (ngIfDirective: any) => new DVShowElement(ngIfDirective);
        directive.$inject = ['ngIfDirective'];
        return directive;
    }
}


export class DVShowElementController {

    dvShowAllowedRoles: Array<TSRole>;
    dvShowExpression: boolean;

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
        if (this.dvShowAllowedRoles) {
            for (let role of this.dvShowAllowedRoles) {
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
        if (this.dvShowExpression === undefined || this.dvShowExpression === null) {
            return true;
        }
        return (this.dvShowExpression === true);
    }
}
