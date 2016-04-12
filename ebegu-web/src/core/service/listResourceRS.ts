import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSLand from '../../models/TSLand';
import {IPromise, IHttpService} from 'angular';

export default class ListResourceRS {
    static $inject = ['$http', 'REST_API', 'EbeguRestUtil'];

    static laenderList: TSLand[];

    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil) {
        this.serviceURL = REST_API + 'lists';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        ListResourceRS.laenderList = [];
    }

    static instance($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil): ListResourceRS {
        return new ListResourceRS($http, REST_API, ebeguRestUtil);
    }

    getLaenderList(): IPromise<TSLand[]> {
        return this.http.get(this.serviceURL + '/laender', {cache: true}).then((response: any) => {
            if (ListResourceRS.laenderList.length <= 0) { // wenn die Laenderliste schon ausgefuellt wurde, nichts machen
                for (var i = 0; i < response.data.length; i++) {
                    ListResourceRS.laenderList.push(this.ebeguRestUtil.landCodeToTSLand(response.data[i]));
                }
            }
            return ListResourceRS.laenderList;
        });
    }
}

