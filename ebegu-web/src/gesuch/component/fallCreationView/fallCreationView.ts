import {IComponentOptions, IPromise} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import TSGesuch from '../../../models/TSGesuch';
import ErrorService from '../../../core/errors/service/ErrorService';
import EbeguUtil from '../../../utils/EbeguUtil';
import {INewFallStateParams} from '../../gesuch.route';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import Moment = moment.Moment;
let template = require('./fallCreationView.html');
require('./fallCreationView.less');

export class FallCreationViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FallCreationViewController;
    controllerAs = 'vm';
}

export class FallCreationViewController extends AbstractGesuchViewController {
    private gesuchsperiodeId: string;
    private createNewParam: boolean = false;

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'EbeguUtil', 'ErrorService', '$stateParams', 'WizardStepManager'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager, private ebeguUtil: EbeguUtil,
                private errorService: ErrorService, private $stateParams: INewFallStateParams, wizardStepManager: WizardStepManager) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.getCreateNewParam();
        this.initViewModel();
    }

    private getCreateNewParam() {
        if (this.$stateParams.createNew === 'true') {
            this.createNewParam = true;
        }
    }

    private initViewModel(): void {
        this.gesuchModelManager.initGesuch(this.createNewParam);
        this.wizardStepManager.setCurrentStep(TSWizardStepName.GESUCH_ERSTELLEN);
        if (this.gesuchModelManager.getGesuchsperiode()) {
            this.gesuchsperiodeId = this.gesuchModelManager.getGesuchsperiode().id;
        }
        if (this.gesuchModelManager.getAllActiveGesuchsperioden() || this.gesuchModelManager.getAllActiveGesuchsperioden().length <= 0) {
            this.gesuchModelManager.updateActiveGesuchsperiodenList();
        }
    }

    save(form: angular.IFormController): IPromise<any> {
        if (form.$valid) {
            this.errorService.clearAll();
            return this.gesuchModelManager.saveGesuchAndFall();
        }
        return undefined;
    }

    public getGesuchModel(): TSGesuch {
        return this.gesuchModelManager.getGesuch();
    }

    public getAllActiveGesuchsperioden() {
        return this.gesuchModelManager.getAllActiveGesuchsperioden();
    }

    public setSelectedGesuchsperiode(): void {
        let gesuchsperiodeList = this.getAllActiveGesuchsperioden();
        for (let i: number = 0; i < gesuchsperiodeList.length; i++) {
            if (gesuchsperiodeList[i].id === this.gesuchsperiodeId) {
                this.getGesuchModel().gesuchsperiode = gesuchsperiodeList[i];
            }
        }
    }

}
