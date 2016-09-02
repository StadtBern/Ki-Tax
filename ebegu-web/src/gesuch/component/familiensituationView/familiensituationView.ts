
import AbstractGesuchViewController from '../abstractGesuchView';
import {IComponentOptions, IFormController} from 'angular';
import {IStateService} from 'angular-ui-router';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSFamiliensituation from '../../../models/TSFamiliensituation';
import './familiensituationView.less';
import {TSFamilienstatus, getTSFamilienstatusValues} from '../../../models/enums/TSFamilienstatus';
import {
    TSGesuchstellerKardinalitaet,
    getTSGesuchstellerKardinalitaetValues
} from '../../../models/enums/TSGesuchstellerKardinalitaet';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import {TSRole} from '../../../models/enums/TSRole';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import ITranslateService = angular.translate.ITranslateService;
let template = require('./familiensituationView.html');
require('./familiensituationView.less');
let removeDialogTemplate = require('../../dialog/removeDialogTemplate.html');


export class FamiliensituationViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = FamiliensituationViewController;
    controllerAs = 'vm';
}


export class FamiliensituationViewController extends AbstractGesuchViewController {
    familienstatusValues: Array<TSFamilienstatus>;
    gesuchstellerKardinalitaetValues: Array<TSGesuchstellerKardinalitaet>;
    allowedRoles: Array<TSRole>;
    initialFamiliensituation: TSFamiliensituation;

    static $inject = ['$state', 'GesuchModelManager', 'BerechnungsManager', 'ErrorService', 'WizardStepManager',
                      'DvDialog', '$translate'];
    /* @ngInject */
    constructor($state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private errorService: ErrorService, wizardStepManager: WizardStepManager, private DvDialog: DvDialog,
                private $translate: ITranslateService) {

        super($state, gesuchModelManager, berechnungsManager, wizardStepManager);
        this.familienstatusValues = getTSFamilienstatusValues();
        this.gesuchstellerKardinalitaetValues = getTSGesuchstellerKardinalitaetValues();
        this.initialFamiliensituation = angular.copy(this.gesuchModelManager.getFamiliensituation());
        this.initViewModel();
    }

    private initViewModel(): void {
        this.gesuchModelManager.initFamiliensituation();
        this.wizardStepManager.setCurrentStep(TSWizardStepName.FAMILIENSITUATION);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
    }

    previousStep(form: IFormController): void {
        this.confirmAndSave(form, (response: any) => {
            this.state.go('gesuch.fallcreation');
        });
    }

    nextStep(form: IFormController): void {
        this.confirmAndSave(form, (response: any) => {
            this.state.go('gesuch.stammdaten');
        });
    }

    private confirmAndSave(form: angular.IFormController, navigationFunction: (gesuch: any) => any) {
        if (this.isConfirmationRequired()) {
            let descriptionText: any = this.$translate.instant('FAMILIENSITUATION_WARNING_BESCHREIIBUNG', {
                gsfullname: this.gesuchModelManager.getGesuch().gesuchsteller2 ? this.gesuchModelManager.getGesuch().gesuchsteller2.getFullName() : ''
            });
            this.DvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
                title: 'FAMILIENSITUATION_WARNING',
                deleteText: descriptionText
            }).then(() => {   //User confirmed changes
                this.save(form, navigationFunction);
            });
        } else {
            this.save(form, navigationFunction);
        }
    }

    private save(form: angular.IFormController, navigationFunction: (gesuch: any) => any) {
        if (form.$valid) {
            this.errorService.clearAll();
            this.gesuchModelManager.updateFamiliensituation().then(navigationFunction);
        }
    }

    showGesuchstellerKardinalitaet(): boolean {
        return this.getFamiliensituation().familienstatus === TSFamilienstatus.ALLEINERZIEHEND
            || this.getFamiliensituation().familienstatus === TSFamilienstatus.WENIGER_FUENF_JAHRE;
    }

    public getFamiliensituation(): TSFamiliensituation {
        return this.gesuchModelManager.getFamiliensituation();
    }

    /**
     * Confirmation is required when the GS2 already exists and the familiensituation changes from 2GS to 2GS
     * @returns {boolean}
     */
    private isConfirmationRequired(): boolean {
        return (this.gesuchModelManager.getGesuch().gesuchsteller2 && this.gesuchModelManager.getGesuch().gesuchsteller2.id
                && this.initialFamiliensituation.hasSecondGesuchsteller()
                && !this.gesuchModelManager.getFamiliensituation().hasSecondGesuchsteller());
    }
}
