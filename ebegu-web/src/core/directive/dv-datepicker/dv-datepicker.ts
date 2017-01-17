import {IDirective, IDirectiveFactory} from 'angular';
import * as moment from 'moment';
import Moment = moment.Moment;
import INgModelController = angular.INgModelController;
let template = require('./dv-datepicker.html');

export class DVDatepicker implements IDirective {
    restrict = 'E';
    require: any = {ngModelCtrl: 'ngModel'};
    scope = {
        ngModel: '=',
        inputId: '@',
        ngRequired: '<',
        placeholder: '@',
        ngDisabled: '<'
    };
    controller = DatepickerController;
    controllerAs = 'vm';
    bindToController = true;
    template = template;
    /* constructor() { this.link = this.unboundLink.bind(this); }*/
    static factory(): IDirectiveFactory {
        const directive = () => new DVDatepicker();
        directive.$inject = [];
        return directive;
    }
}
export class DatepickerController {
    static $inject: string[] = [];
    date: Date;
    ngModelCtrl: INgModelController;
    dateRequired: boolean;
    ngRequired: boolean;
    placeholder: string;
    static allowedFormats: string[] = ['D.M.YYYY', 'DD.MM.YYYY'];
    static defaultFormat: string = 'DD.MM.YYYY';

    constructor() {
    }

    // beispiel wie man auf changes eines attributes von aussen reagieren kann
    $onChanges(changes: any) {
        if (changes.ngRequired && !changes.ngRequired.isFirstChange()) {
            this.dateRequired = changes.ngRequired.currentValue;
        }

    }

    //wird von angular aufgerufen
    $onInit() {
        if (!this.ngModelCtrl) {
            return;
        }

        //wenn kein Placeholder gesetzt wird wird der standardplaceholder verwendet. kann mit placeholder="" ueberscrieben werden
        if (this.placeholder === undefined) {
            this.placeholder = 'tt.mm.jjjj';
        } else if (this.placeholder === '') {
            this.placeholder = undefined;
        }

        if (this.ngRequired) {
            this.dateRequired = this.ngRequired;
        }

        this.ngModelCtrl.$render = () => {
            this.date = this.ngModelCtrl.$viewValue;
        };
        this.ngModelCtrl.$formatters.unshift(DatepickerController.momentToString);
        this.ngModelCtrl.$parsers.push(DatepickerController.stringToMoment);

        this.ngModelCtrl.$validators['moment'] = (modelValue: any, viewValue: any) => {
            // if not required and view value empty, it's ok...
            if (!this.dateRequired && !viewValue) {
                return true;
            }
            let value = modelValue || DatepickerController.stringToMoment(viewValue);
            return moment(value, DatepickerController.allowedFormats, true).isValid();
        };
    }

    onBlur() {
        this.ngModelCtrl.$setTouched();
    }

    updateModelValue() {
        this.ngModelCtrl.$setViewValue(this.date);
    };


    private static momentToString(mom: Moment): string {
        if (mom && mom.isValid()) {
            return mom.format(DatepickerController.defaultFormat);
        }
        return '';
    }

    private static stringToMoment(date: string): any {
        if (moment(date, DatepickerController.allowedFormats, true).isValid()) {
            return moment(date, DatepickerController.allowedFormats, true);
        }
        return null;
    }


}
