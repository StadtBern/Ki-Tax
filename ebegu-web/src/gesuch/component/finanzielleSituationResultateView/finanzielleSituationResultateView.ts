import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import {IStammdatenStateParams} from '../../gesuch.route';
import TSFinanzielleSituationContainer from '../../../models/TSFinanzielleSituationContainer';
import IFormController = angular.IFormController;
let template = require('./finanzielleSituationResultateView.html');

export class FinanzielleSituationResultateViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FinanzielleSituationResultateViewController;
    controllerAs = 'vm';
}

export class FinanzielleSituationResultateViewController extends AbstractGesuchViewController {

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

    // showSteuererklaerung(): boolean {
    //     return this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationSV.steuerveranlagungErhalten === false;
    // }
    //
    // showSelbstaendig(): boolean {
    //     return this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationSV.selbstaendig === true;
    // }
    //
    // geschaeftsgewinnChanged() {
    //     this.calculate();
    // }

    previousStep() {
        if ((this.gesuchModelManager.gesuchstellerNumber === 2)) {
            this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: 2});
        } else {
            this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: 1});
        }
    }

    nextStep() {
        alert('go to next page');
    }

    submit(form: IFormController) {
        if (form.$valid) {
            // Speichern ausloesen
            this.gesuchModelManager.saveFinanzielleSituation().then((finanzielleSituationResponse: any) => {
                this.nextStep();
            });
        }
    }

    // calculate() {
    //     this.gesuchModelManager.calculateFinanzielleSituation().then((finanzielleSituationResponse: any) => {
    //         this.nextStep();
    //     });
    // }

    resetForm() {
        this.initViewModel();
    }

    public getModel(): TSFinanzielleSituationContainer {
        return this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer;
    }
}
