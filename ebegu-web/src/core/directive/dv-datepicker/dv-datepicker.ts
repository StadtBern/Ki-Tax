import {IDirective, IDirectiveFactory, IDirectiveLinkFn, IScope, IAugmentedJQuery, IAttributes} from 'angular';
import * as moment from 'moment';
import DateUtil from '../../../utils/DateUtil';
import Moment = moment.Moment;
let template = require('./dv-datepicker.html');

export interface DatepickerScope extends IScope {
    updateModelValue: Function;
    date: Date;
}

export class DVDatepicker implements IDirective {
    static $inject: string[] = [];

    restrict = 'E';
    require = 'ngModel';
    scope = {
        ngModel: '=',
        inputName: '@',
        inputId: '@'
    };
    link: IDirectiveLinkFn;
    template = template;



    constructor() {
        this.link = this.unboundLink.bind(this);
    }

    unboundLink(scope: DatepickerScope, element: IAugmentedJQuery, attrs: IAttributes, ngModelCtrl: any) {
        if (!ngModelCtrl) {
            return;
        }

        scope.updateModelValue = () => {
            ngModelCtrl.$setViewValue(scope.date);
        };

        ngModelCtrl.$render = () => {
            scope.date = ngModelCtrl.$viewValue;
        };
        ngModelCtrl.$formatters.unshift(DVDatepicker.momentToDate);
        ngModelCtrl.$parsers.push(DVDatepicker.dateToMoment);
        //datum validieren (reihenfolge scheint so zu sein das dieser validator vor dem Datumsfeldvalidator der komponente laeuft)
        ngModelCtrl.$validators.moment = (modelValue: any, viewValue: any) => {
            let value = modelValue || DVDatepicker.dateToMoment(viewValue);
            if (!value) {
                return true;
            }

            return moment.isMoment(value) && value.isValid();
        };
    }

    static factory(): IDirectiveFactory {
        const directive = () => new DVDatepicker();
        directive.$inject = [];
        return directive;
    }

    private static momentToDate(mom: Moment): any {
        if (mom && mom.isValid()) {
            return mom.toDate();
        }
        return '';
    }

    private static dateToMoment(date: Date): any {
        return DateUtil.jsDateToMoment(date);
    }


}
