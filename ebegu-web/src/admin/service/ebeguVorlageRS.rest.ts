import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IPromise} from 'angular';
import TSEbeguVorlage from '../../models/TSEbeguVorlage';
import IQService = angular.IQService;
import IHttpPromise = angular.IHttpPromise;


export class EbeguVorlageRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', 'Upload', '$q'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private upload: any, private $q: IQService) {
        this.serviceURL = REST_API + 'ebeguVorlage';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public getEbeguVorlagenByGesuchsperiode(gesuchsperiodeId: string): IPromise<TSEbeguVorlage[]> {
        return this.http.get(this.serviceURL + '/gesuchsperiode/' + gesuchsperiodeId)
            .then((response: any) => {
                return this.ebeguRestUtil.parseEbeguVorlages(response.data);
            });
    }

    public uploadVorlage(file: any, ebeguVorlage: TSEbeguVorlage, gesuchsperiodeID: string): IPromise<TSEbeguVorlage> {

        let restEbeguVorlage = {};
        restEbeguVorlage = this.ebeguRestUtil.ebeguVorlageToRestObject(restEbeguVorlage, ebeguVorlage);
        let restEbeguVorlageString = this.upload.json(restEbeguVorlage);

        return this.upload.upload({
            url: this.serviceURL,
            method: 'POST',
            headers: {
                'x-filename': file.name,
                'x-vorlagekey': ebeguVorlage.name,
                'x-gesuchsperiode': gesuchsperiodeID,
            },
            data: {
                file: file,
            }
        }).then((response: any) => {
            return this.ebeguRestUtil.parseEbeguVorlage(new TSEbeguVorlage(), response.data);
        }, (response: any) => {
            console.log('Upload File: NOT SUCCESS');
            return this.$q.reject();
        }, (evt: any) => {
            let loaded: number = evt.loaded;
            let total: number = evt.total;
            var progressPercentage: number = 100.0 * loaded / total;
            console.log('progress: ' + progressPercentage + '% ');
            return this.$q.defer().notify();
        });
    }

    public deleteEbeguVorlage(ebeguVorlageID: string): IHttpPromise<any> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(ebeguVorlageID));
    }


}
