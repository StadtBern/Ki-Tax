import {IComponentOptions, ILogService} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import {IStammdatenStateParams} from '../../gesuch.route';
import TSDokumenteDTO from '../../../models/dto/TSDokumenteDTO';
import {TSDokumentGrundTyp} from '../../../models/enums/TSDokumentGrundTyp';
import TSDokumentGrund from '../../../models/TSDokumentGrund';
import EbeguUtil from '../../../utils/EbeguUtil';
import TSDokument from '../../../models/TSDokument';
import DokumenteRS from '../../service/dokumenteRS.rest';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import GlobalCacheService from '../../service/globalCacheService';
import ICacheFactoryService = angular.ICacheFactoryService;
let template = require('./dokumenteView.html');
require('./dokumenteView.less');

export class DokumenteViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = DokumenteViewController;
    controllerAs = 'vm';
}

/**
 * Controller fuer den Dokumenten Upload
 */
export class DokumenteViewController extends AbstractGesuchViewController {
    parsedNum: number;
    dokumenteEkv: TSDokumentGrund[] = [];
    dokumenteFinSit: TSDokumentGrund[] = [];
    dokumenteFamSit: TSDokumentGrund[] = [];
    dokumenteErwp: TSDokumentGrund[] = [];
    dokumenteKinder: TSDokumentGrund[] = [];
    dokumenteSonst: TSDokumentGrund[] = [];
    dokumentePapiergesuch: TSDokumentGrund[] = [];

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService',
        'DokumenteRS', '$log', 'WizardStepManager', 'EbeguUtil', 'GlobalCacheService'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService,
                private dokumenteRS: DokumenteRS, private $log: ILogService, wizardStepManager: WizardStepManager,
                private ebeguUtil: EbeguUtil, private globalCacheService: GlobalCacheService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.parsedNum = parseInt($stateParams.gesuchstellerNumber, 10);
        this.wizardStepManager.setCurrentStep(TSWizardStepName.DOKUMENTE);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        this.calculate();
    }

    calculate() {
        if (this.gesuchModelManager.getGesuch()) {
            this.berechnungsManager
                .getDokumente(this.gesuchModelManager.getGesuch())
                .then((promiseValue: TSDokumenteDTO) => {
                    this.searchDokumente(promiseValue, this.dokumenteEkv, TSDokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG);
                    this.searchDokumente(promiseValue, this.dokumenteFinSit, TSDokumentGrundTyp.FINANZIELLESITUATION);
                    this.searchDokumente(promiseValue, this.dokumenteFamSit, TSDokumentGrundTyp.FAMILIENSITUATION);
                    this.searchDokumente(promiseValue, this.dokumenteErwp, TSDokumentGrundTyp.ERWERBSPENSUM);
                    this.searchDokumente(promiseValue, this.dokumenteKinder, TSDokumentGrundTyp.KINDER);
                    this.searchDokumente(promiseValue, this.dokumenteSonst, TSDokumentGrundTyp.SONSTIGE_NACHWEISE);
                    this.searchDokumente(promiseValue, this.dokumentePapiergesuch, TSDokumentGrundTyp.PAPIERGESUCH);
                });
        } else {
            this.$log.debug('No gesuch für dokumente');
        }
    }

    private searchDokumente(alleDokumente: TSDokumenteDTO, dokumenteForType: TSDokumentGrund[], dokumentGrundTyp: TSDokumentGrundTyp) {

        let dokumentGruende: Array<TSDokumentGrund> = alleDokumente.dokumentGruende;
        for (var i = 0; i < dokumentGruende.length; i++) {
            var tsDokument: TSDokumentGrund = dokumentGruende[i];
            if (tsDokument.dokumentGrundTyp === dokumentGrundTyp) {
                dokumenteForType.push(tsDokument);
            }
        }
    }

    addUploadedDokuments(dokumentGrund: any, dokumente: TSDokumentGrund[]): void {
        this.$log.debug('addUploadedDokuments called');
        var index = EbeguUtil.getIndexOfElementwithID(dokumentGrund, dokumente);

        if (index > -1) {
            this.$log.debug('add dokument to dokumentList');
            dokumente[index] = dokumentGrund;

            // Clear cached Papiergesuch on add...
            if (dokumentGrund.dokumentGrundTyp === TSDokumentGrundTyp.PAPIERGESUCH) {
                this.globalCacheService.getCache().removeAll();
            }
        }
        this.ebeguUtil.handleSmarttablesUpdateBug(dokumente);
    }


    removeDokument(dokumentGrund: TSDokumentGrund, dokument: TSDokument, dokumente: TSDokumentGrund[]) {

        var index = EbeguUtil.getIndexOfElementwithID(dokument, dokumentGrund.dokumente);

        if (index > -1) {
            this.$log.debug('add dokument to dokumentList');
            dokumentGrund.dokumente.splice(index, 1);
        }

        this.dokumenteRS.updateDokumentGrund(dokumentGrund).then((response) => {

            let returnedDG: TSDokumentGrund = angular.copy(response);

            if (returnedDG) {
                // replace existing object in table with returned if returned not null
                var index = EbeguUtil.getIndexOfElementwithID(returnedDG, dokumente);
                if (index > -1) {
                    this.$log.debug('update dokumentGrund in dokumentList');
                    dokumente[index] = dokumentGrund;

                    // Clear cached Papiergesuch on remove...
                    if (dokumentGrund.dokumentGrundTyp === TSDokumentGrundTyp.PAPIERGESUCH) {
                        this.globalCacheService.getCache().removeAll();
                    }
                }
            } else {
                // delete object in table with sended if returned is null
                var index = EbeguUtil.getIndexOfElementwithID(dokumentGrund, dokumente);
                if (index > -1) {
                    this.$log.debug('remove dokumentGrund in dokumentList');
                    dokumente.splice(index, 1);
                }
            }
            this.wizardStepManager.findStepsFromGesuch(this.gesuchModelManager.getGesuch().id);
        });

        this.ebeguUtil.handleSmarttablesUpdateBug(dokumente);
    }


}
