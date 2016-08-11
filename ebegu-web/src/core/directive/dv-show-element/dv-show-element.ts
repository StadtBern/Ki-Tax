import {IDirective, IDirectiveFactory, IAugmentedJQuery} from 'angular';
import {DVRoleElementController} from '../../controller/DVRoleElementController';
import IScope = angular.IScope;


/**
 * Attribute Directive um Elementen aus- und einblenden.
 * Die Direktive muss folgendermasse benutzt werden:
 *     dv-show-element - diese Attribute muss in jedem Element gesetzt werden, das die Direktive braucht
 *     dv-show-allowed-roles="[vm.TSRole.X, vm.TSRole.Y, ...]" - Array mit allen Rollen, für die das Element eingeblendet werden muss. Um diese Syntax
 *                                                          zu verwenden, muss der Kontroller eine Subklasse von AbstractGesuchViewController sein.
 *                                                          Diese Attribute ist pflicht, darf aber auch auch ein leeres Array sein
 *     dv-show-expression - optionale Attribute, mit der man einen extra boolean Wert uebergeben kann
 *
 * ACHTUNG! Diese Direktive darf nicht mit ng-if zusammen benutzt werden
 */
export class DVShowElement implements IDirective {
    restrict = 'A';
    controller = DVRoleElementController;
    // kind bindToController und kein controllerAs weil sonst wird der scope ueberschrieben, da wir mit attribute Direktiven arbeiten

    transclude: any;
    priority: number;
    terminal: boolean;
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

    link = (scope: IScope, element: IAugmentedJQuery, attributes: any, controller: DVRoleElementController, $transclude: any) => {
        // Copy arguments to new array to avoid: The 'arguments' object cannot be referenced in an arrow function in ES3 and ES5.
        // Consider using a standard function expression.
        let arguments2: Array<any> = [scope, element, attributes, controller, $transclude];
        this.callNgIfThrough(attributes, controller, arguments2);

        attributes.$observe('dvShowAllowedRoles', (value: any) => {
            let roles = scope.$eval(value);
            controller.dvAllowedRoles = roles;
        });
        attributes.$observe('dvShowExpression', (value: any) => {
            let expression = scope.$eval(value);
            controller.dvExpression = expression;
        });
    };

    /**
     * Diese Methode darf nur einmal aufgerufen werden.
     * VORSICHT. Sollte diese Methode X-Mal aufgerufen werden, wird das Element dann X-Mall angezeigt
     * @param attributes
     * @param controller
     * @param arguments2
     */
    private callNgIfThrough(attributes: any, controller: DVRoleElementController, arguments2: Array<any>) {
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
