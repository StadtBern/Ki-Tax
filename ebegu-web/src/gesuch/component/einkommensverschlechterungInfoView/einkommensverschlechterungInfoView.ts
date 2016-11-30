import {IComponentOptions, IPromise} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import EbeguUtil from '../../../utils/EbeguUtil';
import {TSMonth, getTSMonthValues} from '../../../models/enums/TSMonth';
import TSEinkommensverschlechterungInfo from '../../../models/TSEinkommensverschlechterungInfo';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {TSRole} from '../../../models/enums/TSRole';
import ITranslateService = angular.translate.ITranslateService;
import IQService = angular.IQService;

let template = require('./einkommensverschlechterungInfoView.html');
require('./einkommensverschlechterungInfoView.less');
let removeDialogTemplate = require('../../dialog/removeDialogTemplate.html');


export class EinkommensverschlechterungInfoViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = EinkommensverschlechterungInfoViewController;
    controllerAs = 'vm';
}

export class EinkommensverschlechterungInfoViewController extends AbstractGesuchViewController<TSEinkommensverschlechterungInfo> {

    monthsStichtage: Array<TSMonth>;
    selectedStichtagBjP1: TSMonth = undefined;
    selectedStichtagBjP2: TSMonth = undefined;
    initialEinkVersInfo: TSEinkommensverschlechterungInfo;
    allowedRoles: Array<TSRole>;

    static $inject: string[] = ['GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService', 'EbeguUtil'
        , 'WizardStepManager', 'DvDialog', '$q'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private CONSTANTS: any, private errorService: ErrorService, private ebeguUtil: EbeguUtil, wizardStepManager: WizardStepManager,
                private DvDialog: DvDialog, private $q: IQService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.model = angular.copy(this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo);
        this.initViewModel();
        this.initialEinkVersInfo = angular.copy(this.model);
        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
    }

    private initViewModel() {

        this.wizardStepManager.setCurrentStep(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        this.monthsStichtage = getTSMonthValues();
        this.selectedStichtagBjP1 = this.getMonatFromStichtag(this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus1);
        this.selectedStichtagBjP2 = this.getMonatFromStichtag(this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus2);
    }

    public initEinkommensverschlechterungInfo(): void {
        if (!this.model) {
            this.model = new TSEinkommensverschlechterungInfo();
            this.model.ekvFuerBasisJahrPlus1 = false;
            this.model.ekvFuerBasisJahrPlus2 = false;

        }
    }

    getEinkommensverschlechterungsInfo(): TSEinkommensverschlechterungInfo {
        if (!this.model) {
            this.initEinkommensverschlechterungInfo();
        }
        return this.model;
    }

    showEkvi(): boolean {
        return this.getEinkommensverschlechterungsInfo().einkommensverschlechterung;
    }

    showJahrPlus1(): boolean {
        return this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus1;
    }

    showJahrPlus2(): boolean {
        return this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus2;
    }

    public getBasisJahrPlusAsString(jahr: number): string {
        return this.ebeguUtil.getBasisJahrPlusAsString(this.gesuchModelManager.getGesuch().gesuchsperiode, jahr);
    }

    /**
     * Gibt den Tag (Moment) anhand des Jahres und Monat enum zurück
     * @param monat
     * @param jahr
     * @returns {any}
     */
    getStichtagFromMonat(monat: TSMonth, jahr: number): moment.Moment {
        if (monat) {
            return moment([jahr, this.monthsStichtage.indexOf(monat)]);
        } else {
            return null;
        }
    }

    /**
     * Gibt den Monat enum anhand des Stichtages zurück
     * @param stichtag
     * @returns {any}
     */
    getMonatFromStichtag(stichtag: moment.Moment): TSMonth {
        if (stichtag) {
            return this.monthsStichtage[stichtag.month()];
        } else {
            return null;
        }
    }

    public confirmAndSave(form: angular.IFormController): IPromise<TSEinkommensverschlechterungInfo> {
        if (this.isConfirmationRequired()) {
            return this.DvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
                title: 'EINKVERS_WARNING',
                deleteText: 'EINKVERS_WARNING_BESCHREIBUNG'
            }).then(() => {   //User confirmed changes
                return this.save(form);
            });
        } else {
            return this.save(form);
        }
    }

    private save(form: angular.IFormController): IPromise<TSEinkommensverschlechterungInfo> {
        if (form.$valid) {
            if (!form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when(this.model);
            }
            this.errorService.clearAll();
            if (this.getEinkommensverschlechterungsInfo().einkommensverschlechterung) {
                if (this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus1 === undefined) {
                    this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus1 = false;
                }
                if (this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus2 === undefined) {
                    this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus2 = false;
                }

                this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus1 = this.getStichtagFromMonat(this.selectedStichtagBjP1, this.gesuchModelManager.getBasisjahr() + 1);
                this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus2 = this.getStichtagFromMonat(this.selectedStichtagBjP2, this.gesuchModelManager.getBasisjahr() + 2);
            } else {
                //wenn keine EV eingetragen wird, setzen wir alles auf undefined, da keine Daten gespeichert werden sollen
                this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus1 = false;
                this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus2 = false;
                this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP1 = undefined;
                this.getEinkommensverschlechterungsInfo().gemeinsameSteuererklaerung_BjP2 = undefined;
                this.getEinkommensverschlechterungsInfo().grundFuerBasisJahrPlus1 = undefined;
                this.getEinkommensverschlechterungsInfo().grundFuerBasisJahrPlus2 = undefined;
                this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus1 = undefined;
                this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus2 = undefined;
            }
            this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo = this.getEinkommensverschlechterungsInfo();
            return this.gesuchModelManager.updateEinkommensverschlechterungsInfo();
        }
        return undefined;
    }

    public isRequired(basisJahrPlus: number): boolean {
        if (basisJahrPlus === 2) {
            return this.model.einkommensverschlechterung && !this.model.ekvFuerBasisJahrPlus1;
        } else {
            return this.model.einkommensverschlechterung && !this.model.ekvFuerBasisJahrPlus2;
        }
    }

    /**
     * Confirmation is required when the user already introduced data for the EV and is about to remove it
     * @returns {boolean}
     */
    private isConfirmationRequired(): boolean {
        return (this.initialEinkVersInfo.einkommensverschlechterung !== undefined && this.initialEinkVersInfo.einkommensverschlechterung !== null
        && !this.model.einkommensverschlechterung
        && this.gesuchModelManager.getGesuch().gesuchsteller1 && this.gesuchModelManager.getGesuch().gesuchsteller1.einkommensverschlechterungContainer !== null
        && this.gesuchModelManager.getGesuch().gesuchsteller1.einkommensverschlechterungContainer !== undefined);
    }

}
