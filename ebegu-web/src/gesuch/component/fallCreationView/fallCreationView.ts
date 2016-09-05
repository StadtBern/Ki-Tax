import {IComponentOptions, IPromise} from 'angular';
import {IStateService} from 'angular-ui-router';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import TSGesuch from '../../../models/TSGesuch';
import ErrorService from '../../../core/errors/service/ErrorService';
import EbeguUtil from '../../../utils/EbeguUtil';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import {INewFallStateParams} from '../../gesuch.route';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
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

    static $inject = ['$state', 'GesuchModelManager', 'BerechnungsManager', 'EbeguUtil', 'ErrorService', '$stateParams', 'WizardStepManager'];
    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager, private ebeguUtil: EbeguUtil,
                private errorService: ErrorService, $stateParams: INewFallStateParams, wizardStepManager: WizardStepManager) {
        super(state, gesuchModelManager, berechnungsManager, wizardStepManager);
        this.createNewParam = $stateParams.createNew;
        this.initViewModel();
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

    public getGesuchsperiodeAsString(gesuchsperiode: TSGesuchsperiode): string {
        return this.ebeguUtil.getGesuchsperiodeAsString(gesuchsperiode);
    }

    /**
     * Calls getGesuchsperiodeAsString with the Gesuchsperiode of the current Gesuch
     * @returns {string}
     */
    public getCurrentGesuchsperiodeAsString(): string {
        return this.ebeguUtil.getGesuchsperiodeAsString(this.gesuchModelManager.getGesuchsperiode());
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
