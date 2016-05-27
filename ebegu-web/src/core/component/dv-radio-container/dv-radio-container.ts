import {IComponentOptions} from 'angular';
import INgModelController = angular.INgModelController;
let template = require('./dv-radio-container.html');

export class DvRadioContainerComponentConfig implements IComponentOptions {
    transclude = false;
    require: any = {ngModelCtrl: 'ngModel'}; //ng-model controller der vom user des elements gesetzt werden muss
    bindings: any = {
        ngModel: '<',
        ngRequired: '<',
        items: '<'
    };
    template = template;
    controller = DvRadioContainerController;
    controllerAs = 'vm';

}

export class DvRadioContainerController {

    ngModelCtrl: INgModelController;
    modelToPassOn: any;

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

    }

    onBlur() {
        //parent model touched setzten on blur vom Kind damit fehlerhandlich richtig funktioniert
        this.ngModelCtrl.$setTouched();
    }

    onChange() {
        this.ngModelCtrl.$setViewValue(this.modelToPassOn);
    }
}
