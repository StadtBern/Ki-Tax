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
import IPromise = angular.IPromise;
let template = require('./finanzielleSituationView.html');
require('./finanzielleSituationView.less');


export class FinanzielleSituationViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FinanzielleSituationViewController;
    controllerAs = 'vm';
}

export class FinanzielleSituationViewController extends AbstractGesuchViewController {

    public showSelbstaendig: boolean;
    allowedRoles: Array<TSRole>;

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService', 'WizardStepManager'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService,
                wizardStepManager: WizardStepManager) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        let parsedNum: number = parseInt($stateParams.gesuchstellerNumber, 10);
        this.gesuchModelManager.setGesuchstellerNumber(parsedNum);
        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
        this.initViewModel();
        this.calculate();
    }

    private initViewModel() {
        this.gesuchModelManager.initFinanzielleSituation();
        this.wizardStepManager.setCurrentStep(TSWizardStepName.FINANZIELLE_SITUATION);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        this.showSelbstaendig = this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationJA.isSelbstaendig();
    }

    public showSelbstaendigClicked() {
        if (!this.showSelbstaendig) {
            this.resetSelbstaendigFields();
        }
    }

    private resetSelbstaendigFields() {
        if (this.gesuchModelManager.getStammdatenToWorkWith() && this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer) {
            this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationJA.geschaeftsgewinnBasisjahr = undefined;
            this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationJA.geschaeftsgewinnBasisjahrMinus1 = undefined;
            this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationJA.geschaeftsgewinnBasisjahrMinus2 = undefined;
            this.calculate();
        }
    }

    showSteuerveranlagung(): boolean {
        return !this.gesuchModelManager.getFamiliensituation().gemeinsameSteuererklaerung || this.gesuchModelManager.getFamiliensituation().gemeinsameSteuererklaerung === false;
    }

    showSteuererklaerung(): boolean {
        return this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationJA.steuerveranlagungErhalten === false;
    }

    private steuerveranlagungClicked(): void {
        // Wenn Steuerveranlagung JA -> auch StekErhalten -> JA
        if (this.getModel().finanzielleSituationJA.steuerveranlagungErhalten === true) {
            this.getModel().finanzielleSituationJA.steuererklaerungAusgefuellt = true;
        } else if (this.getModel().finanzielleSituationJA.steuerveranlagungErhalten === false) {
            // Steuerveranlagung neu NEIN -> Fragen loeschen
            this.getModel().finanzielleSituationJA.steuererklaerungAusgefuellt = undefined;
        }
    }

    private save(form: angular.IFormController): IPromise<TSFinanzielleSituationContainer> {
        if (form.$valid) {
            this.errorService.clearAll();
            return this.gesuchModelManager.saveFinanzielleSituation();
        }
        return undefined;
    }

    calculate() {
        this.berechnungsManager.calculateFinanzielleSituation(this.gesuchModelManager.getGesuch());
    }

    resetForm() {
        this.initViewModel();
    }

    public getModel(): TSFinanzielleSituationContainer {
        return this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer;
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
