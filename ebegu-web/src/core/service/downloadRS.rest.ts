import {IHttpService, ILogService, IPromise, IIntervalService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSDownloadFile from '../../models/TSDownloadFile';
import {TSGeneratedDokumentTyp} from '../../models/enums/TSGeneratedDokumentTyp';
import TSMahnung from '../../models/TSMahnung';
import {TSZustelladresse} from '../../models/enums/TSZustelladresse';
import EbeguUtil from '../../utils/EbeguUtil';

export class DownloadRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', '$window', '$interval'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService, private $window: ng.IWindowService,
    private $interval: IIntervalService) {
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

    /**
     *
     * @param accessToken
     * @param dokumentName
     * @param attachment
     * @param myWindow -> Das Window muss als Parameter mitgegeben werden, damit der Popup Blocker das Oeffnen dieses Fesnters nicht als Popup identifiziert.
     * @returns {boolean}
     */
    public startDownload(accessToken: string, dokumentName: string, attachment: boolean, myWindow: Window) {
        if (myWindow) {
            let name: string = accessToken + '/' + dokumentName;
            let href: string = this.serviceURL + '/blobdata/' + name;
            if (attachment) {
                // add MatrixParam for to download file instead of opening it inline
                href = href + ';attachment=true;';
            } else {
                myWindow.focus();
            }
            //as soon as the window is ready send it to the download
            this.redirectWindowToDownloadWhenReady(myWindow, href, name);

            //This would be the way to open file in new window (for now it's better to open in new tab)
            //this.$window.open(href, name, 'toolbar=0,location=0,menubar=0');
        } else {
            this.log.error("Download popup window was not initialized");

        }
    }

    prepareDownloadWindow(): Window {
        return  this.$window.open('./src/assets/downloadWindow/downloadWindow.html', EbeguUtil.generateRandomName(5));
    }

    private redirectWindowToDownloadWhenReady(win: Window, href: string, name: string) {
        //wir pruefen den dokumentstatus alle 100ms, insgesamt maximal 300 mal
        let readyTimer: IPromise<any> = this.$interval(() => {
            if (win.document.readyState !== 'complete') {
                return
            }
            this.$interval.cancel(readyTimer);
            //do stuff
            this.hideSpinner(win);
            win.open(href, win.name);
        }, 100, 300);
    }

    /**
     * Es kann sein, dass das popup noch gar nicht fertig gerendert ist bevor wir den spinner schon wieder verstecken wollen
     * in diesem fall warten wir noch bis das popup in den readyState 'conplete' wechselt und verstecken den spinner dann
     */
    public  hideSpinner(win: Window) {
        this.log.debug("hiding spinner");
        let element = win.document.getElementById("spinnerCont");
        if (element) {
            element.style.display = 'none';
        } else{
            console.log("element not found, can not hide spinner")
        }
        let buttonElement = win.document.getElementById("closeButton");
        if (buttonElement) {
            buttonElement.style.display = 'block';
        }

    }
}
