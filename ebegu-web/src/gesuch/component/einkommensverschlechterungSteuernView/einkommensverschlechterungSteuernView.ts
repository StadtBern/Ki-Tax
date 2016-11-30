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
import IQService = angular.IQService;
import TSFinanzModel from '../../../models/TSFinanzModel';
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

    static $inject: string[] = ['GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService', 'WizardStepManager', '$q'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private CONSTANTS: any, private errorService: ErrorService, wizardStepManager: WizardStepManager, private $q: IQService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.model = new TSFinanzModel(this.gesuchModelManager.getBasisjahr(), this.gesuchModelManager.isGesuchsteller2Required(), null);
        this.model.copyEkvDataFromGesuch(this.gesuchModelManager.getGesuch());

        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
        this.initViewModel();
    }

    private initViewModel() {
        // Basis Jahr 1 braucht es immer
        this.model.initEinkommensverschlechterungContainer(1, 1);
        this.model.initEinkommensverschlechterungContainer(1, 2);

        // Basis Jahr 2 braucht nur wenn gewünscht
        if (this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus2) {
            this.model.initEinkommensverschlechterungContainer(2, 1);
            this.model.initEinkommensverschlechterungContainer(2, 2);
        }
    }

    getEinkommensverschlechterungsInfo(): TSEinkommensverschlechterungInfo {
        return this.model.einkommensverschlechterungInfo;
    }

    showSteuerveranlagung_BjP1(): boolean {
        return this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP1 === true;
    }


    showSteuerveranlagung_BjP2(): boolean {
        return this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP2 === true;
    }


    showSteuererklaerung_BjP1(): boolean {
        return this.getEkv_GS1_Bjp1().steuerveranlagungErhalten === false;
    }

    showSteuererklaerung_BjP2(): boolean {
        return this.isSteuerveranlagungErhaltenGS1_Bjp2() === false;
    }

    isSteuerveranlagungErhaltenGS1_Bjp2(): boolean {
        if (this.getEkv_GS1_Bjp2()) {
            return this.getEkv_GS1_Bjp2().steuerveranlagungErhalten;
        } else {
            return false;
        }
    }

    private save(form: angular.IFormController): IPromise<TSGesuch> {
        if (form.$valid) {
            if (!form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when(this.gesuchModelManager.getGesuch());
            }
            this.removeNotNeededEKV();
            this.errorService.clearAll();
            this.model.copyEkvSitDataToGesuch(this.gesuchModelManager.getGesuch());
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

    public getEkv_GS1_Bjp1(): TSEinkommensverschlechterung {
        return this.model.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus1;
    }

    public getEkv_GS1_Bjp2(): TSEinkommensverschlechterung {
        return this.model.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus2;
    }

    public getEkv_GS2_Bjp1(): TSEinkommensverschlechterung {
        return this.model.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus1;
    }

    public getEkv_GS2_Bjp2(): TSEinkommensverschlechterung {
        return this.model.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus2;
    }

    private gemeinsameStekClicked_BjP1(): void {
        // Wenn neu NEIN -> Fragen loeschen
        if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP1 === false) {
            this.model.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus1 = undefined;
            this.model.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus1 = undefined;
        } else {
            this.model.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus1 = new TSEinkommensverschlechterung();
            this.model.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus1 = new TSEinkommensverschlechterung();
        }
    }

    private gemeinsameStekClicked_BjP2(): void {
        // Wenn neu NEIN -> Fragen loeschen
        if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP2 === false) {
            this.model.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus2 = undefined;
            this.model.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus2 = undefined;
        } else {
            this.model.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus2 = new TSEinkommensverschlechterung();
            this.model.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus2 = new TSEinkommensverschlechterung();
        }
    }

    private removeNotNeededEKV(): void {
        // Wenn keine gemeinsame Steuererklärung, können hier die zusätzlichen Fragen noch gelöscht werden
        if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP2 === false) {
            this.model.einkommensverschlechterungContainerGS1.ekvJABasisJahrPlus2 = undefined;
            this.model.einkommensverschlechterungContainerGS2.ekvJABasisJahrPlus2 = undefined;
        }

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

    private steuerveranlagungClicked_BjP2(): void {
        // Wenn Steuerveranlagung JA -> auch StekErhalten -> JA
        // Wenn zusätzlich noch GemeinsameStek -> Dasselbe auch für GS2
        // Wenn Steuerveranlagung erhalten, muss auch STEK ausgefüllt worden sein
        if (this.getEkv_GS1_Bjp2().steuerveranlagungErhalten === true) {
            this.getEkv_GS1_Bjp2().steuererklaerungAusgefuellt = true;
            if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP2 === true) {
                this.getEkv_GS2_Bjp2().steuerveranlagungErhalten = true;
                this.getEkv_GS2_Bjp2().steuererklaerungAusgefuellt = true;
            }
        } else if (this.getEkv_GS1_Bjp2().steuerveranlagungErhalten === false) {
            // Steuerveranlagung neu NEIN -> Fragen loeschen
            this.getEkv_GS1_Bjp2().steuererklaerungAusgefuellt = undefined;
            if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP2 === true) {
                this.getEkv_GS2_Bjp2().steuerveranlagungErhalten = false;
                this.getEkv_GS2_Bjp2().steuererklaerungAusgefuellt = undefined;
            }
        }
    }

    private steuererklaerungClicked_BjP1() {
        if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP1 === true) {
            this.getEkv_GS2_Bjp1().steuererklaerungAusgefuellt = this.getEkv_GS1_Bjp1().steuererklaerungAusgefuellt;
        }
    }

    private steuererklaerungClicked_BjP2() {
        if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP2 === true) {
            this.getEkv_GS2_Bjp2().steuererklaerungAusgefuellt = this.getEkv_GS1_Bjp2().steuererklaerungAusgefuellt;
        }
    }

    showFragen_BjP1(): boolean {
        return this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus1 ||
            this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus2;
    }

    showFragen_BjP2(): boolean {
        return this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus2;
    }
}
