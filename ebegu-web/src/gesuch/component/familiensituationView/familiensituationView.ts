import AbstractGesuchViewController from '../abstractGesuchView';
import {IComponentOptions, IPromise} from 'angular';
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
import IQService = angular.IQService;
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

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'ErrorService', 'WizardStepManager',
        'DvDialog', '$translate', '$q'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private errorService: ErrorService, wizardStepManager: WizardStepManager, private DvDialog: DvDialog,
                private $translate: ITranslateService, private $q: IQService) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager);
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

    public confirmAndSave(form: angular.IFormController): IPromise<TSFamiliensituation> {
        if (this.isConfirmationRequired()) {
            let descriptionText: any = this.$translate.instant('FAMILIENSITUATION_WARNING_BESCHREIBUNG', {
                gsfullname: this.gesuchModelManager.getGesuch().gesuchsteller2 ? this.gesuchModelManager.getGesuch().gesuchsteller2.getFullName() : ''
            });
            return this.DvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
                title: 'FAMILIENSITUATION_WARNING',
                deleteText: descriptionText
            }).then(() => {   //User confirmed changes
                return this.save(form);
            });
        } else {
            return this.save(form);
        }
    }

    private save(form: angular.IFormController): IPromise<TSFamiliensituation> {
        if (form.$valid) {
            if (!form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when(this.gesuchModelManager.getFamiliensituation());
            }
            this.errorService.clearAll();
            return this.gesuchModelManager.updateFamiliensituation();
        }
        return undefined;
    }

    showGesuchstellerKardinalitaet(): boolean {
        if (this.getFamiliensituation()) {
            return this.getFamiliensituation().familienstatus === TSFamilienstatus.ALLEINERZIEHEND
                || this.getFamiliensituation().familienstatus === TSFamilienstatus.WENIGER_FUENF_JAHRE;
        }
        return false;
    }

    public getFamiliensituation(): TSFamiliensituation {
        return this.gesuchModelManager.getFamiliensituation();
    }

    /**
     * Confirmation is required when the GS2 already exists and the familiensituation changes from 2GS to 1GS. Or when in a Mutation
     * the GS2 is new and will be removed
     * @returns {boolean}
     */
    private isConfirmationRequired(): boolean {
        return (
            !this.isMutation()
            && this.gesuchModelManager.getGesuch().gesuchsteller2 && this.gesuchModelManager.getGesuch().gesuchsteller2.id
            && this.initialFamiliensituation.hasSecondGesuchsteller()
            && !this.gesuchModelManager.getFamiliensituation().hasSecondGesuchsteller())
            || (
            this.isMutation()
            && this.gesuchModelManager.getGesuch().gesuchsteller2 && this.gesuchModelManager.getGesuch().gesuchsteller2.id
            && !this.gesuchModelManager.getGesuch().gesuchsteller2.vorgaengerId
            && this.initialFamiliensituation.hasSecondGesuchsteller()
            && !this.gesuchModelManager.getFamiliensituation().hasSecondGesuchsteller());
    }

    public isMutation(): boolean {
        if (this.gesuchModelManager.getGesuch()) {
            return this.gesuchModelManager.getGesuch().isMutation();
        }
        return false;
    }
}
