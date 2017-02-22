import {IHttpService, ILogService, IPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';

export default class ExportRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private $log: ILogService) {
        this.serviceURL = REST_API + 'export';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public exportVerfuegungenOfAntrag(gesuchID: string): IPromise<any> {
        return this.http.get(this.serviceURL +'/gesuch/' +  encodeURIComponent(gesuchID), {
        }).then((response: any) => {
            return this.$log.debug('PARSING fall REST object ', response.data);
        });
    }

}
