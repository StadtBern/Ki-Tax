import {IComponentOptions, ILogService} from 'angular';
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
import IFormController = angular.IFormController;
import IPromise = angular.IPromise;
import IQService = angular.IQService;
import ICacheFactoryService = angular.ICacheFactoryService;
import ITranslateService = angular.translate.ITranslateService;
import EbeguUtil from '../../../utils/EbeguUtil';
let template = require('./kommentarView.html');
require('./kommentarView.less');
let okHtmlDialogTempl = require('../../../gesuch/dialog/okHtmlDialogTemplate.html');

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

    dokumentePapiergesuch: TSDokumentGrund;

    static $inject: string[] = ['$log', 'GesuchModelManager', 'GesuchRS', 'DokumenteRS', 'DownloadRS', '$q', 'UploadRS',
        'WizardStepManager', 'GlobalCacheService', 'DvDialog', '$translate', '$window'];
    /* @ngInject */
    constructor(private $log: ILogService, private gesuchModelManager: GesuchModelManager, private gesuchRS: GesuchRS,
                private dokumenteRS: DokumenteRS, private downloadRS: DownloadRS, private $q: IQService,
                private uploadRS: UploadRS, private wizardStepManager: WizardStepManager, private globalCacheService: GlobalCacheService,
                private dvDialog: DvDialog, private $translate: ITranslateService, private $window: ng.IWindowService) {

        if (!this.isGesuchUnsaved()) {
            this.getPapiergesuchFromServer();
        }
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

    public saveBemerkungZurVerfuegung(): void {
        if (!this.isGesuchUnsaved()) {
            // Bemerkungen auf dem Gesuch werden nur gespeichert, wenn das gesuch schon persisted ist!
            this.gesuchRS.updateBemerkung(this.getGesuch().id, this.getGesuch().bemerkungen);
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
        let win: Window = this.$window.open('about:blank', EbeguUtil.generateRandomName(5));
        this.getPapiergesuchFromServer().then((promiseValue: any) => {
            if (!this.hasPapiergesuch()) {
                this.$log.error('Kein Papiergesuch für Download vorhanden!');
            } else {
                let newest: TSDokument = this.getNewest(this.dokumentePapiergesuch.dokumente);
                this.downloadRS.getAccessTokenDokument(newest.id).then((response) => {
                    let tempDokument: TSDownloadFile = angular.copy(response);
                    this.downloadRS.startDownload(tempDokument.accessToken, newest.filename, false, win);
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

}
