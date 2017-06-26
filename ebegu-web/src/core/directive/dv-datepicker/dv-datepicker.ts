import {IDirective, IDirectiveFactory} from 'angular';
import * as moment from 'moment';
import DateUtil from '../../../utils/DateUtil';
import Moment = moment.Moment;
import INgModelController = angular.INgModelController;
import IAttributes = angular.IAttributes;
import ILogService = angular.ILogService;
let template = require('./dv-datepicker.html');

export class DVDatepicker implements IDirective {
    restrict = 'E';
    require: any = {ngModelCtrl: 'ngModel'};
    scope = {
        ngModel: '=',
        inputId: '@',
        ngRequired: '<',
        placeholder: '@',
        ngDisabled: '<',
        noFuture: '<?',
        dvOnBlur: '&?',
        dvMinDate: '<?', // Kann als String im Format allowedFormats oder als Moment angegeben werden
        dvMaxDate: '<?'  // Kann als String im Format allowedFormats oder als Moment angegeben werden
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
    static $inject: string[] = ['$log', '$attrs'];
    date: Date;
    ngModelCtrl: INgModelController;
    dateRequired: boolean;
    ngRequired: boolean;
    placeholder: string;
    dvOnBlur: () => void;
    dvMinDate: any;
    dvMaxDate: any;
    static allowedFormats: string[] = ['D.M.YYYY', 'DD.MM.YYYY'];
    static defaultFormat: string = 'DD.MM.YYYY';

    constructor(private $log: ILogService, private $attrs: IAttributes) {
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
        // Wenn kein Minimumdatum gesetzt ist, verwenden wir 01.01.1900 als Minimum
        if (this.dvMinDate === undefined) {
            this.dvMinDate = DateUtil.localDateToMoment('1900-01-01');
        }
        let noFuture = 'noFuture' in this.$attrs;
        //wenn kein Placeholder gesetzt wird wird der standardplaceholder verwendet. kann mit placeholder=""
        // ueberscrieben werden
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
            return this.getInputAsMoment(modelValue, viewValue).isValid();
        };
        // Validator fuer Minimal-Datum
        this.ngModelCtrl.$validators['dvMinDate'] = (modelValue: any, viewValue: any) => {
            let result: boolean = true;
            if (this.dvMinDate && viewValue) {
                let minDateAsMoment: Moment = moment(this.dvMinDate, DatepickerController.allowedFormats, true);
                if (minDateAsMoment.isValid()) {
                    let inputAsMoment: Moment = this.getInputAsMoment(modelValue, viewValue);
                    if (inputAsMoment && inputAsMoment.isBefore(minDateAsMoment)) {
                        result = false;
                    }
                } else {
                    this.$log.debug('min date is invalid', this.dvMinDate);
                }
            }
            return result;
        };
        if (noFuture) {
            this.ngModelCtrl.$validators['dvNoFutureDate'] = (modelValue: any, viewValue: any) => {
                let result: boolean = true;
                if (viewValue) {
                    let maxDateAsMoment: Moment = moment(moment.now());
                    let inputAsMoment: Moment = this.getInputAsMoment(modelValue, viewValue);
                    if (inputAsMoment && inputAsMoment.isAfter(maxDateAsMoment)) {
                        result = false;
                    }
                }
                return result;
            };
        }
        // Validator fuer Maximal-Datum
        this.ngModelCtrl.$validators['dvMaxDate'] = (modelValue: any, viewValue: any) => {
            let result: boolean = true;
            if (this.dvMaxDate && viewValue) {
                let maxDateAsMoment: Moment = moment(this.dvMaxDate, DatepickerController.allowedFormats, true);
                if (maxDateAsMoment.isValid()) {
                    let inputAsMoment: Moment = this.getInputAsMoment(modelValue, viewValue);
                    if (inputAsMoment && inputAsMoment.isAfter(maxDateAsMoment)) {
                        result = false;
                    }
                } else {
                    this.$log.debug('max date is invalid', this.dvMaxDate);
                }
            }
            return result;
        };
    }

    private getInputAsMoment(modelValue: any, viewValue: any): Moment {
        let value = modelValue || DatepickerController.stringToMoment(viewValue);
        let inputdate: Moment = moment(value, DatepickerController.allowedFormats, true);
        return inputdate;
    }

    onBlur() {
        if (this.dvOnBlur) { // userdefined onBlur event
            this.dvOnBlur();
        }
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
