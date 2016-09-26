import {IHttpService, ILogService, IPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSDownloadFile from '../../models/TSDownloadFile';
import {TSGeneratedDokumentTyp} from '../../models/enums/TSGeneratedDokumentTyp';


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

    public getAccessTokenDokument(dokumentID: string): IPromise<TSDownloadFile> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(dokumentID) + '/dokument')
            .then((response: any) => {
                this.log.debug('PARSING DownloadFile REST object ', response.data);
                return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
            });
    }

    public getAccessTokenVorlage(vorlageID: string): IPromise<TSDownloadFile> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(vorlageID) + '/vorlage')
            .then((response: any) => {
                this.log.debug('PARSING DownloadFile REST object ', response.data);
                return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
            });
    }

    public getAccessTokenGeneratedDokument(gesuchId: string, generatedDokumentTyp: TSGeneratedDokumentTyp, forceCreation: boolean): IPromise<TSDownloadFile> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuchId) + '/'
            + encodeURIComponent(TSGeneratedDokumentTyp[generatedDokumentTyp]) + '/' + forceCreation + '/generated')
            .then((response: any) => {
                this.log.debug('PARSING DownloadFile REST object ', response.data);
                return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
            });
    }

    public getAccessTokenVerfuegungGeneratedDokument(gesuchId: string, betreuungId: string, forceCreation: boolean): IPromise<TSDownloadFile> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuchId) + '/'
            + encodeURIComponent(betreuungId) + '/' + forceCreation + '/generatedVerfuegung')
            .then((response: any) => {
                this.log.debug('PARSING DownloadFile REST object ', response.data);
                return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
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
