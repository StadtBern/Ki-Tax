import {IDirective, IDirectiveFactory} from 'angular';
import INgModelController = angular.INgModelController;
let template = require('./dv-valueinput.html');

export class DVValueinput implements IDirective {
    restrict = 'E';
    require: any = {ngModelCtrl: 'ngModel'};
    scope = {
        ngModel: '=',
        inputId: '@',
        ngRequired: '<',
        ngDisabled: '<',
        allowNegative: '<',
        dvOnBlur: '&?'
    };
    controller = ValueinputController;
    controllerAs = 'vm';
    bindToController = true;
    template = template;

    static factory(): IDirectiveFactory {
        const directive = () => new DVValueinput();
        directive.$inject = [];
        return directive;
    }
}
export class ValueinputController {
    static $inject: string[] = [];
    valueinput: string;
    ngModelCtrl: INgModelController;
    valueRequired: boolean;
    ngRequired: boolean;
    allowNegative: boolean;
    dvOnBlur: () => void;

    constructor() {
    }

    // beispiel wie man auf changes eines attributes von aussen reagieren kann
    $onChanges(changes: any) {
        if (changes.ngRequired && !changes.ngRequired.isFirstChange()) {
            this.valueRequired = changes.ngRequired.currentValue;
        }

    }

    //wird von angular aufgerufen
    $onInit() {
        if (!this.ngModelCtrl) {
            return;
        }

        if (this.ngRequired) {
            this.valueRequired = this.ngRequired;
        }

        if (!this.allowNegative) {
            this.allowNegative = false;
        }

        this.ngModelCtrl.$render = () => {
            this.valueinput = this.ngModelCtrl.$viewValue;
        };
        this.ngModelCtrl.$formatters.unshift(ValueinputController.numberToString);
        this.ngModelCtrl.$parsers.push(ValueinputController.stringToNumber);

        this.ngModelCtrl.$validators['valueinput'] = (modelValue: any, viewValue: any) => {
            // if not required and view value empty, it's ok...
            if (!this.valueRequired && !viewValue) {
                return true;
            }
            let value = modelValue || ValueinputController.stringToNumber(viewValue);

            return !isNaN(Number(value)) && (Number(value) < 999999999999) && this.allowNegative ? true : Number(value) > 0;
        };
    }

    updateModelValue() {
        this.ngModelCtrl.$setViewValue(this.valueinput);
        this.valueinput = ValueinputController.formatToNumberString(ValueinputController.formatFromNumberString(this.valueinput));
        this.ngModelCtrl.$setTouched();

        if (this.dvOnBlur) { // userdefined onBlur event
            this.dvOnBlur();
        }
    };

    private static numberToString(num: number): string {
        if (num) {
            return ValueinputController.formatToNumberString(num.toString());
        }
        return '';
    }

    private static stringToNumber(string: string): number {
        if (string) {
            return Number(ValueinputController.formatFromNumberString(string));
        }
        return null;
    }

    private static formatToNumberString(valueString: string): string {
        return valueString.replace(/\B(?=(\d{3})+(?!\d))/g, "'");
    }

    private static formatFromNumberString(numberString: string): string {
        return numberString.split("'").join('').split(',').join('');
    }

    public removeNotDigits(): void {
        let transformedInput = this.valueinput.split(/\D+/g).join(''); // removes all "not digit"
        if (this.valueinput !== transformedInput) {
            this.ngModelCtrl.$setViewValue(transformedInput);
            this.ngModelCtrl.$render();
        }
    }

}
