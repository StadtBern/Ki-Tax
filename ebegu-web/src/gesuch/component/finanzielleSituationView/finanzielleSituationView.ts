import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import {IStammdatenStateParams} from '../../gesuch.route';
import TSFinanzielleSituationContainer from '../../../models/TSFinanzielleSituationContainer';
import IFormController = angular.IFormController;
import BerechnungsManager from '../../service/berechnungsManager';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
let template = require('./finanzielleSituationView.html');

export class FinanzielleSituationViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FinanzielleSituationViewController;
    controllerAs = 'vm';
}

export class FinanzielleSituationViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, $state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager, private CONSTANTS: any) {
        super($state, gesuchModelManager, berechnungsManager);
        let parsedNum: number = parseInt($stateParams.gesuchstellerNumber, 10);
        this.gesuchModelManager.setGesuchstellerNumber(parsedNum);
        this.initViewModel();
        this.calculate();
    }

    private initViewModel() {
        this.gesuchModelManager.initFinanzielleSituation();
    }

    showSteuerveranlagung(): boolean {
        return !this.gesuchModelManager.familiensituation.gemeinsameSteuererklaerung || this.gesuchModelManager.familiensituation.gemeinsameSteuererklaerung === false;
    }

    showSteuererklaerung(): boolean {
        return this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationSV.steuerveranlagungErhalten === false;
    }

    showSelbstaendig(): boolean {
        return this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationSV.selbstaendig === true;
    }

    private steuerveranlagungClicked(): void {
        // Wenn Steuerveranlagung JA -> auch StekErhalten -> JA
        if (this.getModel().finanzielleSituationSV.steuerveranlagungErhalten === true) {
            this.getModel().finanzielleSituationSV.steuererklaerungAusgefuellt = true;
        } else if (this.getModel().finanzielleSituationSV.steuerveranlagungErhalten === false) {
            // Steuerveranlagung neu NEIN -> Fragen loeschen
            this.getModel().finanzielleSituationSV.steuererklaerungAusgefuellt = undefined;
        }
    }

    previousStep() {
        if ((this.gesuchModelManager.getGesuchstellerNumber() === 2)) {
            this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: 1});
        } else if ((this.gesuchModelManager.gesuchstellerNumber === 1) && this.gesuchModelManager.isGesuchsteller2Required()) {
            this.state.go('gesuch.finanzielleSituationStart');
        } else {
            this.state.go('gesuch.kinder');
        }
    }

    nextStep() {
        if ((this.gesuchModelManager.getGesuchstellerNumber() === 1) && this.gesuchModelManager.isGesuchsteller2Required()) {
            this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: '2'});
        } else {
            this.state.go('gesuch.finanzielleSituationResultate');
        }
    }

    submit(form: IFormController) {
        if (form.$valid) {
            // Speichern ausloesen
            this.gesuchModelManager.saveFinanzielleSituation().then((finanzielleSituationResponse: any) => {
                this.nextStep();
            });
        }
    }

    calculate() {
        this.berechnungsManager.calculateFinanzielleSituation(this.gesuchModelManager.gesuch);
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
}
