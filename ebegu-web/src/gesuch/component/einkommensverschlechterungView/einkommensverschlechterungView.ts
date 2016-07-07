import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import {IEinkommensverschlechterungStateParams} from '../../gesuch.route';
import BerechnungsManager from '../../service/berechnungsManager';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
import ErrorService from '../../../core/errors/service/ErrorService';
import TSGesuch from '../../../models/TSGesuch';
import TSEinkommensverschlechterung from '../../../models/TSEinkommensverschlechterung';
import TSEinkommensverschlechterungInfo from '../../../models/TSEinkommensverschlechterungInfo';
import IFormController = angular.IFormController;
let template = require('./einkommensverschlechterungView.html');
require('./einkommensverschlechterungView.less');


export class EinkommensverschlechterungViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = EinkommensverschlechterungViewController;
    controllerAs = 'vm';
}

export class EinkommensverschlechterungViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService'];
    /* @ngInject */
    constructor($stateParams: IEinkommensverschlechterungStateParams, $state: IStateService, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService) {
        super($state, gesuchModelManager, berechnungsManager);
        let parsedGesuchstelllerNum: number = parseInt($stateParams.gesuchstellerNumber, 10);
        let parsedBasisJahrPlusNum: number = parseInt($stateParams.basisjahrPlus, 10);
        this.gesuchModelManager.setGesuchstellerNumber(parsedGesuchstelllerNum);
        this.gesuchModelManager.setBasisJahrPlusNumber(parsedBasisJahrPlusNum);
        this.initViewModel();
        this.calculate();

    }

    private initViewModel() {
        this.gesuchModelManager.initEinkommensverschlechterungContainer(this.gesuchModelManager.getBasisJahrPlusNumber() === 2);
        this.gesuchModelManager.copyEkvGeschaeftsgewinnFromFS();
    }

    getGesuch(): TSGesuch {
        if (!this.gesuchModelManager.gesuch) {
            this.gesuchModelManager.initGesuch(false);
        }
        return this.gesuchModelManager.gesuch;
    }


    showSteuerveranlagung(): boolean {
        return !this.gesuchModelManager.getGemeinsameSteuererklaerungToWorkWith() || this.gesuchModelManager.getGemeinsameSteuererklaerungToWorkWith() === false;
    }

    showSteuererklaerung(): boolean {
        return this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === false;
    }

    showSelbstaendig(): boolean {
        return this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().selbstaendig === true;
    }

    showHintSteuererklaerung(): boolean {
        return this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuererklaerungAusgefuellt === true &&
            this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === false;
    }

    showHintSteuerveranlagung(): boolean {
        return this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === true;
    }

    steuerveranlagungClicked(): void {
        // Wenn Steuerveranlagung JA -> auch StekErhalten -> JA
        if (this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === true) {
            this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuererklaerungAusgefuellt = true;
        } else if (this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === false) {
            // Steuerveranlagung neu NEIN -> Fragen loeschen
            this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuererklaerungAusgefuellt = undefined;
        }
    }

    previousStep() {
        if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 1)) {
            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) {
                // 1 , 1
                if (this.gesuchModelManager.isGesuchsteller2Required()) {
                    this.state.go('gesuch.einkommensverschlechterungSteuern');
                } else {
                    this.state.go('gesuch.einkommensverschlechterungInfo');
                }
            } else { //gesuchsteller ===2

                // 2 , 1
                if (this.gesuchModelManager.isBasisJahr2Required()) {
                    this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '1', basisjahrPlus: '2'});
                } else {
                    this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '1', basisjahrPlus: '1'});
                }

            }

        } else if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 2)) {

            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) {
                // 1 , 2
                this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '1', basisjahrPlus: '1'});
            } else { //gesuchsteller ===2
                // 2 , 2
                this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '2', basisjahrPlus: '1'});
            }
        }
    }

    nextStep() {

        if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 1)) {
            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) {
                // 1 , 1
                if (this.gesuchModelManager.isBasisJahr2Required()) {
                    this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '1', basisjahrPlus: '2'});
                } else if (this.gesuchModelManager.isGesuchsteller2Required()) {
                    this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '2', basisjahrPlus: '1'});
                } else {
                    this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '1'});
                }

            } else { //gesuchsteller ===2

                // 2 , 1
                if (this.gesuchModelManager.isBasisJahr2Required()) {
                    this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '2', basisjahrPlus: '2'});
                } else {
                    this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '1'});
                }

            }

        } else if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 2)) {

            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) {
                // 1 , 2
                if (this.gesuchModelManager.isGesuchsteller2Required()) {
                    this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '2', basisjahrPlus: '1'});
                } else {
                    this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '1'});
                }

            } else { //gesuchsteller ===2
                // 2 , 2
                this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '1'});
            }
        }
    }

    submit(form: IFormController) {
        if (form.$valid) {
            // Speichern ausloesen
            this.errorService.clearAll();
            this.gesuchModelManager.saveEinkommensverschlechterungContainer().then((einkommensverschlechterungResponse: any) => {
                this.nextStep();
            });
        }
    }

    calculate() {
        this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.gesuch, this.gesuchModelManager.getBasisJahrPlusNumber());
    }

    resetForm() {
        this.initViewModel();
    }

    public getEinkommensverschlechterung(): TSEinkommensverschlechterung {
        return this.gesuchModelManager.getEinkommensverschlechterungToWorkWith();
    }

    public getResultate(): TSFinanzielleSituationResultateDTO {
        return this.berechnungsManager.getEinkommensverschlechterungResultate(this.gesuchModelManager.getBasisJahrPlusNumber());
    }

    private getEinkommensverschlechterungsInfo(): TSEinkommensverschlechterungInfo {
        if (this.getGesuch().einkommensverschlechterungInfo == null) {
            this.gesuchModelManager.initEinkommensverschlechterungInfo();
        }
        return this.getGesuch().einkommensverschlechterungInfo;
    }

    // private getResultViewBasisJahrPlus() {
    //     let ekvFuerBasisJahrPlus1 = this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus1 && this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus1 === true;
    //     let ekvFuerBasisJahrPlus2 = this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus2 && this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus2 === true;
    //
    //     if (ekvFuerBasisJahrPlus1 && ekvFuerBasisJahrPlus2) {
    //         // Wenn für beide Jahre eine Einkommensverschlechterung geltent gemacht werden soll, wird zuerst das Resultat für das BasisJahr 1 angezeigt
    //         return 1;
    //     } else if (ekvFuerBasisJahrPlus1) {
    //         // Wenn nur für bjp1 eine Einkommensverschlechterung geltent gemacht werden soll, wird zuerst das Resultat für das BasisJahr 1 angezeigt
    //         return 1;
    //     } else if (ekvFuerBasisJahrPlus2) {
    //         // Wenn nur für bjp2 eine Einkommensverschlechterung geltent gemacht werden soll, wird zuerst das Resultat für das BasisJahr 2 angezeigt
    //         return 2;
    //     }
    // }
}
