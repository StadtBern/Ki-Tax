import AbstractGesuchViewController from '../abstractGesuchView';
import {IComponentOptions, IPromise} from 'angular';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import TSBetreuung from '../../../models/TSBetreuung';
import TSAbwesenheitContainer from '../../../models/TSAbwesenheitContainer';
import TSKindContainer from '../../../models/TSKindContainer';
import ITranslateService = angular.translate.ITranslateService;
let template = require('./abwesenheitView.html');
require('./abwesenheitView.less');


export class AbwesenheitViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = AbwesenheitViewController;
    controllerAs = 'vm';
}


export class AbwesenheitViewController extends AbstractGesuchViewController {

    abwesenheiten: Array<TSAbwesenheitContainer> = [];

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'WizardStepManager'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                wizardStepManager: WizardStepManager) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.initViewModel();
    }

    private initViewModel(): void {
        this.wizardStepManager.setCurrentStep(TSWizardStepName.ABWESENHEIT);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK);
    }

    public getKinderWithBetreuungList(): Array<TSKindContainer> {
        return this.gesuchModelManager.getKinderWithBetreuungList();
    }



    public save(form: angular.IFormController): IPromise<TSBetreuung> {
        if (form.$valid) {

        }
        return undefined;
    }

    public createAbwesenheit(): void {
        this.abwesenheiten.push(new TSAbwesenheitContainer());
    }

    public getAbwesenheiten(): Array<TSAbwesenheitContainer> {
        return this.abwesenheiten;
    }
}
