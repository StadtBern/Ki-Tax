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
import {TSRole} from '../../../models/enums/TSRole';
let template = require('./dv-dokumente-list.html');
let removeDialogTemplate = require('../../../gesuch/dialog/removeDialogTemplate.html');
require('./dv-dokumente-list.less');

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
        '$log', 'AuthServiceRS'];
    /* @ngInject */
    constructor(private uploadRS: UploadRS, private gesuchModelManager: GesuchModelManager, private ebeguUtil: EbeguUtil,
                private downloadRS: DownloadRS, private dvDialog: DvDialog, private wizardStepManager: WizardStepManager,
                private $log: ILogService, private authServiceRS: AuthServiceRS) {

    }

    $onInit() {

    }

    uploadAnhaenge(files: any[], selectDokument: TSDokumentGrund) {
        if (this.isUploadVisible() && this.gesuchModelManager.getGesuch()) {
            let gesuchID = this.gesuchModelManager.getGesuch().id;
            this.$log.debug('Uploading files on gesuch ' + gesuchID);
            for (let file of files) {
                this.$log.debug('File: ' + file.name);
            }

            this.uploadRS.uploadFile(files, selectDokument, gesuchID).then((response) => {
                let returnedDG: TSDokumentGrund = angular.copy(response);
                this.wizardStepManager.findStepsFromGesuch(this.gesuchModelManager.getGesuch().id).then(() => {
                    this.handleUpload(returnedDG);
                });
            });
        } else {
            this.$log.warn('No gesuch found to store file or gesuch is status verfuegt');
        }
    }

    hasDokuments(selectDokument: TSDokumentGrund): boolean {
        if (selectDokument.dokumente) {
            for (var dokument of selectDokument.dokumente) {
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

        this.downloadRS.getAccessTokenDokument(dokument.id).then((downloadFile: TSDownloadFile) => {
            this.$log.debug('accessToken: ' + downloadFile.accessToken);
            this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, attachment);
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

    public isUploadVisible(): boolean {
        if (this.authServiceRS.isRole(TSRole.GESUCHSTELLER)) {
            return true; //gesuchsteller kann immer dokumente hochladen
        } else {
            return !this.isGesuchReadonly(); //fuer alle andern nicht verfuegbar wenn gesuch im readonly modus
        }
    }
}



