import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import {IStammdatenStateParams} from '../../gesuch.route';
import TSFinanzielleSituation from '../../../models/TSFinanzielleSituation';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import IFormController = angular.IFormController;
let template = require('./finanzielleSituationStartView.html');
require('./finanzielleSituationStartView.less');


export class FinanzielleSituationStartViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FinanzielleSituationStartViewController;
    controllerAs = 'vm';
}

export class FinanzielleSituationStartViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, $state: IStateService, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService) {
        super($state, gesuchModelManager, berechnungsManager);

        this.initViewModel();
    }

    private initViewModel() {
        this.gesuchModelManager.initFinanzielleSituation();
    }

    showSteuerveranlagung(): boolean {
        return this.gesuchModelManager.getFamiliensituation().gemeinsameSteuererklaerung === true;
    }

    showSteuererklaerung(): boolean {
        return this.getFinanzielleSituationGS1().steuerveranlagungErhalten === false;
    }

    previousStep() {
        this.state.go('gesuch.kinder');
    }

    nextStep() {
        this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: '1'});
    }

    submit(form: IFormController) {
        if (form.$valid) {
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

    public getFinanzielleSituationGS1(): TSFinanzielleSituation {
        return this.gesuchModelManager.gesuch.gesuchsteller1.finanzielleSituationContainer.finanzielleSituationJA;
    }

    private getFinanzielleSituationGS2(): TSFinanzielleSituation {
        return this.gesuchModelManager.gesuch.gesuchsteller2.finanzielleSituationContainer.finanzielleSituationJA;
    }

    private gemeinsameStekClicked(): void {
        // Wenn neu NEIN -> Fragen loeschen
        if (this.gesuchModelManager.getFamiliensituation().gemeinsameSteuererklaerung === false) {
            this.gesuchModelManager.gesuch.gesuchsteller1.finanzielleSituationContainer = undefined;
            this.gesuchModelManager.gesuch.gesuchsteller2.finanzielleSituationContainer = undefined;
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
