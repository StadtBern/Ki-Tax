import {IDirective, IDirectiveFactory, IAugmentedJQuery, IDirectiveLinkFn, INgModelController} from 'angular';
import ITimeoutService = angular.ITimeoutService;
let template = require('./dv-valueinput.html');

export class DVValueinput implements IDirective {
    restrict = 'E';
    require: any = {ngModelCtrl: 'ngModel', dvValueInputCtrl: 'dvValueinput'};
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
    link: IDirectiveLinkFn;

    constructor() {

    }

    static factory(): IDirectiveFactory {
        const directive = () => new DVValueinput();
        directive.$inject = [];
        return directive;
    }
}
export class ValueinputController {
    valueinput: string;
    ngModelCtrl: INgModelController;
    valueRequired: boolean;
    ngRequired: boolean;
    allowNegative: boolean;
    dvOnBlur: () => void;

    static $inject: string[] = ['$timeout'];
    constructor(private $timeout: ITimeoutService) {
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

            return !isNaN(Number(value)) && (Number(value) < 999999999999) && this.allowNegative ? true : Number(value) >= 0;
        };
    }

    /**
     * on blur setzen wir den formatierten "string" ins feld
     */
    public updateModelValueBlur() {
        this.updateModelValue();
        this.ngModelCtrl.$setTouched();
    }

    /**
     * onFocus schreiben wir den string als zahl ins feld und setzen den cursor ans ende des inputs
     * @param event
     */
    public handleFocus(event: any) {

        this.valueinput = this.sanitizeInputString();
        if (event) {
            let angEle: IAugmentedJQuery = angular.element(event.target); //read raw html element

            let element: any = angEle[0];
            this.$timeout(() => {
                // If this function exists...
                if (element.setSelectionRange) {
                    // ... then use it
                    element.setSelectionRange(999999, 999999);
                } else {
                    // ... otherwise replace the contents with itself
                    // (Doesn't work in Google Chrome)
                    element.val(element.val());
                }
            });
        }

    }

    updateModelValue() {
        //set the number as formatted string to the model
        this.valueinput = ValueinputController.formatToNumberString(ValueinputController.formatFromNumberString(this.valueinput));
        this.ngModelCtrl.$setViewValue(this.valueinput);
        if (this.dvOnBlur) { // userdefined onBlur event
            this.dvOnBlur();
        }

    };

    private static numberToString(num: number): string {
        if (num || num === 0) {
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
        let transformedInput = this.sanitizeInputString();

        //neuen wert ins model schreiben
        if (transformedInput !== this.ngModelCtrl.$viewValue) {
            this.ngModelCtrl.$setViewValue(ValueinputController.formatToNumberString(transformedInput)); //setting the new raw number into the invisible parentmodel
            this.ngModelCtrl.$render();
        }
        if (this.valueinput !== transformedInput) {
            this.valueinput = transformedInput;
        }
    }

    private sanitizeInputString() {
        let transformedInput = this.valueinput;
        let sign: string = '';
        if (this.allowNegative === true && this.valueinput && this.valueinput.indexOf('-') === 0) { // if negative allowed, get sign
            sign = '-';
            transformedInput.substr(1); // get just the number part
        }
        transformedInput = transformedInput.replace(/\D+/g, ''); // removes all "not digit"
        if (transformedInput) {
            transformedInput = parseInt(transformedInput).toString(); // parse to int to remove not wanted digits like leading zeros and then back to string
        }
        transformedInput = sign + transformedInput; // add sign to raw number
        return transformedInput;
    }

}
