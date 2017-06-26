import {IDirective, IDirectiveFactory, IAugmentedJQuery, IScope, IAttributes} from 'angular';
import {DVRoleElementController} from '../../controller/DVRoleElementController';


/**
 * Attribute Directive um Elementen aus- und einblenden. Erweitert ng-show
 * Die Direktive muss folgendermasse benutzt werden:
 *     dv-display-element - diese Attribute muss in jedem Element gesetzt werden, das die Direktive braucht
 *     dv-display-allowed-roles="[vm.TSRole.X, vm.TSRole.Y, ...]" - Array mit allen Rollen, für die das Element eingeblendet werden muss. Um diese Syntax
 *                                                          zu verwenden, muss der Kontroller eine Subklasse von AbstractGesuchViewController sein.
 *                                                          Diese Attribute ist pflicht, darf aber auch auch ein leeres Array sein. Man kann auch eine
 *                                                          Methode oder eine Variable uebergeben
 *     dv-display-expression - optionale Attribute, mit der man einen extra boolean Wert uebergeben kann. Man kann auch eine
 *                          Methode oder eine Variable uebergeben
 *
 * ACHTUNG! Diese Direktive darf nicht mit ng-show zusammen benutzt werden
 */
export class DVDisplayElement implements IDirective {
    restrict = 'A';
    controller = DVRoleElementController;
    // kind bindToController und kein controllerAs weil sonst wird der scope ueberschrieben, da wir mit attribute Direktiven arbeiten

    multiElement: any;
    replace = true;
    ngShow: any;

    static $inject: string[] = ['ngShowDirective'];

    /* @ngInject */
    constructor(private ngShowDirective: any) {
        this.ngShow = ngShowDirective[0];
        this.multiElement = this.ngShow.multiElement;
    }

    link = (scope: IScope, element: IAugmentedJQuery, attributes: IAttributes, controller: DVRoleElementController, $transclude: any) => {
        // Copy arguments to new array to avoid: The 'arguments' object cannot be referenced in an arrow function in ES3 and ES5.
        // Consider using a standard function expression.
        let arguments2: Array<any> = [scope, element, attributes, controller, $transclude];
        this.callNgShowThrough(attributes, controller, arguments2);

        // Die Version mit attributes.$observe funktioniert nicht. Als Wert bekommen wir immer ein string mit dem Namen der Variable, den wir
        // danach evaluieren muessen. Da dieser String sich nie aendert (sondern eher seine evaluation), wird das observe nie aufgerufen. Mit scope.$watch
        // funktioniert es weil die Variable immer transcluded wird und somit der Wert aendert sich.
        scope.$watch(attributes['dvDisplayAllowedRoles'], (newValue: any, oldValue: any, scope: any) => {
            controller.dvAllowedRoles = newValue;
        }, true);
        scope.$watch(attributes['dvDisplayExpression'], (newValue: any, oldValue: any) => {
            controller.dvExpression = newValue;
        }, true);
    }

    /**
     * Diese Methode darf nur einmal aufgerufen werden.
     * VORSICHT. Sollte diese Methode X-Mal aufgerufen werden, wird das Element dann X-Mall angezeigt
     * @param attributes
     * @param controller
     * @param arguments2
     */
    private callNgShowThrough(attributes: any, controller: DVRoleElementController, arguments2: Array<any>) {
        attributes.ngShow = () => {
            return (controller.checkValidity());
        };
        this.ngShow.link.apply(this.ngShow, arguments2);
    }

    static factory(): IDirectiveFactory {
        const directive = (ngShowDirective: any) => new DVDisplayElement(ngShowDirective);
        directive.$inject = ['ngShowDirective'];
        return directive;
    }
}
