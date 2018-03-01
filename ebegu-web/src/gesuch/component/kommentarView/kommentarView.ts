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

import {IComponentOptions, IFormController, ILogService} from 'angular';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSGesuch from '../../../models/TSGesuch';
import GesuchRS from '../../service/gesuchRS.rest';
import DokumenteRS from '../../service/dokumenteRS.rest';
import {TSDokumentGrundTyp} from '../../../models/enums/TSDokumentGrundTyp';
import TSDokumenteDTO from '../../../models/dto/TSDokumenteDTO';
import TSDokumentGrund from '../../../models/TSDokumentGrund';
import TSDokument from '../../../models/TSDokument';
import TSDownloadFile from '../../../models/TSDownloadFile';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import {UploadRS} from '../../../core/service/uploadRS.rest';
import WizardStepManager from '../../service/wizardStepManager';
import TSWizardStep from '../../../models/TSWizardStep';
import GlobalCacheService from '../../service/globalCacheService';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {OkHtmlDialogController} from '../../dialog/OkHtmlDialogController';
import {TSCacheTyp} from '../../../models/enums/TSCacheTyp';
import GesuchstellerRS from '../../../core/service/gesuchstellerRS.rest';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import {TSAntragStatus} from '../../../models/enums/TSAntragStatus';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import {IStateService} from 'angular-ui-router';
import IPromise = angular.IPromise;
import IQService = angular.IQService;
import ITranslateService = angular.translate.ITranslateService;
import IRootScopeService = angular.IRootScopeService;
let template = require('./kommentarView.html');
require('./kommentarView.less');
let okHtmlDialogTempl = require('../../../gesuch/dialog/okHtmlDialogTemplate.html');
let removeDialogTempl = require('../../dialog/removeDialogTemplate.html');

export class KommentarViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = KommentarViewController;
    controllerAs = 'vm';
}

/**
 * Controller fuer den Kommentare
 */
export class KommentarViewController {

    form: IFormController;
    dokumentePapiergesuch: TSDokumentGrund;
    TSRoleUtil: any;

    static $inject: string[] = ['$log', 'GesuchModelManager', 'GesuchRS', 'DokumenteRS', 'DownloadRS', '$q', 'UploadRS',
        'WizardStepManager', 'GlobalCacheService', 'DvDialog', '$translate', '$window', 'GesuchstellerRS', '$rootScope', '$state', '$mdSidenav'];
    /* @ngInject */
    constructor(private $log: ILogService, private gesuchModelManager: GesuchModelManager, private gesuchRS: GesuchRS,
                private dokumenteRS: DokumenteRS, private downloadRS: DownloadRS, private $q: IQService,
                private uploadRS: UploadRS, private wizardStepManager: WizardStepManager, private globalCacheService: GlobalCacheService,
                private dvDialog: DvDialog, private $translate: ITranslateService, private $window: ng.IWindowService, private gesuchstellerRS: GesuchstellerRS,
                private $rootScope: IRootScopeService, private $state: IStateService, private $mdSidenav: ng.material.ISidenavService) {

        if (!this.isGesuchUnsaved()) {
            this.getPapiergesuchFromServer();
        }
        this.TSRoleUtil = TSRoleUtil;
    }

    private getPapiergesuchFromServer(): IPromise<TSDokumenteDTO> {

        return this.dokumenteRS.getDokumenteByTypeCached(
            this.getGesuch(), TSDokumentGrundTyp.PAPIERGESUCH, this.globalCacheService.getCache(TSCacheTyp.EBEGU_DOCUMENT))
            .then((promiseValue: TSDokumenteDTO) => {

                if (promiseValue.dokumentGruende.length === 1) {
                    this.dokumentePapiergesuch = promiseValue.dokumentGruende[0];
                } else {
                    console.log('Falsche anzahl Dokumente');
                }
                return promiseValue;
            });
    }

    getGesuch(): TSGesuch {
        return this.gesuchModelManager.getGesuch();
    }
    public toggleEwkSidenav() {
        this.$mdSidenav('ewk').toggle();
    }

    public saveBemerkungZurVerfuegung(): void {
        if (!this.isGesuchUnsaved()) {
            // Bemerkungen auf dem Gesuch werden nur gespeichert, wenn das gesuch schon persisted ist!
            this.gesuchRS.updateBemerkung(this.getGesuch().id, this.getGesuch().bemerkungen);
        }
    }

    public saveBemerkungPruefungSTV(): void {
        if (!this.isGesuchUnsaved()) {
            // Bemerkungen auf dem Gesuch werden nur gespeichert, wenn das gesuch schon persisted ist!
            this.gesuchRS.updateBemerkungPruefungSTV(this.getGesuch().id, this.getGesuch().bemerkungenPruefungSTV);
        }
    }

    public saveStepBemerkung(): void {
        if (!this.isGesuchUnsaved()) {
            this.wizardStepManager.updateCurrentWizardStep();
        }
    }

