import {IComponentOptions} from 'angular';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import INgModelController = angular.INgModelController;
let template = require('./dv-radio-container.html');
require('./dv-radio-container.less');

export class DvRadioContainerComponentConfig implements IComponentOptions {
    transclude = false;
    require: any = {ngModelCtrl: 'ngModel'}; //ng-model controller der vom user des elements gesetzt werden muss
    bindings: any = {
        ngModel: '<',
        ngRequired: '<',
        items: '<',
        dvEnableAllowedRoles: '<',
        dvEnableExpression: '<',
        ariaDescribedBy: '@',
        ariaLabel: '@'
    };
    template = template;
    controller = DvRadioContainerController;
    controllerAs = 'vm';

}

export class DvRadioContainerController {

    ngModelCtrl: INgModelController;
    modelToPassOn: any;
    // Die Direktiven duerfen nicht dynamisch gesetzt werden. https://github.com/angular/angular.js/issues/14575
    // D.h. dv-enable-element muss in diesem Fall immer gesetzt sein. Aus diesem Grund spielen wir
    // immer mit den Werten von dv-enable-allowed-roles und dv-enable-expression. Wenn die Direktive nicht gesetzt werden muss, bekommen diese attributen
    // die Werte by default allRoles und true, sodass es immer angezeigt wird.
    allRoles: Array<string>;

    static $inject: any[] = [];
    /* @ngInject */
    constructor() {
    }

    $onInit() {
        this.modelToPassOn = this.ngModelCtrl.$viewValue;
        //wenn im model etwas aendert muss unsere view das mitkriegen
        this.ngModelCtrl.$render = () => {
            this.modelToPassOn = this.ngModelCtrl.$viewValue;
        };
        this.allRoles = TSRoleUtil.getAllRoles();
    }

    onBlur() {
        //parent model touched setzten on blur vom Kind damit fehlerhandlich richtig funktioniert
        this.ngModelCtrl.$setTouched();
    }

    onChange() {
        this.ngModelCtrl.$setViewValue(this.modelToPassOn);
    }
}
