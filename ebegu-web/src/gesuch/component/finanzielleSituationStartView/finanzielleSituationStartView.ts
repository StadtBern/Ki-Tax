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
import IQService = angular.IQService;
import TSFinanzModel from '../../../models/TSFinanzModel';
let template = require('./finanzielleSituationStartView.html');
require('./finanzielleSituationStartView.less');


export class FinanzielleSituationStartViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FinanzielleSituationStartViewController;
    controllerAs = 'vm';
}

export class FinanzielleSituationStartViewController extends AbstractGesuchViewController<TSFinanzModel> {

    allowedRoles: Array<TSRoleUtil>;

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService',
        'WizardStepManager', '$q'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService,
                wizardStepManager: WizardStepManager, private $q: IQService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);

        this.model = new TSFinanzModel(this.gesuchModelManager.getBasisjahr(), this.gesuchModelManager.isGesuchsteller2Required(), null);
        this.model.copyFinSitDataFromGesuch(this.gesuchModelManager.getGesuch());

        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
        this.initViewModel();
    }

    private initViewModel() {
        this.wizardStepManager.setCurrentStep(TSWizardStepName.FINANZIELLE_SITUATION);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
    }

    showSteuerveranlagung(): boolean {
        return this.model.gemeinsameSteuererklaerung === true;
    }

    showSteuererklaerung(): boolean {
        return this.getFinanzielleSituationGS1().steuerveranlagungErhalten === false;
    }

    private save(form: angular.IFormController): IPromise<TSGesuch> {
        if (form.$valid) {
            this.model.copyFinSitDataToGesuch(this.gesuchModelManager.getGesuch());
            if (!form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when(this.gesuchModelManager.getGesuch());
            }
            this.errorService.clearAll();
            return this.gesuchModelManager.updateGesuch().then((gesuch: TSGesuch) => {
                // Nötig, da nur das ganze Gesuch upgedated wird und die Aeenderng bei der FinSit sonst nicht bemerkt werden
                if (this.gesuchModelManager.getGesuch().isMutation()) {
                    this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.MUTIERT);
                }
                return gesuch;
            });
        }
        return undefined;
    }

    public getFinanzielleSituationGS1(): TSFinanzielleSituation {
        return this.model.finanzielleSituationContainerGS1.finanzielleSituationJA;
    }

    private getFinanzielleSituationGS2(): TSFinanzielleSituation {
        return this.model.finanzielleSituationContainerGS2.finanzielleSituationJA;
    }

    public gemeinsameStekClicked(): void {
        // Wenn neu NEIN -> Fragen loeschen
        if (this.model.gemeinsameSteuererklaerung === false) {
            this.model.finanzielleSituationContainerGS1 = undefined;
            this.model.finanzielleSituationContainerGS2 = undefined;
        } else {
            this.model.initFinSit();
        }
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

    public steuererklaerungClicked() {
        if (this.model.gemeinsameSteuererklaerung === true) {
            this.model.finanzielleSituationContainerGS2.finanzielleSituationJA.steuererklaerungAusgefuellt =
                this.model.finanzielleSituationContainerGS1.finanzielleSituationJA.steuererklaerungAusgefuellt;
        }
    }
}
