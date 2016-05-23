import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import {IStammdatenStateParams} from '../../gesuch.route';
import TSFinanzielleSituationContainer from '../../../models/TSFinanzielleSituationContainer';
import IFormController = angular.IFormController;
import BerechnungsManager from '../../service/berechnungsManager';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
let template = require('./finanzielleSituationResultateView.html');

export class FinanzielleSituationResultateViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FinanzielleSituationResultateViewController;
    controllerAs = 'vm';
}

/**
 * Controller fuer die Finanzielle Situation
 */
export class FinanzielleSituationResultateViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, $state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager, private CONSTANTS: any) {
        super($state, gesuchModelManager, berechnungsManager);
        this.initViewModel();
        this.calculate();
    }

    private initViewModel() {
        this.gesuchModelManager.initFinanzielleSituation();
    }

    showGemeinsam(): boolean {
        return this.gesuchModelManager.isGesuchsteller2Required() &&
            this.gesuchModelManager.familiensituation.gemeinsameSteuererklaerung === true;
    }

    showGS1(): boolean {
        return !this.showGemeinsam();
    }

    showGS2(): boolean {
        return this.gesuchModelManager.isGesuchsteller2Required() &&
            this.gesuchModelManager.familiensituation.gemeinsameSteuererklaerung === false;
    }

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
            this.gesuchModelManager.updateGesuch().then((gesuch: any) => {
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

    public getFinanzielleSituationGS1(): TSFinanzielleSituationContainer {
        return this.gesuchModelManager.gesuch.gesuchsteller1.finanzielleSituationContainer;
    }

    public getFinanzielleSituationGS2(): TSFinanzielleSituationContainer {
        return this.gesuchModelManager.gesuch.gesuchsteller2.finanzielleSituationContainer;
    }

    public getResultate(): TSFinanzielleSituationResultateDTO {
        return this.berechnungsManager.finanzielleSituationResultate;
    }
}
