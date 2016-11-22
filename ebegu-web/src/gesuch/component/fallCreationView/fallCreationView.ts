import {IComponentOptions, IPromise} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import TSGesuch from '../../../models/TSGesuch';
import ErrorService from '../../../core/errors/service/ErrorService';
import {INewFallStateParams} from '../../gesuch.route';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSAntragTyp} from '../../../models/enums/TSAntragTyp';
import Moment = moment.Moment;
import ITranslateService = angular.translate.ITranslateService;
import IQService = angular.IQService;
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
    private createMutation: boolean = false;

    // showError ist ein Hack damit, die Fehlermeldung fuer die Checkboxes nicht direkt beim Laden der Seite angezeigt wird
    // sondern erst nachdem man auf ein checkbox oder auf speichern geklickt hat
    showError: boolean = false;

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'ErrorService', '$stateParams',
        'WizardStepManager', '$translate', '$q'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private errorService: ErrorService, private $stateParams: INewFallStateParams, wizardStepManager: WizardStepManager,
                private $translate: ITranslateService, private $q: IQService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.readCreateNewParam();
        this.readCreateMutation();
        this.initViewModel();
    }

    private readCreateNewParam() {
        if (this.$stateParams.createNew === 'true') {
            this.createNewParam = true;
        }
    }

    private readCreateMutation() {
        if (this.$stateParams.createMutation === 'true') {
            this.createMutation = true;
        }
    }

    public setShowError(showError: boolean): void {
        this.showError = showError;
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

    save(form: angular.IFormController): IPromise<TSGesuch> {
        this.showError = true;
        if (form.$valid) {
            if (!form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when(this.gesuchModelManager.getGesuch());
            }
            this.errorService.clearAll();
            if (this.gesuchModelManager.getGesuch().typ === TSAntragTyp.MUTATION && this.gesuchModelManager.getGesuch().isNew()) {
                this.berechnungsManager.clear();
                return this.gesuchModelManager.saveMutation();
            } else {
                return this.gesuchModelManager.saveGesuchAndFall();
            }
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
