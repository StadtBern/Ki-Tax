import {IDirective, IDirectiveFactory, IAugmentedJQuery, IScope, IAttributes} from 'angular';
import {DVRoleElementController} from '../../controller/DVRoleElementController';


/**
 * Attribute Directive um Elementen aus- und einblenden.
 * Die Direktive muss folgendermasse benutzt werden:
 *     dv-show-element - diese Attribute muss in jedem Element gesetzt werden, das die Direktive braucht
 *     dv-show-allowed-roles="[vm.TSRole.X, vm.TSRole.Y, ...]" - Array mit allen Rollen, für die das Element eingeblendet werden muss. Um diese Syntax
 *                                                          zu verwenden, muss der Kontroller eine Subklasse von AbstractGesuchViewController sein.
 *                                                          Diese Attribute ist pflicht, darf aber auch auch ein leeres Array sein. Man kann auch eine
 *                                                          Methode oder eine Variable uebergeben
 *     dv-show-expression - optionale Attribute, mit der man einen extra boolean Wert uebergeben kann. Man kann auch eine
 *                          Methode oder eine Variable uebergeben
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

    link = (scope: IScope, element: IAugmentedJQuery, attributes: IAttributes, controller: DVRoleElementController, $transclude: any) => {
        // Copy arguments to new array to avoid: The 'arguments' object cannot be referenced in an arrow function in ES3 and ES5.
        // Consider using a standard function expression.
        let arguments2: Array<any> = [scope, element, attributes, controller, $transclude];
        this.callNgIfThrough(attributes, controller, arguments2);

        // Die Version mit attributes.$observe funktioniert nicht. Als Wert bekommen wir immer ein string mit dem Namen der Variable, den wir
        // danach evaluieren muessen. Da dieser String sich nie aendert (sondern eher seine evaluation), wird das observe nie aufgerufen. Mit scope.$watch
        // funktioniert es weil die Variable immer transcluded wird und somit der Wert aendert sich.
        scope.$watch(attributes['dvShowAllowedRoles'], (newValue: any, oldValue: any, scope: any) => {
            controller.dvAllowedRoles = newValue;
        }, true);
        scope.$watch(attributes['dvShowExpression'], (newValue: any, oldValue: any) => {
            controller.dvExpression = newValue;
        }, true);
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
