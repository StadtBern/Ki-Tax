import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import {IStammdatenStateParams} from '../../gesuch.route';
import TSDokumenteDTO from '../../../models/dto/TSDokumenteDTO';
import {TSDokumentGrundTyp} from '../../../models/enums/TSDokumentGrundTyp';
import TSDokumentGrund from '../../../models/TSDokumentGrund';
import IFormController = angular.IFormController;
import EbeguUtil from '../../../utils/EbeguUtil';
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

    test: any;


    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService', 'Upload', 'EbeguUtil'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, $state: IStateService, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService, private upload: any, ebeguUtil: EbeguUtil) {
        super($state, gesuchModelManager, berechnungsManager);
        this.parsedNum = parseInt($stateParams.gesuchstellerNumber, 10);
        this.calculate();
        this.dokumenteSonst.push(new TSDokumentGrund(TSDokumentGrundTyp.SONSTIGE_NACHWEISE));
        console.log('alskdjf', upload);
    }

    calculate() {
        if (this.gesuchModelManager.gesuch) {
            this.berechnungsManager
                .getDokumente(this.gesuchModelManager.gesuch)
                .then((promiseValue: TSDokumenteDTO) => {
                    this.searchDokumente(promiseValue, this.dokumenteEkv, TSDokumentGrundTyp.EINKOMMENSVERSCHLECHTERUNG);
                    this.searchDokumente(promiseValue, this.dokumenteFinSit, TSDokumentGrundTyp.FINANZIELLESITUATION);
                    this.searchDokumente(promiseValue, this.dokumenteFamSit, TSDokumentGrundTyp.FAMILIENSITUATION);
                    this.searchDokumente(promiseValue, this.dokumenteErwp, TSDokumentGrundTyp.ERWERBSPENSUM);
                    this.searchDokumente(promiseValue, this.dokumenteKinder, TSDokumentGrundTyp.KINDER);
                });
        } else {
            console.log('No gesuch für dokumente');
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


    previousStep() {
        let ekvFuerBasisJahrPlus2 = this.gesuchModelManager.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2
            && this.gesuchModelManager.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2 === true;
        let ekvFuerBasisJahrPlus1 = this.gesuchModelManager.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1
            && this.gesuchModelManager.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1 === true;
        if (ekvFuerBasisJahrPlus2) {
            this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '2'});
        } else if (ekvFuerBasisJahrPlus1) {
            this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '1'});
        } else {
            this.state.go('gesuch.einkommensverschlechterungInfo');
        }
    }

    nextStep() {
        this.state.go('gesuch.verfuegen');
    }

    submit(form: IFormController) {
        if (form.$valid) {

            this.errorService.clearAll();
            this.nextStep();
        }
    }

    addUploadedDokuments(dokumentGrund: any, dokumente: any): void {
        var index = EbeguUtil.getIndexOfElementwithID(dokumentGrund, dokumente);
        console.log(index);

        if (index > -1) {
            dokumente[index] = dokumentGrund;
        }
    }

    addUploadedDokumentsFinSit(dokumentGrund: any): void {
        var index = EbeguUtil.getIndexOfElementwithID(dokumentGrund, this.dokumenteFinSit);
        console.log(index);

        if (index > -1) {
            this.dokumenteFinSit[index] = dokumentGrund;
        }
    }

}
