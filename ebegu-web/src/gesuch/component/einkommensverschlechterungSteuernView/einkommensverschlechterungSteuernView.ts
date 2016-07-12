import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import {IStammdatenStateParams} from '../../gesuch.route';
import TSEinkommensverschlechterung from '../../../models/TSEinkommensverschlechterung';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import TSEinkommensverschlechterungInfo from '../../../models/TSEinkommensverschlechterungInfo';
import TSGesuch from '../../../models/TSGesuch';
import IFormController = angular.IFormController;
let template = require('./einkommensverschlechterungSteuernView.html');
require('./einkommensverschlechterungSteuernView.less');


export class EinkommensverschlechterungSteuernViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = EinkommensverschlechterungSteuernViewController;
    controllerAs = 'vm';
}

export class EinkommensverschlechterungSteuernViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, $state: IStateService, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService) {
        super($state, gesuchModelManager, berechnungsManager);

        this.initViewModel();
    }

    private initViewModel() {
        // Basis Jahr 1 braucht es immer
        this.gesuchModelManager.initEinkommensverschlechterungContainer(1, 1);
        this.gesuchModelManager.initEinkommensverschlechterungContainer(1, 2);

        // Basis Jahr 2 braucht nur wenn gewünscht
        if (this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus2) {
            this.gesuchModelManager.initEinkommensverschlechterungContainer(2, 1);
            this.gesuchModelManager.initEinkommensverschlechterungContainer(2, 2);
        }
    }

    getGesuch(): TSGesuch {
        if (!this.gesuchModelManager.gesuch) {
            this.gesuchModelManager.initGesuch(false);
        }
        return this.gesuchModelManager.gesuch;
    }

    getEinkommensverschlechterungsInfo(): TSEinkommensverschlechterungInfo {
        if (this.getGesuch().einkommensverschlechterungInfo == null) {
            this.gesuchModelManager.initEinkommensverschlechterungInfo();
        }
        return this.getGesuch().einkommensverschlechterungInfo;
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

    previousStep() {
        this.state.go('gesuch.einkommensverschlechterungInfo');
    }

    nextStep() {
        this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '1', basisjahrPlus: '1'});
    }

    submit(form: IFormController) {
        if (form.$valid) {
            this.removeNotNeededEKV();
            // Speichern ausloesen
            this.errorService.clearAll();
            this.gesuchModelManager.updateGesuch().then((gesuch: any) => {
                this.nextStep();
            });
        }
    }

    resetForm() {
        this.initViewModel();
    }

    public getEkv_GS1_Bjp1(): TSEinkommensverschlechterung {
        return this.gesuchModelManager.gesuch.gesuchsteller1.einkommensverschlechterungContainer.ekvJABasisJahrPlus1;
    }

    public getEkv_GS1_Bjp2(): TSEinkommensverschlechterung {
        return this.gesuchModelManager.gesuch.gesuchsteller1.einkommensverschlechterungContainer.ekvJABasisJahrPlus2;
    }

    public getEkv_GS2_Bjp1(): TSEinkommensverschlechterung {
        return this.gesuchModelManager.gesuch.gesuchsteller2.einkommensverschlechterungContainer.ekvJABasisJahrPlus1;
    }

    public getEkv_GS2_Bjp2(): TSEinkommensverschlechterung {
        return this.gesuchModelManager.gesuch.gesuchsteller2.einkommensverschlechterungContainer.ekvJABasisJahrPlus2;
    }

    private gemeinsameStekClicked_BjP1(): void {
        // Wenn neu NEIN -> Fragen loeschen
        if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP1 === false) {
            this.gesuchModelManager.gesuch.gesuchsteller1.einkommensverschlechterungContainer.ekvJABasisJahrPlus1 = undefined;
            this.gesuchModelManager.gesuch.gesuchsteller2.einkommensverschlechterungContainer.ekvJABasisJahrPlus1 = undefined;
        } else {
            this.gesuchModelManager.gesuch.gesuchsteller1.einkommensverschlechterungContainer.ekvJABasisJahrPlus1 = new TSEinkommensverschlechterung();
            this.gesuchModelManager.gesuch.gesuchsteller2.einkommensverschlechterungContainer.ekvJABasisJahrPlus1 = new TSEinkommensverschlechterung();
        }
    }

    private gemeinsameStekClicked_BjP2(): void {
        // Wenn neu NEIN -> Fragen loeschen
        if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP2 === false) {
            this.gesuchModelManager.gesuch.gesuchsteller1.einkommensverschlechterungContainer.ekvJABasisJahrPlus2 = undefined;
            this.gesuchModelManager.gesuch.gesuchsteller2.einkommensverschlechterungContainer.ekvJABasisJahrPlus2 = undefined;
        } else {
            this.gesuchModelManager.gesuch.gesuchsteller1.einkommensverschlechterungContainer.ekvJABasisJahrPlus2 = new TSEinkommensverschlechterung();
            this.gesuchModelManager.gesuch.gesuchsteller2.einkommensverschlechterungContainer.ekvJABasisJahrPlus2 = new TSEinkommensverschlechterung();
        }
    }

    private removeNotNeededEKV(): void {
        // Wenn keine gemeinsame Steuererklärung, können hier die zusätzlichen Fragen noch gelöscht werden
        if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP2 === false) {
            this.gesuchModelManager.gesuch.gesuchsteller1.einkommensverschlechterungContainer.ekvJABasisJahrPlus2 = undefined;
            this.gesuchModelManager.gesuch.gesuchsteller2.einkommensverschlechterungContainer.ekvJABasisJahrPlus2 = undefined;
        }

        if (this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP1 === false) {
            this.gesuchModelManager.gesuch.gesuchsteller1.einkommensverschlechterungContainer.ekvJABasisJahrPlus1 = undefined;
            this.gesuchModelManager.gesuch.gesuchsteller2.einkommensverschlechterungContainer.ekvJABasisJahrPlus1 = undefined;
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
