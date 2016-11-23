import {IEntityRS} from '../../core/service/iEntityRS.rest';
import {IHttpPromise, IHttpService, IPromise, ILogService} from 'angular';
import TSGesuch from '../../models/TSGesuch';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSMahnung from '../../models/TSMahnung';

export default class MahnungRS implements IEntityRS {

    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private $log: ILogService) {
        this.serviceURL = REST_API + 'mahnung';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public saveMahnung(mahnung: TSMahnung): IPromise<TSMahnung> {
        let sentMahnung = {};
        sentMahnung = this.ebeguRestUtil.mahnungToRestObject(sentMahnung, mahnung);
        return this.http.post(this.serviceURL, sentMahnung, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.$log.debug('PARSING gesuch REST object ', response.data);
            return this.ebeguRestUtil.parseMahnung(new TSMahnung(), response.data);
        });
    }

    public findMahnungen(gesuch: TSGesuch): IPromise<TSMahnung[]> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuch.id))
            .then((response: any) => {
                return this.ebeguRestUtil.parseMahnungen(response.data);
            });
    }

    public mahnlaufBeenden(gesuch: TSGesuch): IHttpPromise<any> {
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(gesuch.id), {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public getInitialeBemerkungen(gesuch: TSGesuch): IHttpPromise<string> {
        return this.http.get(this.serviceURL + '/bemerkungen/' + encodeURIComponent(gesuch.id), {
            headers: {
                'Content-Type': 'text/plain'
            }
        });
    }
}
