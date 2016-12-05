import AbstractGesuchViewController from '../abstractGesuchView';
import {IComponentOptions, IPromise, IQService, IScope} from 'angular';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import ITranslateService = angular.translate.ITranslateService;
import {isFreigegeben} from '../../../models/enums/TSAntragStatus';
import IFormController = angular.IFormController;
import TSGesuch from '../../../models/TSGesuch';
let template = require('./freigabeView.html');
require('./freigabeView.less');


export class FreigabeViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = FreigabeViewController;
    controllerAs = 'vm';
}


export class FreigabeViewController extends AbstractGesuchViewController {

    bestaetigungFreigabequittung: boolean = false;
    bestaetigungFreigabeTagesschule: boolean = false;

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'ErrorService', 'WizardStepManager',
        'DvDialog', '$translate', '$q', '$scope'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private errorService: ErrorService, wizardStepManager: WizardStepManager, private DvDialog: DvDialog,
                private $translate: ITranslateService, private $q: IQService, private $scope: IScope) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.initViewModel();
    }

    private initViewModel(): void {
        this.wizardStepManager.setCurrentStep(TSWizardStepName.FREIGABE);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
    }

    public save(form: IFormController): IPromise<TSGesuch> {
        if (form.$valid) {

        }
        return undefined;
    }

    public isGesuchFreigegeben(): boolean {
        if (this.gesuchModelManager.getGesuch()) {
            return isFreigegeben(this.gesuchModelManager.getGesuch().status);
        }
        return false;
    }

    public openFreigabequittungPDF(): void {

    }

}
