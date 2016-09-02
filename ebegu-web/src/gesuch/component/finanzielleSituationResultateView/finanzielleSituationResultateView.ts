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
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
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

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService', 'WizardStepManager'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, $state: IStateService, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService,
                wizardStepManager: WizardStepManager) {
        super($state, gesuchModelManager, berechnungsManager, wizardStepManager);
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
        this.save(form, (gesuch: any) => {
            if ((this.gesuchModelManager.gesuchstellerNumber === 2)) {
                this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: 2});
            } else {
                this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: 1});
            }
        });
    }

    nextStep(form: IFormController): void {
        this.save(form, (gesuch: any) => {
            this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK).then(() => {
                this.state.go('gesuch.einkommensverschlechterungInfo');
            });
        });
    }

    private save(form: angular.IFormController, navigationFunction: (gesuch: any) => any) {
        if (form.$valid) {
            this.errorService.clearAll();
            if (this.gesuchModelManager.getGesuch().gesuchsteller1) {
                this.gesuchModelManager.setGesuchstellerNumber(1);
                if (this.gesuchModelManager.getGesuch().gesuchsteller2) {
                    this.gesuchModelManager.saveFinanzielleSituation().then(() => {
                        this.gesuchModelManager.setGesuchstellerNumber(2);
                        this.gesuchModelManager.saveFinanzielleSituation().then((navigationFunction));
                    });
                } else {
                    this.gesuchModelManager.saveFinanzielleSituation().then(navigationFunction);
                }
            }
        }
    }

    calculate() {
        this.berechnungsManager.calculateFinanzielleSituation(this.gesuchModelManager.getGesuch());
    }

    resetForm() {
        this.initViewModel();
    }

    public getFinanzielleSituationGS1(): TSFinanzielleSituationContainer {
        if (!this.gesuchsteller1FinSit) {
            if (this.gesuchModelManager.getGesuch().gesuchsteller1) {
                this.gesuchsteller1FinSit = this.gesuchModelManager.getGesuch().gesuchsteller1.finanzielleSituationContainer;
            } else {
                this.gesuchsteller1FinSit = new TSFinanzielleSituationContainer();
            }
        }
        return this.gesuchsteller1FinSit;

    }

    public getFinanzielleSituationGS2(): TSFinanzielleSituationContainer {
        if (!this.gesuchsteller2FinSit) {
            if (this.gesuchModelManager.getGesuch().gesuchsteller2) {
                this.gesuchsteller2FinSit = this.gesuchModelManager.getGesuch().gesuchsteller2.finanzielleSituationContainer;
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
