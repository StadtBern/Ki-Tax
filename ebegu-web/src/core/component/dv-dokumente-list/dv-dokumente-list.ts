import {IComponentOptions} from 'angular';
import TSDokumentGrund from '../../../models/TSDokumentGrund';
import {UploadRS} from '../../service/uploadRS.rest';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import EbeguUtil from '../../../utils/EbeguUtil';
import TSDokument from '../../../models/TSDokument';
let template = require('./dv-dokumente-list.html');
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

    static $inject: any[] = ['UploadRS', 'GesuchModelManager', 'EbeguUtil'];
    /* @ngInject */
    constructor(private uploadRS: UploadRS, private gesuchModelManager: GesuchModelManager, ebeguUtil: EbeguUtil) {

    }

    $onInit() {

    }

    uploadAnhaenge(files: any[], selectDokument: TSDokumentGrund) {

        if (this.gesuchModelManager.gesuch) {
            let gesuchID = this.gesuchModelManager.gesuch.id;
            console.log('Uploading files on gesuch ' + gesuchID);
            for (var file of files) {
                console.log('File: ' + file.name);
            }

            this.uploadRS.uploadFile(files, selectDokument, gesuchID).then((response) => {
                let returnedDG: TSDokumentGrund = angular.copy(response);
                this.handleUpload(returnedDG);
            });
        } else {
            console.log('No gesuch found to store file ');
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
        this.onRemove({dokumentGrund: dokumentGrund, dokument: dokument});
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


}