    hasPapiergesuch(): boolean {
        if (this.dokumentePapiergesuch) {
            if (this.dokumentePapiergesuch.dokumente && this.dokumentePapiergesuch.dokumente.length !== 0) {
                if (this.dokumentePapiergesuch.dokumente[0].filename) {
                    return true;
                }
            }
        }
        return false;
    }

    download() {
        let win: Window = this.downloadRS.prepareDownloadWindow();
        this.getPapiergesuchFromServer().then((promiseValue: any) => {
            if (!this.hasPapiergesuch()) {
                this.$log.error('Kein Papiergesuch für Download vorhanden!');
            } else {
                let newest: TSDokument = this.getNewest(this.dokumentePapiergesuch.dokumente);
                this.downloadRS.getAccessTokenDokument(newest.id)
                    .then((response) => {
                        let tempDokument: TSDownloadFile = angular.copy(response);
                        this.downloadRS.startDownload(tempDokument.accessToken, newest.filename, false, win);
                    })
                    .catch((ex) => {
                        win.close();
                        this.$log.error('An error occurred downloading the document, closing download window.');
                    });
            }
        });
    }

    private getNewest(dokumente: Array<TSDokument>): TSDokument {
        let newest: TSDokument = dokumente[0];
        for (let i = 0; i < dokumente.length; i++) {
            if (dokumente[i].timestampErstellt.isAfter(newest.timestampErstellt)) {
                newest = dokumente[i];
            }
        }
        return newest;

    }

    upload(files: any[]) {
        this.getPapiergesuchFromServer().then((promiseValue: any) => {
            if (this.hasPapiergesuch()) {
                this.$log.error('Papiergesuch schon vorhanden');
            } else {
                let gesuchID = this.getGesuch().id;
                console.log('Uploading files on gesuch ' + gesuchID);

                let filesTooBig: any[] = [];
                let filesOk: any[] = [];
                this.$log.debug('Uploading files on gesuch ' + gesuchID);
                for (let file of files) {
                    this.$log.debug('File: ' + file.name + ' size: ' + file.size);
                    if (file.size > 10000000) { // Maximale Filegrösse ist 10MB
                        filesTooBig.push(file);
                    } else {
                        filesOk.push(file);
                    }
                }

                if (filesTooBig.length > 0) {
                    // DialogBox anzeigen für Files, welche zu gross sind!
                    let returnString = this.$translate.instant('FILE_ZU_GROSS') + '<br/><br/>';
                    returnString += '<ul>';
                    for (let file of filesTooBig) {
                        returnString += '<li>';
                        returnString += file.name;
                        returnString += '</li>';
                    }
                    returnString += '</ul>';

                    this.dvDialog.showDialog(okHtmlDialogTempl, OkHtmlDialogController, {
                        title: returnString
                    });
                }

                if (filesOk.length > 0) {
                    this.uploadRS.uploadFile(filesOk, this.dokumentePapiergesuch, gesuchID).then((response) => {
                        this.dokumentePapiergesuch = angular.copy(response);
                        this.globalCacheService.getCache(TSCacheTyp.EBEGU_DOCUMENT).removeAll();
                    });
                }
            }
        });
    }

    isGesuchUnsaved(): boolean {
        return this.getGesuch().isNew();
    }

    public getCurrentWizardStep(): TSWizardStep {
        return this.wizardStepManager.getCurrentStep();
    }

    public isGesuchReadonly(): boolean {
        return this.gesuchModelManager.isGesuchReadonly();
    }

    public isInBearbeitungSTV(): boolean {
        return this.gesuchModelManager.getGesuch().status === TSAntragStatus.IN_BEARBEITUNG_STV;
    }

    public freigebenSTV(): void {
        this.dvDialog.showRemoveDialog(removeDialogTempl, this.form, RemoveDialogController, {
            title: this.getFreigabeTitel(),
            deleteText: this.getFreigabeBeschreibung(),
            parentController: undefined,
            elementID: undefined
        }).then(() => {
            return this.gesuchRS.gesuchBySTVFreigeben(this.getGesuch().id).then((gesuch: TSGesuch) => {
                this.gesuchModelManager.setGesuch(gesuch);
                this.$state.go('pendenzenSteueramt');
            });
        });
    }

    public showBemerkungenPruefungSTV(): boolean {
        return this.getGesuch().geprueftSTV === true || this.getGesuch().status === TSAntragStatus.PRUEFUNG_STV || this.getGesuch().status === TSAntragStatus.IN_BEARBEITUNG_STV
            || this.getGesuch().status === TSAntragStatus.GEPRUEFT_STV;
    }

    public getFreigabeName(): string {
        return this.$translate.instant(this.getFreigabeTitel());
    }

    public getFreigabeTitel(): string {
        if (this.getGesuch().areThereOnlySchulamtAngebote()) {
            return 'FREIGABE_SCH';
        }
        return 'FREIGABE_JA';
    }

    public getFreigabeBeschreibung(): string {
        if (this.getGesuch().areThereOnlySchulamtAngebote()) {
            return 'FREIGABE_SCH_BESCHREIBUNG';
        }
        return 'FREIGABE_JA_BESCHREIBUNG';
    }
}
