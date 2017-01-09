import {IComponentOptions, ILogService, IPromise, IQService} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IEinkommensverschlechterungStateParams} from '../../gesuch.route';
import BerechnungsManager from '../../service/berechnungsManager';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
import ErrorService from '../../../core/errors/service/ErrorService';
import TSEinkommensverschlechterung from '../../../models/TSEinkommensverschlechterung';
import TSFinanzielleSituation from '../../../models/TSFinanzielleSituation';
import WizardStepManager from '../../service/wizardStepManager';
import TSEinkommensverschlechterungContainer from '../../../models/TSEinkommensverschlechterungContainer';
import {TSRole} from '../../../models/enums/TSRole';
import TSFinanzModel from '../../../models/TSFinanzModel';
import IScope = angular.IScope;
import ITranslateService = angular.translate.ITranslateService;
let template = require('./einkommensverschlechterungView.html');
require('./einkommensverschlechterungView.less');


export class EinkommensverschlechterungViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = EinkommensverschlechterungViewController;
    controllerAs = 'vm';
}

export class EinkommensverschlechterungViewController extends AbstractGesuchViewController<TSFinanzModel> {

    public showSelbstaendig: boolean;
    public showSelbstaendigGS: boolean;
    public geschaeftsgewinnBasisjahrMinus1: number;
    public geschaeftsgewinnBasisjahrMinus2: number;
    public geschaeftsgewinnBasisjahrMinus1GS: number;
    public geschaeftsgewinnBasisjahrMinus2GS: number;
    allowedRoles: Array<TSRole>;
    public initialModel: TSFinanzModel;

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService', '$log',
        'WizardStepManager', '$q', '$scope', '$translate'];

    /* @ngInject */
    constructor($stateParams: IEinkommensverschlechterungStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService, private $log: ILogService,
                wizardStepManager: WizardStepManager, private $q: IQService, $scope: IScope, private $translate: ITranslateService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope);
        let parsedGesuchstelllerNum: number = parseInt($stateParams.gesuchstellerNumber, 10);
        let parsedBasisJahrPlusNum: number = parseInt($stateParams.basisjahrPlus, 10);
        this.gesuchModelManager.setGesuchstellerNumber(parsedGesuchstelllerNum);
        this.gesuchModelManager.setBasisJahrPlusNumber(parsedBasisJahrPlusNum);
        this.model = new TSFinanzModel(this.gesuchModelManager.getBasisjahr(), this.gesuchModelManager.isGesuchsteller2Required(), parsedGesuchstelllerNum, parsedBasisJahrPlusNum);
        this.model.copyEkvDataFromGesuch(this.gesuchModelManager.getGesuch());
        this.model.copyFinSitDataFromGesuch(this.gesuchModelManager.getGesuch());
        this.model.initEinkommensverschlechterungContainer(parsedBasisJahrPlusNum, parsedGesuchstelllerNum);
        this.initialModel = angular.copy(this.model);
        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
        this.initViewModel();
        this.calculate();

    }

    private initViewModel() {
        this.initGeschaeftsgewinnFromFS();
        this.showSelbstaendig = this.model.getFiSiConToWorkWith().finanzielleSituationJA.isSelbstaendig()
            || (this.model.getEkvToWorkWith().geschaeftsgewinnBasisjahr !== null
            && this.model.getEkvToWorkWith().geschaeftsgewinnBasisjahr !== undefined);
        if (this.model.getFiSiConToWorkWith().finanzielleSituationGS && this.model.getEkvToWorkWith_GS()) {
            this.showSelbstaendigGS = this.model.getFiSiConToWorkWith().finanzielleSituationGS.isSelbstaendig()
                || (this.model.getEkvToWorkWith_GS().geschaeftsgewinnBasisjahr !== null
                && this.model.getEkvToWorkWith_GS().geschaeftsgewinnBasisjahr !== undefined);
        }
    }

    public showSelbstaendigClicked() {
        if (!this.showSelbstaendig) {
            this.resetSelbstaendigFields();
        }
    }

    private resetSelbstaendigFields() {
        if (this.model.getEkvToWorkWith().geschaeftsgewinnBasisjahr) {
            this.model.getEkvToWorkWith().geschaeftsgewinnBasisjahr = undefined;
            this.calculate();
        }
    }

    /**
     *  Wenn z.B. in der Periode 2016/2017 eine Einkommensverschlechterung für 2017 geltend gemacht wird,
     *  ist es unmöglich, dass die Steuerveranlagung und Steuererklärung für 2017 schon dem Gesuchsteller vorliegt
     */
    showSteuerveranlagung(): boolean {
        return (this.model.getBasisJahrPlus() === 1) &&
            (!this.model.getGemeinsameSteuererklaerungToWorkWith() || this.model.getGemeinsameSteuererklaerungToWorkWith() === false);
    }

    showSteuererklaerung(): boolean {
        return this.model.getEkvToWorkWith().steuerveranlagungErhalten === false;
    }

    showHintSteuererklaerung(): boolean {
        return this.model.getEkvToWorkWith().steuererklaerungAusgefuellt === true &&
            this.model.getEkvToWorkWith().steuerveranlagungErhalten === false;
    }

    showHintSteuerveranlagung(): boolean {
        return this.model.getEkvToWorkWith().steuerveranlagungErhalten === true;
    }

    steuerveranlagungClicked(): void {
        // Wenn Steuerveranlagung JA -> auch StekErhalten -> JA
        if (this.model.getEkvToWorkWith().steuerveranlagungErhalten === true) {
            this.model.getEkvToWorkWith().steuererklaerungAusgefuellt = true;
        } else if (this.model.getEkvToWorkWith().steuerveranlagungErhalten === false) {
            // Steuerveranlagung neu NEIN -> Fragen loeschen
            this.model.getEkvToWorkWith().steuererklaerungAusgefuellt = undefined;
        }
    }

    private save(): IPromise<TSEinkommensverschlechterungContainer> {
        if (this.isGesuchValid()) {
            if (!this.form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when(this.model.getEkvContToWorkWith());
            }
            this.errorService.clearAll();
            this.model.copyEkvSitDataToGesuch(this.gesuchModelManager.getGesuch());
            return this.gesuchModelManager.saveEinkommensverschlechterungContainer();
        }
        return undefined;
    }

    calculate() {
        this.berechnungsManager.calculateEinkommensverschlechterungTemp(this.model, this.model.getBasisJahrPlus());
    }

    public getEinkommensverschlechterung(): TSEinkommensverschlechterung {
        return this.model.getEkvToWorkWith();
    }

    public getResultate(): TSFinanzielleSituationResultateDTO {
        return this.berechnungsManager.getEinkommensverschlechterungResultate(this.model.getBasisJahrPlus());
    }

    public initGeschaeftsgewinnFromFS(): void {
        if (!this.model.getFiSiConToWorkWith()
            || !this.model.getFiSiConToWorkWith().finanzielleSituationJA) {
            // TODO: Wenn die finanzielleSituation noch nicht existiert haben wir ein Problem
            this.$log.debug('Fehler: FinSit muss existieren');
            return;
        }

        let fs: TSFinanzielleSituation = this.model.getFiSiConToWorkWith().finanzielleSituationJA;
        let fsGS: TSFinanzielleSituation = this.model.getFiSiConToWorkWith().finanzielleSituationGS;
        if (this.model.getBasisJahrPlus() === 2) {
            //basisjahr Plus 2
            if (this.model.einkommensverschlechterungInfoContainer.einkommensverschlechterungInfoJA.ekvFuerBasisJahrPlus1) {
                let einkommensverschlJABasisjahrPlus1 = this.model.getEkvContToWorkWith().ekvJABasisJahrPlus1;
                this.geschaeftsgewinnBasisjahrMinus1 = einkommensverschlJABasisjahrPlus1 ? einkommensverschlJABasisjahrPlus1.geschaeftsgewinnBasisjahr : undefined;
                let einkommensverschlGSBasisjahrPlus1 = this.model.getEkvContToWorkWith().ekvGSBasisJahrPlus1;
                this.geschaeftsgewinnBasisjahrMinus1GS = einkommensverschlGSBasisjahrPlus1 ? einkommensverschlGSBasisjahrPlus1.geschaeftsgewinnBasisjahr : undefined;
            } else {
                let einkommensverschlGS = this.model.getEkvToWorkWith_GS();
                this.geschaeftsgewinnBasisjahrMinus1GS = einkommensverschlGS ? einkommensverschlGS.geschaeftsgewinnBasisjahrMinus1 : undefined;
            }

            this.geschaeftsgewinnBasisjahrMinus2 = fs.geschaeftsgewinnBasisjahr;
            this.geschaeftsgewinnBasisjahrMinus2GS = fsGS ? fsGS.geschaeftsgewinnBasisjahr : undefined;
        } else {
            this.geschaeftsgewinnBasisjahrMinus1 = fs.geschaeftsgewinnBasisjahr;
            this.geschaeftsgewinnBasisjahrMinus2 = fs.geschaeftsgewinnBasisjahrMinus1;
            this.geschaeftsgewinnBasisjahrMinus1GS = fsGS ? fsGS.geschaeftsgewinnBasisjahr : undefined;
            this.geschaeftsgewinnBasisjahrMinus2GS = fsGS ? fsGS.geschaeftsgewinnBasisjahrMinus1 : undefined;
        }
    }

    public enableGeschaeftsgewinnBasisjahrMinus1(): boolean {
        return this.model.getBasisJahrPlus() === 2 && !this.model.einkommensverschlechterungInfoContainer.einkommensverschlechterungInfoJA.ekvFuerBasisJahrPlus1;
    }

    public getTextSelbstaendigKorrektur() {
        if (this.showSelbstaendigGS === true && this.model.getEkvToWorkWith_GS()) {
            let gew1 = this.model.getEkvToWorkWith_GS().geschaeftsgewinnBasisjahr;
            if (gew1) {
                let basisjahr = this.gesuchModelManager.getBasisjahrPlus(this.model.getBasisJahrPlus());
                return this.$translate.instant('JA_KORREKTUR_SELBSTAENDIG_EKV',
                    {basisjahr: basisjahr, gewinn1: gew1});
            }
        }
        return this.$translate.instant('LABEL_KEINE_ANGABE');
    }
}
