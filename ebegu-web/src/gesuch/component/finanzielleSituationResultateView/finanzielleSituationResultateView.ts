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
import TSFinanzModel from '../../../models/TSFinanzModel';
import IQService = angular.IQService;
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
export class FinanzielleSituationResultateViewController extends AbstractGesuchViewController<TSFinanzModel> {

    private initialModel: TSFinanzModel;

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService',
        'WizardStepManager', '$q'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService,
                wizardStepManager: WizardStepManager, private $q: IQService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);

        this.model = new TSFinanzModel(this.gesuchModelManager.getBasisjahr(), this.gesuchModelManager.isGesuchsteller2Required(), null);
        this.model.copyFinSitDataFromGesuch(this.gesuchModelManager.getGesuch());
        this.initialModel = angular.copy(this.model);

        this.calculate();
    }

    showGemeinsam(): boolean {
        return this.model.isGesuchsteller2Required() &&
            this.model.gemeinsameSteuererklaerung === true;
    }

    showGS1(): boolean {
        return !this.showGemeinsam();
    }

    showGS2(): boolean {
        return this.model.isGesuchsteller2Required() &&
            this.model.gemeinsameSteuererklaerung === false;
    }

    private save(form: angular.IFormController): IPromise<void> {
        if (form.$valid) {
            this.model.copyFinSitDataToGesuch(this.gesuchModelManager.getGesuch());
            if (!form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                // Update wizardStepStatus also if the form is empty and not dirty
                return this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK);
            }
            this.errorService.clearAll();
            if (this.gesuchModelManager.getGesuch().gesuchsteller1) {
                this.gesuchModelManager.setGesuchstellerNumber(1);
                if (this.gesuchModelManager.getGesuch().gesuchsteller2) {
                    return this.gesuchModelManager.saveFinanzielleSituation().then(() => {
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
        this.berechnungsManager.calculateFinanzielleSituationTemp(this.model);
    }
    //init weg

    public getFinanzielleSituationGS1(): TSFinanzielleSituationContainer {
        return this.model.finanzielleSituationContainerGS1;

    }

    public getFinanzielleSituationGS2(): TSFinanzielleSituationContainer {
        return this.model.finanzielleSituationContainerGS2;
    }

    public getResultate(): TSFinanzielleSituationResultateDTO {
        return this.berechnungsManager.finanzielleSituationResultate;
    }
}
