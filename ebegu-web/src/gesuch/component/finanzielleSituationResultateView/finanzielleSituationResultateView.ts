import {IComponentOptions, IPromise} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStammdatenStateParams} from '../../gesuch.route';
import TSFinanzielleSituationContainer from '../../../models/TSFinanzielleSituationContainer';
import BerechnungsManager from '../../service/berechnungsManager';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
import ErrorService from '../../../core/errors/service/ErrorService';
import WizardStepManager from '../../service/wizardStepManager';
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

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService', 'WizardStepManager'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService,
                wizardStepManager: WizardStepManager) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
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

    private save(form: angular.IFormController): IPromise<void> {
        if (form.$valid) {
            this.errorService.clearAll();
            if (this.gesuchModelManager.getGesuch().gesuchsteller1) {
                this.gesuchModelManager.setGesuchstellerNumber(1);
                if (this.gesuchModelManager.getGesuch().gesuchsteller2) {
                    this.gesuchModelManager.saveFinanzielleSituation().then(() => {
                        this.gesuchModelManager.setGesuchstellerNumber(2);
                        return this.gesuchModelManager.saveFinanzielleSituation().then(() => {
                            return this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK);
                        });
                    });
                } else {
                    return this.gesuchModelManager.saveFinanzielleSituation().then(() => {
                        return this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK);
                    });
                }
            }
        }
        return undefined;
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
