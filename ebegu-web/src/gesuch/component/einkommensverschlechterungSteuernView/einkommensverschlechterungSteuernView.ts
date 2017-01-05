import {IComponentOptions, IPromise} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSEinkommensverschlechterung from '../../../models/TSEinkommensverschlechterung';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import TSEinkommensverschlechterungInfo from '../../../models/TSEinkommensverschlechterungInfo';
import TSGesuch from '../../../models/TSGesuch';
import WizardStepManager from '../../service/wizardStepManager';
import {TSRole} from '../../../models/enums/TSRole';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import TSFinanzModel from '../../../models/TSFinanzModel';
import IQService = angular.IQService;
import IScope = angular.IScope;
let template = require('./einkommensverschlechterungSteuernView.html');
require('./einkommensverschlechterungSteuernView.less');


export class EinkommensverschlechterungSteuernViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = EinkommensverschlechterungSteuernViewController;
    controllerAs = 'vm';
}

export class EinkommensverschlechterungSteuernViewController extends AbstractGesuchViewController<TSFinanzModel> {

    allowedRoles: Array<TSRole>;
    initialModel: TSFinanzModel;

    static $inject: string[] = ['GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService',
        'WizardStepManager', '$q', '$scope'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private CONSTANTS: any, private errorService: ErrorService, wizardStepManager: WizardStepManager,
                private $q: IQService, $scope: IScope) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope);
        this.model = new TSFinanzModel(this.gesuchModelManager.getBasisjahr(), this.gesuchModelManager.isGesuchsteller2Required(), null);
        this.model.copyEkvDataFromGesuch(this.gesuchModelManager.getGesuch());
        this.initialModel = angular.copy(this.model);

        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
        this.initViewModel();
    }

    private initViewModel() {
        // Basis Jahr 1 braucht es nur wenn gewünscht
        if (this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus1) {
            this.model.initEinkommensverschlechterungContainer(1, 1);
            this.model.initEinkommensverschlechterungContainer(1, 2);
        }

        // Basis Jahr 2 braucht es nur wenn gewünscht
        if (this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus2) {
            this.model.initEinkommensverschlechterungContainer(2, 1);
            this.model.initEinkommensverschlechterungContainer(2, 2);
        }
    }

    getEinkommensverschlechterungsInfo(): TSEinkommensverschlechterungInfo {
        return this.model.einkommensverschlechterungInfoContainer.einkommensverschlechterungInfoJA;
    }

    showSteuerveranlagung_BjP1(): boolean {
        return this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP1 === true;
    }

    showSteuererklaerung_BjP1(): boolean {
        return this.isSteuerveranlagungErhaltenGS1_Bjp1() === false;
    }


    isSteuerveranlagungErhaltenGS1_Bjp1(): boolean {
        if (this.getEkv_GS1_Bjp1()) {
            return this.getEkv_GS1_Bjp1().steuerveranlagungErhalten;
        } else {
            return false;
        }
    }

    private save(): IPromise<TSGesuch> {
        if (this.isGesuchValid()) {
            if (!this.form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when(this.gesuchModelManager.getGesuch());
            }
            this.removeNotNeededEKV();
            this.errorService.clearAll();
            this.model.copyEkvSitDataToGesuch(this.gesuchModelManager.getGesuch());
            return this.gesuchModelManager.updateGesuch().then((gesuch: TSGesuch) => {
                // Noetig, da nur das ganze Gesuch upgedated wird und die Aenderng bei der FinSit sonst nicht bemerkt werden
                if (this.gesuchModelManager.getGesuch().isMutation() && this.wizardStepManager.getCurrentStep().wizardStepStatus !== TSWizardStepStatus.NOK) {
                    //wenn es NOK wir duerfen es erst im letzten Schritt aendern
                    this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.MUTIERT);
                }
                return gesuch;
            });
        }
        return undefined;
    }

    public getEkv_GS1_Bjp1(): TSEinkommensverschlechterung {
        return this.model.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus1;
    }


    public getEkv_GS2_Bjp1(): TSEinkommensverschlechterung {
        return this.model.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus1;
    }

    private gemeinsameStekClicked_BjP1(): void {
        // Wenn neu NEIN -> Fragen loeschen

        let ekvJaBasisJahrPlus1WasAlreadyEntered = this.model.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus1
            && !this.model.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus1.isNew();
        if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP1 === false && ekvJaBasisJahrPlus1WasAlreadyEntered) {
            // Wenn neu NEIN und schon was eingegeben -> Fragen mal auf false setzen und Status auf nok damit man sicher noch weiter muss!
            this.initSteuerFragen();
            this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.NOK);
        } else if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP1 === false) {
            // Wenn neu NEIN und noch nichts eingegeben -> Fragen loeschen da noch nichts eingegeben worden ist
            this.model.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus1 = undefined;
            this.model.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus1 = undefined;
        } else {
            // Wenn neu JA
            this.initViewModel();  //review @gapa fragen ist das nicht ein change genueber vorher
        }
    }


    /**
     * Es muss ein Wert geschrieben werden, um ekv persisierten zu können
     */
    private initSteuerFragen() {
        let gs1EkvJABasisJahrPlus1 = this.model.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus1;
        if (gs1EkvJABasisJahrPlus1) {
            gs1EkvJABasisJahrPlus1.steuererklaerungAusgefuellt = !gs1EkvJABasisJahrPlus1.steuererklaerungAusgefuellt ? false : gs1EkvJABasisJahrPlus1.steuererklaerungAusgefuellt;
            gs1EkvJABasisJahrPlus1.steuerveranlagungErhalten = !gs1EkvJABasisJahrPlus1.steuerveranlagungErhalten ? false : gs1EkvJABasisJahrPlus1.steuerveranlagungErhalten;
        }
        let gs2EkvJABasisJahrPlus1 = this.model.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus1;
        if (gs2EkvJABasisJahrPlus1) {
            gs2EkvJABasisJahrPlus1.steuererklaerungAusgefuellt = !gs2EkvJABasisJahrPlus1.steuererklaerungAusgefuellt ? false : gs2EkvJABasisJahrPlus1.steuererklaerungAusgefuellt;
            gs2EkvJABasisJahrPlus1.steuerveranlagungErhalten = !gs2EkvJABasisJahrPlus1.steuerveranlagungErhalten ? false : gs2EkvJABasisJahrPlus1.steuerveranlagungErhalten;
        }
    }

    private removeNotNeededEKV(): void {

        if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP1 === false) {
            this.model.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus1 = undefined;
            this.model.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus1 = undefined;
        }
    }

    private steuerveranlagungClicked_BjP1(): void {
        // Wenn Steuerveranlagung JA -> auch StekErhalten -> JA
        // Wenn zusätzlich noch GemeinsameStek -> Dasselbe auch für GS2
        // Wenn Steuerveranlagung erhalten, muss auch STEK ausgefüllt worden sein
        if (this.getEkv_GS1_Bjp1().steuerveranlagungErhalten === true) {
            this.getEkv_GS1_Bjp1().steuererklaerungAusgefuellt = true;
            if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP1 === true) {
                this.getEkv_GS2_Bjp1().steuerveranlagungErhalten = true;
                this.getEkv_GS2_Bjp1().steuererklaerungAusgefuellt = true;
            }
        } else if (this.getEkv_GS1_Bjp1().steuerveranlagungErhalten === false) {
            // Steuerveranlagung neu NEIN -> Fragen loeschen
            this.getEkv_GS1_Bjp1().steuererklaerungAusgefuellt = undefined;
            if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP1 === true) {
                this.getEkv_GS2_Bjp1().steuerveranlagungErhalten = false;
                this.getEkv_GS2_Bjp1().steuererklaerungAusgefuellt = undefined;
            }
        }
    }

    private steuererklaerungClicked_BjP1() {
        if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP1 === true) {
            this.getEkv_GS2_Bjp1().steuererklaerungAusgefuellt = this.getEkv_GS1_Bjp1().steuererklaerungAusgefuellt;
        }
    }


}
