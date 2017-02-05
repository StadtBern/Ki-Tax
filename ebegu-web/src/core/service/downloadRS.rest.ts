import {IHttpService, ILogService, IPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSDownloadFile from '../../models/TSDownloadFile';
import {TSGeneratedDokumentTyp} from '../../models/enums/TSGeneratedDokumentTyp';
import TSMahnung from '../../models/TSMahnung';
import {TSZustelladresse} from '../../models/enums/TSZustelladresse';


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

    public getFinSitDokumentAccessTokenGeneratedDokument(gesuchId: string, forceCreation: boolean): IPromise<TSDownloadFile> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuchId) + '/FINANZIELLE_SITUATION/' + forceCreation + '/generated')
            .then((response: any) => {
                this.log.debug('PARSING DownloadFile REST object ', response.data);
                return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
            });
    }

    public getBegleitschreibenDokumentAccessTokenGeneratedDokument(gesuchId: string, forceCreation: boolean): IPromise<TSDownloadFile> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuchId) + '/BEGLEITSCHREIBEN/' + forceCreation + '/generated')
            .then((response: any) => {
                this.log.debug('PARSING DownloadFile REST object ', response.data);
                return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
            });
    }

    public getFreigabequittungAccessTokenGeneratedDokument(gesuchId: string, forceCreation: boolean, zustelladresse: TSZustelladresse): IPromise<TSDownloadFile> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuchId) + '/'
            + encodeURIComponent(TSGeneratedDokumentTyp[TSGeneratedDokumentTyp.FREIGABEQUITTUNG]) + '/' + forceCreation + '/generated',
            {params: {zustelladresse: TSZustelladresse[zustelladresse]}})
            .then((response: any) => {
                this.log.debug('PARSING DownloadFile REST object ', response.data);
                return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
            });
    }

    public getAccessTokenMahnungGeneratedDokument(mahnung: TSMahnung, forceCreation: boolean): IPromise<TSDownloadFile> {
        let restMahnung = {};
        restMahnung = this.ebeguRestUtil.mahnungToRestObject(restMahnung, mahnung);
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(TSGeneratedDokumentTyp[TSGeneratedDokumentTyp.MAHNUNG])
            + '/' + forceCreation + '/generated', restMahnung, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING DownloadFile REST object ', response.data);
            return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
        });
    }

    public getAccessTokenVerfuegungGeneratedDokument(gesuchId: string, betreuungId: string, forceCreation: boolean, manuelleBemerkungen: string): IPromise<TSDownloadFile> {
        return this.http.post(this.serviceURL + '/' + encodeURIComponent(gesuchId) + '/' + encodeURIComponent(betreuungId)
            + '/' + encodeURIComponent(TSGeneratedDokumentTyp[TSGeneratedDokumentTyp.VERFUEGUNG]) + '/' + forceCreation + '/generated', manuelleBemerkungen, {
            headers: {
                'Content-Type': 'text/plain'
            }
        }).then((response: any) => {
            this.log.debug('PARSING DownloadFile REST object ', response.data);
            return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
        });
    }

    public getAccessTokenNichteintretenGeneratedDokument(betreuungId: string, forceCreation: boolean): IPromise<TSDownloadFile> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(betreuungId)
            + '/' + encodeURIComponent(TSGeneratedDokumentTyp[TSGeneratedDokumentTyp.NICHTEINTRETEN]) + '/' + forceCreation + '/generated')
            .then((response: any) => {
                this.log.debug('PARSING DownloadFile REST object ', response.data);
                return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
            });
    }


    public getServiceName(): string {
        return 'DownloadRS';
    }

    public startDownload(accessToken: string, dokumentName: string, attachment: boolean): boolean {
        let name: string = accessToken + '/' + dokumentName;
        let href: string = this.serviceURL + '/blobdata/' + name;




        if (attachment) {
            // add MatrixParam for to download file instead of inline
            this.download(href);
        } else {
            let win =this.$window.open(href, '_blank');
            if (!win) {
                let warn: string = 'Popup-Blocker scheint eingeschaltet zu sein. ' +
                    'Dadurch kann das Dokument im Browser nicht angezeigt werden und wird heruntergeladen. '+
                    'Bitte erlauben Sie der Seite Pop-Ups öffnen zu dürfen, um das Dokument im Browser anzuzeigen.';
                this.log.error(warn);
                this.download(href);
                this.$window.alert(warn);
                return false;
            } else {
                win.focus();
            }
        }

        //This would be the way to open file in new window (for now it's better to open in new tab)
        //this.$window.open(href, name, 'toolbar=0,location=0,menubar=0');
        return true;

    }

    private download(href: string) {
        href = href + ';attachment=true;';
        this.$window.location.href = href;
        return href;
    }
}
