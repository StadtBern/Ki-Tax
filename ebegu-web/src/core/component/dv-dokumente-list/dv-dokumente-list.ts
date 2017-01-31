import {IComponentOptions, ILogService} from 'angular';
import TSDokumentGrund from '../../../models/TSDokumentGrund';
import {UploadRS} from '../../service/uploadRS.rest';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import EbeguUtil from '../../../utils/EbeguUtil';
import TSDokument from '../../../models/TSDokument';
import {DownloadRS} from '../../service/downloadRS.rest';
import TSDownloadFile from '../../../models/TSDownloadFile';
import {DvDialog} from '../../directive/dv-dialog/dv-dialog';
import {RemoveDialogController} from '../../../gesuch/dialog/RemoveDialogController';
import WizardStepManager from '../../../gesuch/service/wizardStepManager';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {OkHtmlDialogController} from '../../../gesuch/dialog/OkHtmlDialogController';
import ITranslateService = angular.translate.ITranslateService;
let template = require('./dv-dokumente-list.html');
let removeDialogTemplate = require('../../../gesuch/dialog/removeDialogTemplate.html');
require('./dv-dokumente-list.less');
let okHtmlDialogTempl = require('../../../gesuch/dialog/okHtmlDialogTemplate.html');

export class DVDokumenteListConfig implements IComponentOptions {
    transclude = false;

    bindings: any = {
        dokumente: '<',
        tableId: '@',
        tableTitle: '@',
        tag: '@',
        titleValue: '<',
        onUploadDone: '&',
        onRemove: '&',
        sonstige: '<'

    };
    template = template;
    controller = DVDokumenteListController;
    controllerAs = 'vm';
}

export class DVDokumenteListController {

    dokumente: TSDokumentGrund[];
    tableId: string;
    tableTitle: string;
    tag: string;
    titleValue: string;
    onUploadDone: (dokumentGrund: any) => void;
    onRemove: (attrs: any) => void;
    sonstige: boolean;

    static $inject: any[] = ['UploadRS', 'GesuchModelManager', 'EbeguUtil', 'DownloadRS', 'DvDialog', 'WizardStepManager',
        '$log', 'AuthServiceRS', '$translate', '$window'];
    /* @ngInject */
    constructor(private uploadRS: UploadRS, private gesuchModelManager: GesuchModelManager, private ebeguUtil: EbeguUtil,
                private downloadRS: DownloadRS, private dvDialog: DvDialog, private wizardStepManager: WizardStepManager,
                private $log: ILogService, private authServiceRS: AuthServiceRS, private $translate: ITranslateService,
                private $window: ng.IWindowService) {

    }

    $onInit() {

    }

    uploadAnhaenge(files: any[], selectDokument: TSDokumentGrund) {
        if (this.gesuchModelManager.getGesuch()) {
            let gesuchID = this.gesuchModelManager.getGesuch().id;
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
                this.uploadRS.uploadFile(filesOk, selectDokument, gesuchID).then((response) => {
                    let returnedDG: TSDokumentGrund = angular.copy(response);
                    this.wizardStepManager.findStepsFromGesuch(this.gesuchModelManager.getGesuch().id).then(() => {
                        this.handleUpload(returnedDG);
                    });
                });
            }
        } else {
            this.$log.warn('No gesuch found to store file or gesuch is status verfuegt');
        }
    }

    hasDokuments(selectDokument: TSDokumentGrund): boolean {
        if (selectDokument.dokumente) {
            for (let dokument of selectDokument.dokumente) {
                if (dokument.filename) {
                    return true;
                }
            }
        }
        return false;
    }

    handleUpload(returnedDG: TSDokumentGrund) {
        this.onUploadDone({dokument: returnedDG});
    }

    remove(dokumentGrund: TSDokumentGrund, dokument: TSDokument) {
        this.$log.debug('component -> remove dokument ' + dokument.filename);
        this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
            deleteText: '',
            title: 'FILE_LOESCHEN'
        })
            .then(() => {   //User confirmed removal
                this.onRemove({dokumentGrund: dokumentGrund, dokument: dokument});

            });
    }

    download(dokument: TSDokument, attachment: boolean) {
        this.$log.debug('download dokument ' + dokument.filename);
        let win: Window = this.$window.open('about:blank', EbeguUtil.generateRandomName(5));

        this.downloadRS.getAccessTokenDokument(dokument.id).then((downloadFile: TSDownloadFile) => {
            this.$log.debug('accessToken: ' + downloadFile.accessToken);
            this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, attachment, win);
        });
    }

    getWidth(): String {
        if (this.sonstige) {
            return '95%';
        } else {
            if (this.tag) {
                return '45%';
            } else {
                return '60%';
            }
        }
    }

    public isGesuchReadonly(): boolean {
        return this.gesuchModelManager.isGesuchReadonly();
    }
}


