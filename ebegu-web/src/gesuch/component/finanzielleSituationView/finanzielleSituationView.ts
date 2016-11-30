import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStammdatenStateParams} from '../../gesuch.route';
import TSFinanzielleSituationContainer from '../../../models/TSFinanzielleSituationContainer';
import BerechnungsManager from '../../service/berechnungsManager';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
import ErrorService from '../../../core/errors/service/ErrorService';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import {TSRole} from '../../../models/enums/TSRole';
import TSFinanzModel from '../../../models/TSFinanzModel';
import TSGesuch from '../../../models/TSGesuch';
import IPromise = angular.IPromise;
import IQService = angular.IQService;
import TSFamiliensituation from '../../../models/TSFamiliensituation';
import TSGesuchsteller from '../../../models/TSGesuchsteller';
let template = require('./finanzielleSituationView.html');
require('./finanzielleSituationView.less');


export class FinanzielleSituationViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FinanzielleSituationViewController;
    controllerAs = 'vm';
}

export class FinanzielleSituationViewController extends AbstractGesuchViewController<TSFinanzModel> {

    public showSelbstaendig: boolean;
    allowedRoles: Array<TSRole>;

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService',
        'WizardStepManager', '$q'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService,
                wizardStepManager: WizardStepManager, private $q: IQService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        let parsedNum: number = parseInt($stateParams.gesuchstellerNumber, 10);
        this.model = new TSFinanzModel(this.gesuchModelManager.getBasisjahr(), this.gesuchModelManager.isGesuchsteller2Required(), parsedNum);
        this.model.copyFinSitDataFromGesuch(this.gesuchModelManager.getGesuch());
        this.gesuchModelManager.setGesuchstellerNumber(parsedNum);
        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
        this.initViewModel();
        this.calculate();
    }

    private initViewModel() {

        this.wizardStepManager.setCurrentStep(TSWizardStepName.FINANZIELLE_SITUATION);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        this.showSelbstaendig = this.model.getFiSiConToWorkWith().finanzielleSituationJA.isSelbstaendig();
    }

    public showSelbstaendigClicked() {
        if (!this.showSelbstaendig) {
            this.resetSelbstaendigFields();
        }
    }

    private resetSelbstaendigFields() {
        if (this.model.getFiSiConToWorkWith()) {
            this.model.getFiSiConToWorkWith().finanzielleSituationJA.geschaeftsgewinnBasisjahr = undefined;
            this.model.getFiSiConToWorkWith().finanzielleSituationJA.geschaeftsgewinnBasisjahrMinus1 = undefined;
            this.model.getFiSiConToWorkWith().finanzielleSituationJA.geschaeftsgewinnBasisjahrMinus2 = undefined;
            this.calculate();
        }
    }

    showSteuerveranlagung(): boolean {
        return !this.model.gemeinsameSteuererklaerung || this.model.gemeinsameSteuererklaerung === false;
    }

    showSteuererklaerung(): boolean {
        return this.model.getFiSiConToWorkWith().finanzielleSituationJA.steuerveranlagungErhalten === false;
    }

    public steuerveranlagungClicked(): void {
        // Wenn Steuerveranlagung JA -> auch StekErhalten -> JA
        // Wenn zusätzlich noch GemeinsameStek -> Dasselbe auch für GS2
        // Wenn Steuerveranlagung erhalten, muss auch STEK ausgefüllt worden sein
        if (this.model.finanzielleSituationContainerGS1.finanzielleSituationJA.steuerveranlagungErhalten === true) {
            this.model.finanzielleSituationContainerGS1.finanzielleSituationJA.steuererklaerungAusgefuellt = true;
            if (this.model.gemeinsameSteuererklaerung === true) {
                this.model.finanzielleSituationContainerGS2.finanzielleSituationJA.steuerveranlagungErhalten = true;
                this.model.finanzielleSituationContainerGS2.finanzielleSituationJA.steuererklaerungAusgefuellt = true;
            }
        } else if (this.model.finanzielleSituationContainerGS1.finanzielleSituationJA.steuerveranlagungErhalten === false) {
            // Steuerveranlagung neu NEIN -> Fragen loeschen
            this.model.finanzielleSituationContainerGS1.finanzielleSituationJA.steuererklaerungAusgefuellt = undefined;
            if (this.model.gemeinsameSteuererklaerung === true) {
                this.model.finanzielleSituationContainerGS2.finanzielleSituationJA.steuerveranlagungErhalten = false;
                this.model.finanzielleSituationContainerGS2.finanzielleSituationJA.steuererklaerungAusgefuellt = undefined;
            }
        }
    }

    private save(form: angular.IFormController): IPromise<TSFinanzielleSituationContainer> {
        if (form.$valid) {
            this.model.copyFinSitDataToGesuch(this.gesuchModelManager.getGesuch());
            if (!form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when(this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer);
            }
            this.errorService.clearAll();
            return this.gesuchModelManager.saveFinanzielleSituation();
        }
        return undefined;
    }

    calculate() {
        this.berechnungsManager.calculateFinanzielleSituationTemp(this.model);
    }

    resetForm() {
        this.initViewModel();
    }

    public getModel(): TSFinanzielleSituationContainer {
        return this.model.getFiSiConToWorkWith();
    }

    public getResultate(): TSFinanzielleSituationResultateDTO {
        return this.berechnungsManager.finanzielleSituationResultate;
    }

    /**
     * Mindestens einer aller Felder von Geschaftsgewinn muss ausgefuellt sein. Mit dieser Methode kann man es pruefen.
     * @returns {boolean}
     */
    public isGeschaeftsgewinnRequired(): boolean {
        return !(this.getModel().finanzielleSituationJA.geschaeftsgewinnBasisjahr ||
        this.getModel().finanzielleSituationJA.geschaeftsgewinnBasisjahrMinus1 ||
        this.getModel().finanzielleSituationJA.geschaeftsgewinnBasisjahrMinus2);
    }
}
