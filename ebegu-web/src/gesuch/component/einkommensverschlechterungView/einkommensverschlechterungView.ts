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
    public geschaeftsgewinnBasisjahrMinus1: number;
    public geschaeftsgewinnBasisjahrMinus2: number;
    allowedRoles: Array<TSRole>;
    public initialModel: TSFinanzModel;

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService', '$log',
        'WizardStepManager', '$q'];

    /* @ngInject */
    constructor($stateParams: IEinkommensverschlechterungStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService, private $log: ILogService,
                wizardStepManager: WizardStepManager, private $q: IQService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
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

        //brauchen wir hier das init wirklich nicht mehr? was ist bei mutation etc
        this.getGeschaeftsgewinnFromFS();

        this.showSelbstaendig = this.model.getFiSiConToWorkWith().finanzielleSituationJA.isSelbstaendig()
            || (this.model.getEkvToWorkWith().geschaeftsgewinnBasisjahr !== null
            && this.model.getEkvToWorkWith().geschaeftsgewinnBasisjahr !== undefined);

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

    showSteuerveranlagung(): boolean {
        return !this.model.getGemeinsameSteuererklaerungToWorkWith() || this.model.getGemeinsameSteuererklaerungToWorkWith() === false;
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

    private save(form: angular.IFormController): IPromise<TSEinkommensverschlechterungContainer> {
        if (form.$valid) {
            if (!form.$dirty) {
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

    public getGeschaeftsgewinnFromFS(): void {
        if (!this.model.getFiSiConToWorkWith()
            || !this.model.getFiSiConToWorkWith().finanzielleSituationJA) {
            // TODO: Wenn die finanzielleSituation noch nicht existiert haben wir ein Problem
            this.$log.debug('Fehler: FinSit muss existieren');
            return;
        }

        let fs: TSFinanzielleSituation = this.model.getFiSiConToWorkWith().finanzielleSituationJA;
        if (this.model.getBasisJahrPlus() === 2) {
            //basisjahr Plus 2
            this.geschaeftsgewinnBasisjahrMinus1 = this.model.getEkvContToWorkWith().ekvJABasisJahrPlus1.geschaeftsgewinnBasisjahr;
            this.geschaeftsgewinnBasisjahrMinus2 = fs.geschaeftsgewinnBasisjahr;
        } else {
            this.geschaeftsgewinnBasisjahrMinus1 = fs.geschaeftsgewinnBasisjahr;
            this.geschaeftsgewinnBasisjahrMinus2 = fs.geschaeftsgewinnBasisjahrMinus1;
        }
    }

}
