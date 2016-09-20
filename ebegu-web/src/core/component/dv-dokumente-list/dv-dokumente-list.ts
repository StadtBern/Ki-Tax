import {IComponentOptions} from 'angular';
import TSDokumentGrund from '../../../models/TSDokumentGrund';
import {UploadRS} from '../../service/uploadRS.rest';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import EbeguUtil from '../../../utils/EbeguUtil';
import TSDokument from '../../../models/TSDokument';
import {DownloadRS} from '../../service/downloadRS.rest';
import TSTempDokument from '../../../models/TSTempDokument';
import {DvDialog} from '../../directive/dv-dialog/dv-dialog';
import {RemoveDialogController} from '../../../gesuch/dialog/RemoveDialogController';
import WizardStepManager from '../../../gesuch/service/wizardStepManager';
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

    static $inject: any[] = ['UploadRS', 'GesuchModelManager', 'EbeguUtil', 'DownloadRS', 'DvDialog', 'WizardStepManager'];
    /* @ngInject */
    constructor(private uploadRS: UploadRS, private gesuchModelManager: GesuchModelManager, private ebeguUtil: EbeguUtil,
                private downloadRS: DownloadRS, private dvDialog: DvDialog, private wizardStepManager: WizardStepManager) {

    }

    $onInit() {

    }

    uploadAnhaenge(files: any[], selectDokument: TSDokumentGrund) {

        if (!this.isGesuchStatusVerfuegenVerfuegt() && this.gesuchModelManager.getGesuch()) {
            let gesuchID = this.gesuchModelManager.getGesuch().id;
            console.log('Uploading files on gesuch ' + gesuchID);
            for (var file of files) {
                console.log('File: ' + file.name);
            }

            this.uploadRS.uploadFile(files, selectDokument, gesuchID).then((response) => {
                let returnedDG: TSDokumentGrund = angular.copy(response);
                this.wizardStepManager.findStepsFromGesuch(this.gesuchModelManager.getGesuch().id).then(() => {
                    this.handleUpload(returnedDG);
                });
            });
        } else {
            console.log('No gesuch found to store file or gesuch is status verfuegt');
        }
    }

    hasDokuments(selectDokument: TSDokumentGrund): boolean {
        if (selectDokument.dokumente) {
            for (var dokument of selectDokument.dokumente) {
                if (dokument.dokumentName) {
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
        console.log('component -> remove dokument ' + dokument.dokumentName);
        this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
            deleteText: '',
            title: 'FILE_LOESCHEN'
        })
            .then(() => {   //User confirmed removal
                this.onRemove({dokumentGrund: dokumentGrund, dokument: dokument});

            });
    }

    download(dokument: TSDokument, attachment: boolean) {
        console.log('download dokument ' + dokument.dokumentName);

        this.downloadRS.getAccessToken(dokument.id).then((response) => {
            let tempDokument: TSTempDokument = angular.copy(response);
            console.log('accessToken: ' + tempDokument.accessToken);

            this.downloadRS.startDownload(tempDokument.accessToken, dokument.dokumentName, attachment);
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

    public isGesuchStatusVerfuegenVerfuegt(): boolean {
        return this.gesuchModelManager.isGesuchStatusVerfuegenVerfuegt();
    }


}



