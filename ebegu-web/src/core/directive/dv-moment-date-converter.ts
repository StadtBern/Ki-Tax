import {IDirective, IDirectiveFactory, IDirectiveLinkFn, IScope, IAugmentedJQuery, IAttributes} from 'angular';
import * as moment from 'moment';
import DateUtil from '../../utils/DateUtil';
import Moment = moment.Moment;

export default class DVMomentDateConverter implements IDirective {
    static $inject: string[] = [];

    restrict = 'A';
    require = 'ngModel';
    link: IDirectiveLinkFn;
    datepickerDate: Date;

    constructor() {
        this.link = (scope: IScope, element: IAugmentedJQuery, attrs: IAttributes, ctrl: any) => {
            if (!ctrl) {
                return;
            }

            ctrl.$formatters.unshift(DVMomentDateConverter.momentToDate);
            ctrl.$parsers.push(DVMomentDateConverter.dateToMoment);
            //datum validieren (reihenfolge scheint so zu sein das dieser validator vor dem Datumsfeldvalidator der komponente laeuft)
            ctrl.$validators.date = (modelValue: any, viewValue: any) => {
                let value = modelValue || viewValue;
                if (!value) {
                    return true;
                }

                return moment.isMoment(value) && value.isValid();
            };
        };
    }

    static factory(): IDirectiveFactory {
        const directive = () => new DVMomentDateConverter();
        directive.$inject = [];
        return directive;
    }

    private static momentToDate(mom: Moment): any {
        if (mom && mom.isValid()) {
            return mom.toDate();
        }
        return mom;
    }

    private static dateToMoment(date: Date): any {
        return DateUtil.jsDateToMoment(date);
    }


}
