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
import IScope = angular.IScope;
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
    savedClicked: boolean = false;

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'ErrorService', 'WizardStepManager',
        'DvDialog', '$translate', '$q', '$scope'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private errorService: ErrorService, wizardStepManager: WizardStepManager, private DvDialog: DvDialog,
                private $translate: ITranslateService, private $q: IQService, private $scope: IScope) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.familienstatusValues = getTSFamilienstatusValues();
        this.gesuchstellerKardinalitaetValues = getTSGesuchstellerKardinalitaetValues();
        this.initialFamiliensituation = angular.copy(this.gesuchModelManager.getFamiliensituation());
        this.initViewModel();

        if ($scope) {
            $scope.$watch(() => {
                return this.gesuchModelManager.getFamiliensituation().aenderungPer;
            }, (newValue, oldValue) => {
                if ((newValue !== oldValue) && (!newValue)) {
                    this.resetFamsit();
                }
            });
        }
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
        this.savedClicked = true;
        if (form.$valid && !this.hasEmptyAenderungPer() && !this.hasError()) {
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

    public getFamiliensituationErstgesuch(): TSFamiliensituation {
        return this.gesuchModelManager.getFamiliensituationErstgesuch();
    }

    /**
     * Confirmation is required when the GS2 already exists and the familiensituation changes from 2GS to 1GS. Or when in a Mutation
     * the GS2 is new and will be removed
     * @returns {boolean}
     */
    private isConfirmationRequired(): boolean {
        return ((!this.isMutation() && this.checkChanged2To1GS())) ||
            (this.isMutation() && this.checkChanged2To1GS() && !this.gesuchModelManager.getGesuch().gesuchsteller2.vorgaengerId );
    }

    private checkChanged2To1GS() {
        return this.gesuchModelManager.getGesuch().gesuchsteller2 && this.gesuchModelManager.getGesuch().gesuchsteller2.id
            && this.initialFamiliensituation.hasSecondGesuchsteller()
            && !this.gesuchModelManager.getFamiliensituation().hasSecondGesuchsteller();
    }


    public isMutationAndDateSet(): boolean {
        if (!this.isMutation()) {
            return true;
        } else {
            if (this.getFamiliensituation().aenderungPer !== null && this.getFamiliensituation().aenderungPer !== undefined) {
                return true;
            }
        }
        return false;
    }

    public isEnabled(): boolean {
        if (this.isMutationAndDateSet() && !this.isGesuchStatusVerfuegenVerfuegt()) {
            console.debug('return true');
            return true
        } else {
            console.debug('return false');
            return false;
        }
    }

    public hasEmptyAenderungPer(): boolean {
        if (this.isMutation() && !this.getFamiliensituation().aenderungPer && !this.getFamiliensituationErstgesuch().isSameFamiliensituation(this.getFamiliensituation())) {
            return true;
        }
        return false;
    }

    public resetFamsit() {
        this.getFamiliensituation().isRevertFamiliensituation(this.getFamiliensituationErstgesuch())
    }

    public hasError(): boolean {
        if (this.isMutation() && this.getFamiliensituation().aenderungPer && this.getFamiliensituationErstgesuch().isSameFamiliensituation(this.getFamiliensituation())) {
            return true;
        }
        return false;
    }

    public showError(): boolean {
        return this.hasError() && this.savedClicked;
    }
}
