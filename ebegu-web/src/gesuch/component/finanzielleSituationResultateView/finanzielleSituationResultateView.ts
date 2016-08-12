import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import {IStammdatenStateParams} from '../../gesuch.route';
import TSFinanzielleSituationContainer from '../../../models/TSFinanzielleSituationContainer';
import BerechnungsManager from '../../service/berechnungsManager';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
import ErrorService from '../../../core/errors/service/ErrorService';
import IFormController = angular.IFormController;
let template = require('./finanzielleSituationResultateView.html');
require('./finanzielleSituationResultateView.less');

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

    gesuchsteller1FinSit: TSFinanzielleSituationContainer;
    gesuchsteller2FinSit: TSFinanzielleSituationContainer;

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, $state: IStateService, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService) {
        super($state, gesuchModelManager, berechnungsManager);
        this.initViewModel();
        this.calculate();
    }

    private initViewModel() {
        this.gesuchModelManager.initFinanzielleSituation();
    }

    showGemeinsam(): boolean {
        return this.gesuchModelManager.isGesuchsteller2Required() &&
            this.gesuchModelManager.getFamiliensituation().gemeinsameSteuererklaerung === true;
    }

    showGS1(): boolean {
        return !this.showGemeinsam();
    }

    showGS2(): boolean {
        return this.gesuchModelManager.isGesuchsteller2Required() &&
            this.gesuchModelManager.getFamiliensituation().gemeinsameSteuererklaerung === false;
    }

    previousStep(form: IFormController): void {
        if (form.$valid) {
            this.errorService.clearAll();
            this.gesuchModelManager.updateGesuch().then((gesuch: any) => {
                if ((this.gesuchModelManager.gesuchstellerNumber === 2)) {
                    this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: 2});
                } else {
                    this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: 1});
                }
            });
        }
    }

    nextStep(form: IFormController): void {
        if (form.$valid) {
            this.errorService.clearAll();
            this.gesuchModelManager.updateGesuch().then((gesuch: any) => {
                this.state.go('gesuch.einkommensverschlechterungInfo');
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
        if (!this.gesuchsteller1FinSit) {
            if (this.gesuchModelManager.gesuch.gesuchsteller1) {
                this.gesuchsteller1FinSit = this.gesuchModelManager.gesuch.gesuchsteller1.finanzielleSituationContainer;
            } else {
                this.gesuchsteller1FinSit = new TSFinanzielleSituationContainer();
            }
        }
        return this.gesuchsteller1FinSit;

    }

    public getFinanzielleSituationGS2(): TSFinanzielleSituationContainer {
        if (!this.gesuchsteller2FinSit) {
            if (this.gesuchModelManager.gesuch.gesuchsteller2) {
                this.gesuchsteller2FinSit = this.gesuchModelManager.gesuch.gesuchsteller2.finanzielleSituationContainer;
            } else {
                this.gesuchsteller2FinSit = new TSFinanzielleSituationContainer();
            }
        }
        return this.gesuchsteller2FinSit;
    }

    public getResultate(): TSFinanzielleSituationResultateDTO {
        return this.berechnungsManager.finanzielleSituationResultate;
    }
}
