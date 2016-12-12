import {IComponentOptions} from 'angular';
import * as moment from 'moment';
import Moment = moment.Moment;
import DateUtil from '../../../utils/DateUtil';
import ITranslateService = angular.translate.ITranslateService;
import ILogService = angular.ILogService;
let template =  require('./dv-bisher.html');
require('./dv-bisher.less');

export class DvBisherComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        gs: '<',
        ja: '<',
    };
    template = template;
    controller = DvBisher;
    controllerAs = 'vm';
}

export class DvBisher {

    static $inject = ['$translate', '$log'];

    gs: any;
    ja: any;


    /* @ngInject */
    constructor(private $translate: ITranslateService, private $log: ILogService) {
    }

    public getBisher() : string {
        if (this.gs instanceof moment) {
            return  DateUtil.momentToLocalDateFormat(this.gs, 'DD.MM.YYYY');
        } else if (this.gs === true) {
            return this.$translate.instant('LABEL_JA');
        } else if (this.gs === false) {
            return this.$translate.instant('LABEL_NEIN');
        } else if (this.gs === null || this.gs === undefined || this.gs === '') {
            return this.$translate.instant('LABEL_KEINE_ANGABE');
        } else {
            return this.$translate.instant(this.gs);
        }
    }

    public equals(gs: any, ja: any) : boolean {
        if (gs instanceof moment) {
            return this.equals(DateUtil.momentToLocalDateFormat(gs, 'DD.MM.YYYY'), DateUtil.momentToLocalDateFormat(ja, 'DD.MM.YYYY'));
        }
        return gs === ja;
    }
}
