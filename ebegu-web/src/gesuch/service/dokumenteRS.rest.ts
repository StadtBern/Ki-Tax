import {IHttpService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSGesuch from '../../models/TSGesuch';
import TSDokumenteDTO from '../../models/dto/TSDokumenteDTO';
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;


export default class DokumenteRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'dokumente';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getDokumente(gesuch: TSGesuch): IPromise<TSDokumenteDTO> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuch.id))
            .then((response: any) => {
                this.log.debug('PARSING dokumentDTA REST object ', response.data);
                return this.ebeguRestUtil.parseDokumenteDTO(new TSDokumenteDTO(), response.data);
            });
    }

}
