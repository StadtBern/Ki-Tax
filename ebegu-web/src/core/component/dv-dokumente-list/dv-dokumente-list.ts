import {IComponentOptions} from 'angular';
import TSDokumentGrund from '../../../models/TSDokumentGrund';
import {UploadRS} from '../../service/uploadRS.rest';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import EbeguUtil from '../../../utils/EbeguUtil';
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
        onUploadDone: '&'

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

    static $inject: any[] = ['UploadRS', 'GesuchModelManager', 'EbeguUtil'];
    /* @ngInject */
    constructor(private uploadRS: UploadRS, private gesuchModelManager: GesuchModelManager, ebeguUtil: EbeguUtil) {

    }

    $onInit() {

    }

    uploadAnhaenge(files: any[], selectDokument: TSDokumentGrund) {


//        console.log('Uploading file:', file.name);
        console.log('**********');
        console.log(this.dokumente);

        if (this.gesuchModelManager.gesuch) {
            let gesuchID = this.gesuchModelManager.gesuch.id;
            //          console.log('Uploading file: ' + file.name + ' on gesuch ' + gesuchID);
            this.uploadRS.uploadFile(files, selectDokument, gesuchID).then((response) => {

                let returnedDG: TSDokumentGrund = angular.copy(response);

                this.handleUpload(returnedDG);

            });
        } else {
            //console.log('No gesuch found to store file: ', file.name);
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



}



