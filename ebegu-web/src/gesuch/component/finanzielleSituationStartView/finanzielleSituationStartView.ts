import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import {IStammdatenStateParams} from '../../gesuch.route';
import TSFinanzielleSituationContainer from '../../../models/TSFinanzielleSituationContainer';
import IFormController = angular.IFormController;
import TSFinanzielleSituation from '../../../models/TSFinanzielleSituation';
let template = require('./finanzielleSituationStartView.html');

export class FinanzielleSituationStartViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FinanzielleSituationStartViewController;
    controllerAs = 'vm';
}

export class FinanzielleSituationStartViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'CONSTANTS'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, $state: IStateService, gesuchModelManager: GesuchModelManager, private CONSTANTS: any) {
        super($state, gesuchModelManager);
        // let parsedNum: number = parseInt($stateParams.gesuchstellerNumber, 10);
        // this.gesuchModelManager.setGesuchstellerNumber(parsedNum);
        this.initViewModel();
    }

    private initViewModel() {
        this.gesuchModelManager.initFinanzielleSituation();
    }

    showSteuerveranlagung(): boolean {
        return this.gesuchModelManager.familiensituation.gemeinsameSteuererklaerung === true;
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
            this.gesuchModelManager.updateFamiliensituation().then((familiensituationResponse: any) => {
                if (this.showSteuerveranlagung()) {
                    // Die Fragen zur Steuererklaerung wurden eingeblendet, da gemeinsame STEK. Die Daten
                    // muessen auf beiden Gesuchstellern gespeichert werden!
                    this.getFinanzielleSituationGS2().steuerveranlagungErhalten = this.getFinanzielleSituationGS1().steuerveranlagungErhalten;
                    this.getFinanzielleSituationGS2().steuererklaerungAusgefuellt = this.getFinanzielleSituationGS1().steuererklaerungAusgefuellt;
                    this.gesuchModelManager.saveFinanzielleSituation().then((finanzielleSituationResponse: any) => {
                        this.nextStep();
                    });
                } else {
                    this.nextStep();
                }
            });
        }
    }

    resetForm() {
        this.initViewModel();
    }

    public getFinanzielleSituationGS1(): TSFinanzielleSituation {
        return  this.gesuchModelManager.gesuch.gesuchsteller1.finanzielleSituationContainer.finanzielleSituationSV;
    }

    private getFinanzielleSituationGS2(): TSFinanzielleSituation {
        return  this.gesuchModelManager.gesuch.gesuchsteller2.finanzielleSituationContainer.finanzielleSituationSV;
    }
}
