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
import TSFamiliensituationContainer from '../../../models/TSFamiliensituationContainer';
import FamiliensituationRS from '../../service/familiensituationRS.rest';
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


export class FamiliensituationViewController extends AbstractGesuchViewController<TSFamiliensituationContainer> {
    familienstatusValues: Array<TSFamilienstatus>;
    gesuchstellerKardinalitaetValues: Array<TSGesuchstellerKardinalitaet>;
    allowedRoles: Array<TSRole>;
    initialFamiliensituation: TSFamiliensituation;
    savedClicked: boolean = false;

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'ErrorService', 'WizardStepManager',
        'DvDialog', '$translate', '$q', '$scope', 'FamiliensituationRS'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private errorService: ErrorService, wizardStepManager: WizardStepManager, private DvDialog: DvDialog,
                private $translate: ITranslateService, private $q: IQService, $scope: IScope,
                private familiensituationRS: FamiliensituationRS) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.FAMILIENSITUATION);
        this.gesuchModelManager.initFamiliensituation();
        this.model = angular.copy(this.gesuchModelManager.getGesuch().familiensituationContainer);
        this.initialFamiliensituation = angular.copy(this.gesuchModelManager.getFamiliensituation());
        this.familienstatusValues = getTSFamilienstatusValues();
        this.gesuchstellerKardinalitaetValues = getTSGesuchstellerKardinalitaetValues();

        this.initViewModel();

    }

    private initViewModel(): void {
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
    }


    public confirmAndSave(): IPromise<TSFamiliensituationContainer> {
        this.savedClicked = true;
        if (this.isGesuchValid() && !this.hasEmptyAenderungPer() && !this.hasError()) {
            if (!this.form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when(this.gesuchModelManager.getGesuch().familiensituationContainer);
            }

            if (this.isConfirmationRequired()) {
                let descriptionText: any = this.$translate.instant('FAMILIENSITUATION_WARNING_BESCHREIBUNG', {
                    gsfullname: this.gesuchModelManager.getGesuch().gesuchsteller2
                        ? this.gesuchModelManager.getGesuch().gesuchsteller2.extractFullName() : ''
                });
                return this.DvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
                    title: 'FAMILIENSITUATION_WARNING',
                    deleteText: descriptionText
                }).then(() => {   //User confirmed changes
                    return this.save();
                });
            } else {
                return this.save();
            }

        }
        return undefined;
    }

    private save(): IPromise<TSFamiliensituationContainer> {
        this.errorService.clearAll();
        return this.familiensituationRS.saveFamiliensituation(this.model, this.gesuchModelManager.getGesuch().id).then((familienContainerResponse: any) => {
            this.model = familienContainerResponse;
            this.gesuchModelManager.getGesuch().familiensituationContainer = familienContainerResponse;
            // Gesuchsteller may changed...
            return this.gesuchModelManager.reloadGesuch().then((response: any) => {
                return this.model;
            });
        });
    }

    showGesuchstellerKardinalitaet(): boolean {
        if (this.getFamiliensituation()) {
            return this.getFamiliensituation().familienstatus === TSFamilienstatus.ALLEINERZIEHEND
                || this.getFamiliensituation().familienstatus === TSFamilienstatus.WENIGER_FUENF_JAHRE;
        }
        return false;
    }

    public getFamiliensituation(): TSFamiliensituation {
        return this.model.familiensituationJA;
    }

    public getFamiliensituationErstgesuch(): TSFamiliensituation {
        return this.model.familiensituationErstgesuch;
    }

    /**
     * Confirmation is required when the GS2 already exists and the familiensituation changes from 2GS to 1GS. Or when in a Mutation
     * the GS2 is new and will be removed
     * @returns {boolean}
     */
    private isConfirmationRequired(): boolean {
        return (!this.isMutation() && this.checkChanged2To1GS()) ||
            (this.isMutation() && this.checkChanged2To1GSMutation());
    }

    private checkChanged2To1GS() {
        return this.gesuchModelManager.getGesuch().gesuchsteller2 && this.gesuchModelManager.getGesuch().gesuchsteller2.id
            && this.initialFamiliensituation.hasSecondGesuchsteller()
            && this.isScheidung();
    }

    private checkChanged2To1GSMutation() {
        return this.gesuchModelManager.getGesuch().gesuchsteller2 && this.gesuchModelManager.getGesuch().gesuchsteller2.id
            && this.isScheidung()
            && this.model.familiensituationErstgesuch
            && !this.model.familiensituationErstgesuch.hasSecondGesuchsteller();
    }

    private isScheidung() {
        return this.initialFamiliensituation.hasSecondGesuchsteller()
            && !this.getFamiliensituation().hasSecondGesuchsteller();
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
        if (this.isMutationAndDateSet() && !this.isGesuchReadonly() && !this.isKorrekturModusJugendamt()) {
            return true;
        } else {
            return false;
        }
    }

    public hasEmptyAenderungPer(): boolean {
        if (this.isMutation() && !this.getFamiliensituation().aenderungPer
            && !this.isKorrekturModusJugendamt()
            && !this.getFamiliensituationErstgesuch().isSameFamiliensituation(this.getFamiliensituation())) {
            return true;
        }
        return false;
    }

    public resetFamsit() {
        this.getFamiliensituation().revertFamiliensituation(this.getFamiliensituationErstgesuch());
    }

    public hasError(): boolean {
        if (this.isMutation() && this.getFamiliensituation().aenderungPer
            && this.getFamiliensituationErstgesuch().isSameFamiliensituation(this.getFamiliensituation())) {
            return true;
        }
        return false;
    }

    public showError(): boolean {
        return this.hasError() && this.savedClicked;
    }

    public onDatumBlur(): void {
        if (this.hasEmptyAenderungPer()) {
            this.resetFamsit();
        }
    }

    public gesuchstellerHasChangedZivilstand(): boolean {
        if (this.model.familiensituationGS && this.model.familiensituationGS.aenderungPer) {
            return true;
        }
        return false;
    }
}
