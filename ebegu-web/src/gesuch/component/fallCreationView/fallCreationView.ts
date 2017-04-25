import {IComponentOptions, IPromise, IQService, IScope} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import TSGesuch from '../../../models/TSGesuch';
import ErrorService from '../../../core/errors/service/ErrorService';
import {INewFallStateParams} from '../../gesuch.route';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSAntragTyp} from '../../../models/enums/TSAntragTyp';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import * as moment from 'moment';
import Moment = moment.Moment;
import ITranslateService = angular.translate.ITranslateService;
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
let template = require('./fallCreationView.html');
require('./fallCreationView.less');

export class FallCreationViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FallCreationViewController;
    controllerAs = 'vm';
}

export class FallCreationViewController extends AbstractGesuchViewController<any> {
    private gesuchsperiodeId: string;

    TSRoleUtil: any;

    // showError ist ein Hack damit, die Fehlermeldung fuer die Checkboxes nicht direkt beim Laden der Seite angezeigt wird
    // sondern erst nachdem man auf ein checkbox oder auf speichern geklickt hat
    showError: boolean = false;
    private activeGesuchsperiodenList: Array<TSGesuchsperiode>;

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'ErrorService', '$stateParams',
        'WizardStepManager', '$translate', '$q', '$scope', 'AuthServiceRS', 'GesuchsperiodeRS'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private errorService: ErrorService, private $stateParams: INewFallStateParams, wizardStepManager: WizardStepManager,
                private $translate: ITranslateService, private $q: IQService, $scope: IScope, private authServiceRS: AuthServiceRS,
                private gesuchsperiodeRS: GesuchsperiodeRS) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.GESUCH_ERSTELLEN);
        this.TSRoleUtil = TSRoleUtil;
    }

    $onInit() {
        this.readStateParams();
        this.initViewModel();
    }

    private readStateParams() {
        if (this.$stateParams.gesuchsperiodeId && this.$stateParams.gesuchsperiodeId !== '') {
            this.gesuchsperiodeId = this.$stateParams.gesuchsperiodeId;
        }
    }

    public setShowError(showError: boolean): void {
        this.showError = showError;
    }

    private initViewModel(): void {
        //gesuch should already have been initialized in resolve function
        if (this.gesuchsperiodeId === null || this.gesuchsperiodeId === undefined || this.gesuchsperiodeId === '') {
            if (this.gesuchModelManager.getGesuchsperiode()) {
                this.gesuchsperiodeId = this.gesuchModelManager.getGesuchsperiode().id;
            }
        }

        this.gesuchsperiodeRS.getAllActiveGesuchsperioden().then((response: TSGesuchsperiode[]) => {
            this.activeGesuchsperiodenList = angular.copy(response);
        });
    }

    save(): IPromise<TSGesuch> {
        this.showError = true;
        if (this.isGesuchValid()) {
            if (!this.form.$dirty && !this.gesuchModelManager.getGesuch().isNew()) {
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
        return this.activeGesuchsperiodenList;
    }

    public setSelectedGesuchsperiode(): void {
        let gesuchsperiodeList = this.getAllActiveGesuchsperioden();
        for (let i: number = 0; i < gesuchsperiodeList.length; i++) {
            if (gesuchsperiodeList[i].id === this.gesuchsperiodeId) {
                this.getGesuchModel().gesuchsperiode = gesuchsperiodeList[i];
            }
        }
    }

    public isGesuchsperiodeActive(): boolean {
        if (this.gesuchModelManager.getGesuchsperiode()) {
            return this.gesuchModelManager.getGesuchsperiode().active;
        } else {
            return true;
        }
    }

    public getTitle(): string {
        if (this.gesuchModelManager.isErstgesuch()) {
            if (this.gesuchModelManager.isGesuchSaved() && this.gesuchModelManager.getGesuchsperiode()) {
                return this.$translate.instant('KITAX_ERSTGESUCH_PERIODE', {
                    periode: this.gesuchModelManager.getGesuchsperiode().gesuchsperiodeString
                });
            } else {
                return this.$translate.instant('KITAX_ERSTGESUCH');
            }
        } else {
            return this.$translate.instant('ART_DER_MUTATION');
        }
    }

    public getNextButtonText(): string {
        if (this.authServiceRS.isOneOfRoles(this.TSRoleUtil.getGesuchstellerOnlyRoles())) {
            return this.$translate.instant('WEITER_ONLY_UPPER');
        }
        return this.$translate.instant('WEITER_UPPER');
    }
}
