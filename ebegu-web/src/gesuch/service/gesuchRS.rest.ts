import {IEntityRS} from '../../core/service/iEntityRS.rest';
import {IHttpPromise, IHttpService, IPromise, ILogService} from 'angular';
import TSGesuch from '../../models/TSGesuch';
import EbeguRestUtil from '../../utils/EbeguRestUtil';

export default class GesuchRS implements IEntityRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private $log: ILogService) {
        this.serviceURL = REST_API + 'gesuche';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public createGesuch(gesuch: TSGesuch): IHttpPromise<TSGesuch> {
        let sentGesuch = {};
        sentGesuch = this.ebeguRestUtil.gesuchToRestObject(sentGesuch, gesuch);
        return this.http.post(this.serviceURL, sentGesuch, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public updateGesuch(gesuch: TSGesuch): IHttpPromise<TSGesuch> {
        let sentGesuch = {};
        sentGesuch = this.ebeguRestUtil.gesuchToRestObject(sentGesuch, gesuch);
        return this.http.put(this.serviceURL, sentGesuch, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public findGesuch(gesuchID: string): IPromise<TSGesuch> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuchID))
            .then((response: any) => {
                this.$log.debug('PARSING gesuch REST object ', response.data);
                return this.ebeguRestUtil.parseGesuch(new TSGesuch(), response.data);
            });
    }

}
