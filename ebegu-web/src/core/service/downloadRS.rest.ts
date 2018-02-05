/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import {IHttpService, IIntervalService, ILogService, IPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSDownloadFile from '../../models/TSDownloadFile';
import {TSGeneratedDokumentTyp} from '../../models/enums/TSGeneratedDokumentTyp';
import TSMahnung from '../../models/TSMahnung';
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

    public getFinSitDokumentAccessTokenGeneratedDokument(gesuchId: string): IPromise<TSDownloadFile> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuchId) + '/FINANZIELLE_SITUATION/generated')
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

    public getFreigabequittungAccessTokenGeneratedDokument(gesuchId: string, forceCreation: boolean): IPromise<TSDownloadFile> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuchId) + '/'
            + encodeURIComponent(TSGeneratedDokumentTyp[TSGeneratedDokumentTyp.FREIGABEQUITTUNG]) + '/' + forceCreation + '/generated')
            .then((response: any) => {
                this.log.debug('PARSING DownloadFile REST object ', response.data);
                return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
            });
    }

    public getAccessTokenMahnungGeneratedDokument(mahnung: TSMahnung): IPromise<TSDownloadFile> {
        let restMahnung = {};
        restMahnung = this.ebeguRestUtil.mahnungToRestObject(restMahnung, mahnung);
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(TSGeneratedDokumentTyp[TSGeneratedDokumentTyp.MAHNUNG]) + '/generated', restMahnung, {
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

    public getPain001AccessTokenGeneratedDokument(zahlungsauftragId: string): IPromise<TSDownloadFile> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(zahlungsauftragId) + '/'
            + encodeURIComponent(TSGeneratedDokumentTyp[TSGeneratedDokumentTyp.PAIN001]) + '/generated')
            .then((response: any) => {
                this.log.debug('PARSING DownloadFile REST object ', response.data);
                return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
            });
    }

    public getDokumentAccessTokenVerfuegungExport(betreuungId: string): IPromise<TSDownloadFile> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(betreuungId) + '/EXPORT')
            .then((response: any) => {
                this.log.debug('PARSING DownloadFile REST object ', response.data);
                return this.ebeguRestUtil.parseDownloadFile(new TSDownloadFile(), response.data);
            });
    }

    public getAccessTokenBenutzerhandbuch(): IPromise<TSDownloadFile> {
        return this.http.get(this.serviceURL + '/BENUTZERHANDBUCH')
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
            let href: string = this.serviceURL + '/blobdata/' + accessToken;
            if (attachment) {
                // add MatrixParam for to download file instead of opening it inline
                href += ';attachment=true;';
            } else {
                myWindow.focus();
            }
            //as soon as the window is ready send it to the download
            this.addCloseButtonHandler(myWindow);
            this.redirectWindowToDownloadWhenReady(myWindow, href, accessToken);

            //This would be the way to open file in new window (for now it's better to open in new tab)
            //this.$window.open(href, name, 'toolbar=0,location=0,menubar=0');
        } else {
            this.log.error('Download popup window was not initialized');
        }
    }

    prepareDownloadWindow(): Window {
        return this.$window.open('./src/assets/downloadWindow/downloadWindow.html', EbeguUtil.generateRandomName(5));
    }

    private redirectWindowToDownloadWhenReady(win: Window, href: string, name: string) {
        //wir pruefen den dokumentstatus alle 100ms, insgesamt maximal 300 mal
        let readyTimer: IPromise<any> = this.$interval(() => {
            if (win.document.readyState !== 'complete') {
                return;
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
        this.log.debug('hiding spinner');
        let element = win.document.getElementById('spinnerCont');
        if (element) {
            element.style.display = 'none';
        } else {
            console.log('element not found, can not hide spinner');
        }
        let buttonElement = win.document.getElementById('closeButton');
        if (buttonElement) {
            buttonElement.style.display = 'block';
            this.addCloseButtonHandler(win);
        }
    }

    public addCloseButtonHandler(win: Window) {
        let element = win.document.getElementById('closeButton');
        if (element) {
            element.addEventListener('click', () => {
                win.close();
            }, false);
        } else {
            console.log('element not found, can not attach window close handler spinner');
        }
    }
}
