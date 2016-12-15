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
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSAntragStatus, isAnyStatusOfVerfuegt} from '../../../models/enums/TSAntragStatus';
import IFormController = angular.IFormController;
import IPromise = angular.IPromise;
import IQService = angular.IQService;
import ICacheFactoryService = angular.ICacheFactoryService;
import GlobalCacheService from '../../service/globalCacheService';
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

    static $inject: string[] = ['$log', 'GesuchModelManager', 'GesuchRS', 'DokumenteRS', 'DownloadRS', '$q', 'UploadRS',
        'WizardStepManager', 'GlobalCacheService'];
    /* @ngInject */
    constructor(private $log: ILogService, private gesuchModelManager: GesuchModelManager, private gesuchRS: GesuchRS,
                private dokumenteRS: DokumenteRS, private downloadRS: DownloadRS, private $q: IQService,
                private uploadRS: UploadRS, private wizardStepManager: WizardStepManager, private globalCacheService: GlobalCacheService) {

        if (!this.isGesuchUnsaved()) {
            this.getPapiergesuchFromServer();
        }
    }

    private getPapiergesuchFromServer(): IPromise<TSDokumenteDTO> {

        return this.dokumenteRS.getDokumenteByTypeCached(
            this.getGesuch(), TSDokumentGrundTyp.PAPIERGESUCH, this.globalCacheService.getCache())
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
        this.getPapiergesuchFromServer().then((promiseValue: any) => {
            if (!this.hasPapiergesuch()) {
                this.$log.error('Kein Papiergesuch für Download vorhanden!');
            } else {
                let newest: TSDokument = this.getNewest(this.dokumentePapiergesuch.dokumente);
                this.downloadRS.getAccessTokenDokument(newest.id).then((response) => {
                    let tempDokument: TSDownloadFile = angular.copy(response);
                    this.downloadRS.startDownload(tempDokument.accessToken, newest.filename, false);
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
                    this.globalCacheService.getCache().removeAll();
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

    public isGesuchReadonly(): boolean {
        return this.gesuchModelManager.isGesuchReadonly();
    }

    /**
     * StepsComment sind fuer alle Steps disabled wenn das Gesuch VERFUEGEN oder VERFUEGT ist. Fuer den Step VERFUEGEN ist es nur im
     * Status VERFUEGT DISABLED
     * @returns {boolean}
     */
    public isStepCommentDisabled(): boolean {
        return (this.wizardStepManager.getCurrentStepName() !== TSWizardStepName.VERFUEGEN
            && this.gesuchModelManager.isGesuchReadonly())
            || (this.wizardStepManager.getCurrentStepName() === TSWizardStepName.VERFUEGEN
            && this.gesuchModelManager.getGesuch() && isAnyStatusOfVerfuegt(this.gesuchModelManager.getGesuch().status));
    }

}
