import {IHttpService, ILogService, IPromise} from "angular";
import EbeguRestUtil from "../../utils/EbeguRestUtil";
import TSDownloadFile from "../../models/TSDownloadFile";


export class ReportRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', '$window'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService, private $window: ng.IWindowService) {
        this.serviceURL = REST_API + 'reporting';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getGesuchStichtagReportExcel(stichTag: string, gesuchPeriodeID: string): IPromise<TSDownloadFile> {
        return this.http.get(this.serviceURL + '/gesuchStichtag/excel'
            ,{params: {
                stichTag: stichTag,
                gesuchPeriodeID: gesuchPeriodeID
            }})
            .then((response: any) => {
                this.log.debug('PARSING ExceFile REST object ', response.data);
                return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
            });
    }

    public getGesuchPeriodeReportExcel(dateTimeFrom: string, dateTimeTo: string, gesuchPeriodeID: string): IPromise<TSDownloadFile> {
        return this.http.get(this.serviceURL + '/gesuchPeriode/excel'
            ,{params: {
                dateTimeFrom: dateTimeFrom,
                dateTimeTo: dateTimeTo,
                gesuchPeriodeID: gesuchPeriodeID
            }})
            .then((response: any) => {
                this.log.debug('PARSING ExcelFile REST object ', response.data);
                return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
            });
    }

    public getServiceName(): string {
        return 'ReportRS';
    }

}
