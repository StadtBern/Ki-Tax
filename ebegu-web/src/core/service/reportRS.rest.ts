import {IHttpParamSerializer, ILogService} from 'angular';
import IPromise = angular.IPromise;
import TSDownloadFile from "../../models/TSDownloadFile";
import EbeguRestUtil from "../../utils/EbeguRestUtil";
import IHttpService = angular.IHttpService;

export class ReportRS {
    serviceURL: string;
    httpParamSerializer: IHttpParamSerializer;
    log: ILogService;
    ebeguRestUtil: EbeguRestUtil;
    http: IHttpService;

    static $inject = ['$httpParamSerializer', 'REST_API', '$log', '$window', 'EbeguRestUtil', '$http'];
    /* @ngInject */
    constructor($httpParamSerializer: IHttpParamSerializer, REST_API: string, $log: ILogService, private $window: ng.IWindowService, ebeguRestUtil: EbeguRestUtil, $http: IHttpService) {
        this.serviceURL = REST_API + 'reporting';
        this.httpParamSerializer = $httpParamSerializer;
        this.log = $log;
        this.ebeguRestUtil = ebeguRestUtil;
        this.http = $http;
    }

    public getGesuchStichtagReportExcel(dateTimeStichtag: string, gesuchPeriodeID: string): IPromise<TSDownloadFile> {

        let reportParams: string = this.httpParamSerializer({
            dateTimeStichtag: dateTimeStichtag,
            gesuchPeriodeID: gesuchPeriodeID
        });

        return this.http.get(this.serviceURL + '/excel/gesuchStichtag?' + reportParams)
            .then((response: any) => {
                this.log.debug('PARSING DownloadFile REST object ', response.data);
                return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
            });

    }

    public getGesuchZeitraumReportExcel(dateTimeFrom: string, dateTimeTo: string, gesuchPeriodeID: string): IPromise<TSDownloadFile> {

        let reportParams: string = this.httpParamSerializer({
            dateTimeFrom: dateTimeFrom,
            dateTimeTo: dateTimeTo,
            gesuchPeriodeID: gesuchPeriodeID
        });

        return this.http.get(this.serviceURL + '/excel/gesuchZeitraum?' + reportParams)
            .then((response: any) => {
                this.log.debug('PARSING DownloadFile REST object ', response.data);
                return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
            });
    }

    public getServiceName(): string {
        return 'ReportRS';
    }

}