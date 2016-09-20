import {IHttpService, ILogService, IPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSTempDokument from '../../models/TSTempDokument';


export class DownloadRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', '$window'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService, private $window: ng.IWindowService) {
        this.serviceURL = REST_API + 'blobs/temp';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getAccessTokenDokument(dokumentID: string): IPromise<TSTempDokument> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(dokumentID) + '/dokument')
            .then((response: any) => {
                this.log.debug('PARSING tempDokument REST object ', response.data);
                return this.ebeguRestUtil.parseTempDokument(new TSTempDokument(), response.data);
            });
    }

    public getAccessTokenVorlage(vorlageID: string): IPromise<TSTempDokument> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(vorlageID) + '/vorlage')
            .then((response: any) => {
                this.log.debug('PARSING tempDokument REST object ', response.data);
                return this.ebeguRestUtil.parseTempDokument(new TSTempDokument(), response.data);
            });
    }


    public getServiceName(): string {
        return 'DownloadRS';
    }

    public startDownload(accessToken: string, dokumentName: string, attachment: boolean) {
        let name: string = accessToken + '/' + dokumentName;
        let href: string = this.serviceURL + '/blobdata/' + name;

        if (attachment) {
            // add MatrixParam for to download file instead of inline
            href = href + ';attachment=true;';
            this.$window.open(href, '_blank');
        } else {
            let win = this.$window.open(href, '_blank');
            win.focus();
        }

        //This would be the way to open file in new window (for now it's better to open in new tab)
        //this.$window.open(href, name, 'toolbar=0,location=0,menubar=0');

    }
}
