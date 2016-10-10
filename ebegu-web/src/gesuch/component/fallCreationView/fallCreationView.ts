import {IComponentOptions, IPromise} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import TSGesuch from '../../../models/TSGesuch';
import ErrorService from '../../../core/errors/service/ErrorService';
import {INewFallStateParams} from '../../gesuch.route';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import Moment = moment.Moment;
import ITranslateService = angular.translate.ITranslateService;
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
    familiensituationSelected: boolean;
    stammdatenGSSelected: boolean;
    umzugSelected: boolean;
    kindSelected: boolean;
    betreuungSelected: boolean;
    abwesenheitSelected: boolean;
    erwerbspensumSelected: boolean;
    finanzielleSituationSelected: boolean;
    einkommensverschlechterungSelected: boolean;

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'ErrorService', '$stateParams',
        'WizardStepManager', '$translate'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private errorService: ErrorService, private $stateParams: INewFallStateParams, wizardStepManager: WizardStepManager,
                private $translate: ITranslateService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.readCreateNewParam();
        this.initViewModel();
    }

    private readCreateNewParam() {
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

    public getTitle(): string {
        if (this.gesuchModelManager.isErstgesuch()) {
            if (this.gesuchModelManager.isGesuchSaved()) {
                return this.$translate.instant('MENU_ERSTGESUCH_PERIODE', {
                    periode: this.gesuchModelManager.getGesuchsperiode().gesuchsperiodeString
                });
            } else {
                return this.$translate.instant('MENU_ERSTGESUCH');
            }
        } else {
            return this.$translate.instant('ART_DER_MUTATION');
        }
    }

}
