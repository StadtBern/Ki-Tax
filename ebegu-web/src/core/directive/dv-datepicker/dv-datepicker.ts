import {IDirective, IDirectiveFactory, IScope} from 'angular';
import * as moment from 'moment';
import DateUtil from '../../../utils/DateUtil';
import Moment = moment.Moment;
import INgModelController = angular.INgModelController;
let template = require('./dv-datepicker.html');
export interface DatepickerScope extends IScope { updateModelValue: Function; date: Date;
}
export class DVDatepicker implements IDirective {
    restrict = 'E';
    require: any = {ngModelCtrl: 'ngModel'};
    scope = {
        ngModel: '=',
        inputId: '@'
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

    constructor() {
    }
    // beispiel wie man auf changes eines attributes von aussen reagieren kann
    // $onChanges(changes) {
    //     if (changes.required&& !changes.inputName.isFirstChange()) {
    //         this.isRequired == changes.require.currentValue;
    //         // changes.inputName.currentValue;
    //         // changes.inputName.currentValue;
    //     }
    //
    // }

    //wird von angular aufgerufen
    $onInit() {
        if (!this.ngModelCtrl) {
            return;
        }


        this.ngModelCtrl.$render = () => {
            this.date = this.ngModelCtrl.$viewValue;
        };
        this.ngModelCtrl.$formatters.unshift(DatepickerController.momentToDate);
        this.ngModelCtrl.$parsers.push(DatepickerController.dateToMoment);
        //datum validieren (reihenfolge scheint so zu sein das dieser validator vor dem Datumsfeldvalidator der komponente laeuft)
        this.ngModelCtrl.$validators['moment'] = (modelValue: any, viewValue: any) => {
            let value = modelValue || DatepickerController.dateToMoment(viewValue);
            if (!value) {
                return true;
            }

            return moment.isMoment(value) && value.isValid();
        };
    }

    onBlur() {
        this.ngModelCtrl.$setTouched();

    }

    updateModelValue() {
        this.ngModelCtrl.$setViewValue(this.date);
    };


    private static momentToDate(mom: Moment): any {
        if (mom && mom.isValid()) {
            return mom.toDate();
        }
        return '';
    }

    private static dateToMoment(date: Date): any {
        //nur versuchen das datum als moment zu parsen wenn es kein string ist
        if (date && !(typeof date === 'string' )) {
            let dateString = date.toISOString().substring(0, 10);
            return DateUtil.localDateToMoment(dateString);
        }

        return date;
    }


}
