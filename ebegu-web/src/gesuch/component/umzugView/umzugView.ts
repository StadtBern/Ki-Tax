import AbstractGesuchViewController from '../abstractGesuchView';
import {IComponentOptions, IPromise} from 'angular';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import TSAdresse from '../../../models/TSAdresse';
import ITranslateService = angular.translate.ITranslateService;
let template = require('./umzugView.html');
require('./umzugView.less');
let removeDialogTemplate = require('../../dialog/removeDialogTemplate.html');


export class UmzugViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = UmzugViewController;
    controllerAs = 'vm';
}


export class UmzugViewController extends AbstractGesuchViewController {

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'WizardStepManager', 'ErrorService'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                wizardStepManager: WizardStepManager, private errorService: ErrorService) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.initViewModel();
    }

    private initViewModel(): void {
        this.wizardStepManager.setCurrentStep(TSWizardStepName.UMZUG);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
    }

    public save(form: angular.IFormController): IPromise<TSAdresse> {
        if (form.$valid) {
            this.errorService.clearAll();
            // return this.gesuchModelManager.updateUmzug();
        }
        return undefined;
    }

}
