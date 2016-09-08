import {IComponentOptions, ILogService} from 'angular';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSGesuch from '../../../models/TSGesuch';
import GesuchRS from '../../service/gesuchRS.rest';
import DokumenteRS from '../../service/dokumenteRS.rest';
import {TSDokumentGrundTyp} from '../../../models/enums/TSDokumentGrundTyp';
import TSDokumenteDTO from '../../../models/dto/TSDokumenteDTO';
import TSDokumentGrund from '../../../models/TSDokumentGrund';
import TSDokument from '../../../models/TSDokument';
import TSTempDokument from '../../../models/TSTempDokument';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import {UploadRS} from '../../../core/service/uploadRS.rest';
import WizardStepManager from '../../service/wizardStepManager';
import TSWizardStep from '../../../models/TSWizardStep';
import IFormController = angular.IFormController;
import IPromise = angular.IPromise;
import IQService = angular.IQService;
let template = require('./kommentarView.html');
require('./kommentarView.less');

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

    static $inject: string[] = ['$log', 'GesuchModelManager', 'GesuchRS', 'DokumenteRS', 'DownloadRS', '$q', 'UploadRS', 'WizardStepManager'];
    /* @ngInject */
    constructor(private $log: ILogService, private gesuchModelManager: GesuchModelManager, private gesuchRS: GesuchRS,
                private dokumenteRS: DokumenteRS, private downloadRS: DownloadRS, private $q: IQService,
                private uploadRS: UploadRS, private wizardStepManager: WizardStepManager) {
        if (!this.isGesuchUnsaved()) {
            this.getPapiergesuchFromServer();
        }
    }

    private getPapiergesuchFromServer(): IPromise<TSDokumenteDTO> {

        return this.dokumenteRS.getDokumenteByType(
            this.getGesuch(), TSDokumentGrundTyp.PAPIERGESUCH)
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
                if (this.dokumentePapiergesuch.dokumente[0].dokumentName) {
                    return true;
                }
            }
        }
        return false;
    }

    download() {
        this.getPapiergesuchFromServer().then((promiseValue: any) => {
            if (!this.hasPapiergesuch()) {
                this.$log.error('Kein Papiergesuch für Download vorhanden!');
            } else {
                let newest: TSDokument = this.getNewest(this.dokumentePapiergesuch.dokumente);
                this.downloadRS.getAccessToken(newest.id).then((response) => {
                    let tempDokument: TSTempDokument = angular.copy(response);
                    this.downloadRS.startDownload(tempDokument.accessToken, newest.dokumentName, false);
                });
            }
        });
    }

    private getNewest(dokumente: Array<TSDokument>): TSDokument {
        let newest: TSDokument = dokumente[0];
        for (var i = 0; i < dokumente.length; i++) {
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
                for (var file of files) {
                    console.log('File: ' + file.name);
                }

                this.uploadRS.uploadFile(files, this.dokumentePapiergesuch, gesuchID).then((response) => {
                    this.dokumentePapiergesuch = angular.copy(response);
                });
            }
        });
    }

    isGesuchUnsaved(): boolean {
        return this.getGesuch().isNew();
    }

    public getCurrentWizardStep(): TSWizardStep {
        return this.wizardStepManager.getCurrentStep();
    }

}
