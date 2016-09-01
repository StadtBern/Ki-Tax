import {IHttpService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSEinkommensverschlechterungInfo from '../../models/TSEinkommensverschlechterungInfo';
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;


export default class EinkommensverschlechterungInfoRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'einkommensverschlechterungInfo';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public saveEinkommensverschlechterungInfo(einkommensverschlechterungInfo: TSEinkommensverschlechterungInfo,
                                              gesuchId: string): IPromise<TSEinkommensverschlechterungInfo> {
        let returnedEinkommensverschlechterungInfo = {};
        returnedEinkommensverschlechterungInfo =
            this.ebeguRestUtil.einkommensverschlechterungInfoToRestObject(returnedEinkommensverschlechterungInfo, einkommensverschlechterungInfo);
        return this.http.put(this.serviceURL + '/' + gesuchId, returnedEinkommensverschlechterungInfo, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((httpresponse: any) => {
            this.log.debug('PARSING EinkommensverschlechterungInfo REST object ', httpresponse.data);
            return this.ebeguRestUtil.parseEinkommensverschlechterungInfo(new TSEinkommensverschlechterungInfo(), httpresponse.data);
        });
    }

}
