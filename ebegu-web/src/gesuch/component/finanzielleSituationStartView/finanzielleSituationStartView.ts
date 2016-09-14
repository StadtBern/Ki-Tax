import {IComponentOptions, IPromise} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStammdatenStateParams} from '../../gesuch.route';
import TSFinanzielleSituation from '../../../models/TSFinanzielleSituation';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import TSGesuch from '../../../models/TSGesuch';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
let template = require('./finanzielleSituationStartView.html');
require('./finanzielleSituationStartView.less');


export class FinanzielleSituationStartViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FinanzielleSituationStartViewController;
    controllerAs = 'vm';
}

export class FinanzielleSituationStartViewController extends AbstractGesuchViewController {

    allowedRoles: Array<TSRoleUtil>;

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService', 'WizardStepManager'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService,
                wizardStepManager: WizardStepManager) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);

        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
        this.initViewModel();
    }

    private initViewModel() {
        this.gesuchModelManager.initFinanzielleSituation();
        this.wizardStepManager.setCurrentStep(TSWizardStepName.FINANZIELLE_SITUATION);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
    }

    showSteuerveranlagung(): boolean {
        return this.gesuchModelManager.getFamiliensituation().gemeinsameSteuererklaerung === true;
    }

    showSteuererklaerung(): boolean {
        return this.getFinanzielleSituationGS1().steuerveranlagungErhalten === false;
    }

    private save(form: angular.IFormController): IPromise<TSGesuch> {
        if (form.$valid) {
            this.errorService.clearAll();
            return this.gesuchModelManager.updateGesuch();
        }
        return undefined;
    }

    public getFinanzielleSituationGS1(): TSFinanzielleSituation {
        return this.gesuchModelManager.getGesuch().gesuchsteller1.finanzielleSituationContainer.finanzielleSituationJA;
    }

    private getFinanzielleSituationGS2(): TSFinanzielleSituation {
        return this.gesuchModelManager.getGesuch().gesuchsteller2.finanzielleSituationContainer.finanzielleSituationJA;
    }

    private gemeinsameStekClicked(): void {
        // Wenn neu NEIN -> Fragen loeschen
        if (this.gesuchModelManager.getFamiliensituation().gemeinsameSteuererklaerung === false) {
            this.gesuchModelManager.getGesuch().gesuchsteller1.finanzielleSituationContainer = undefined;
            this.gesuchModelManager.getGesuch().gesuchsteller2.finanzielleSituationContainer = undefined;
        } else {
            this.gesuchModelManager.initFinanzielleSituation();
        }
    }

    private steuerveranlagungClicked(): void {
        // Wenn Steuerveranlagung JA -> auch StekErhalten -> JA
        // Wenn zusätzlich noch GemeinsameStek -> Dasselbe auch für GS2
        // Wenn Steuerveranlagung erhalten, muss auch STEK ausgefüllt worden sein
        if (this.getFinanzielleSituationGS1().steuerveranlagungErhalten === true) {
            this.getFinanzielleSituationGS1().steuererklaerungAusgefuellt = true;
            if (this.gesuchModelManager.getFamiliensituation().gemeinsameSteuererklaerung === true) {
                this.getFinanzielleSituationGS2().steuerveranlagungErhalten = true;
                this.getFinanzielleSituationGS2().steuererklaerungAusgefuellt = true;
            }
        } else if (this.getFinanzielleSituationGS1().steuerveranlagungErhalten === false) {
            // Steuerveranlagung neu NEIN -> Fragen loeschen
            this.getFinanzielleSituationGS1().steuererklaerungAusgefuellt = undefined;
            if (this.gesuchModelManager.getFamiliensituation().gemeinsameSteuererklaerung === true) {
                this.getFinanzielleSituationGS2().steuerveranlagungErhalten = false;
                this.getFinanzielleSituationGS2().steuererklaerungAusgefuellt = undefined;
            }
        }
    }

    private steuererklaerungClicked() {
        if (this.gesuchModelManager.getFamiliensituation().gemeinsameSteuererklaerung === true) {
            this.getFinanzielleSituationGS2().steuererklaerungAusgefuellt = this.getFinanzielleSituationGS1().steuererklaerungAusgefuellt;
        }
    }
}
