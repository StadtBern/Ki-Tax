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
import TSEinkommensverschlechterungInfoContainer from '../../../models/TSEinkommensverschlechterungInfoContainer';
import EinkommensverschlechterungInfoRS from '../../service/einkommensverschlechterungInfoRS.rest';
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

export class EinkommensverschlechterungInfoViewController extends AbstractGesuchViewController<TSEinkommensverschlechterungInfoContainer> {

    monthsStichtage: Array<TSMonth>;
    selectedStichtagBjP1: TSMonth = undefined;
    selectedStichtagBjP2: TSMonth = undefined;
    initialEinkVersInfo: TSEinkommensverschlechterungInfoContainer;
    allowedRoles: Array<TSRole>;

    static $inject: string[] = ['GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService', 'EbeguUtil'
        , 'WizardStepManager', 'DvDialog', '$q', 'EinkommensverschlechterungInfoRS'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private CONSTANTS: any, private errorService: ErrorService, private ebeguUtil: EbeguUtil, wizardStepManager: WizardStepManager,
                private DvDialog: DvDialog, private $q: IQService, private einkommensverschlechterungInfoRS: EinkommensverschlechterungInfoRS) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.initialEinkVersInfo = angular.copy(this.gesuchModelManager.getGesuch().einkommensverschlechterungInfoContainer);
        this.model = angular.copy(this.initialEinkVersInfo);
        this.initViewModel();
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
            this.model = new TSEinkommensverschlechterungInfoContainer();
            this.model.init();
        }
    }

    getEinkommensverschlechterungsInfoContainer(): TSEinkommensverschlechterungInfoContainer {
        if (!this.model) {
            this.initEinkommensverschlechterungInfo();
        }
        return this.model;
    }

    getEinkommensverschlechterungsInfo(): TSEinkommensverschlechterungInfo {
        return this.getEinkommensverschlechterungsInfoContainer().einkommensverschlechterungInfoJA;
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

    public confirmAndSave(form: angular.IFormController): IPromise<TSEinkommensverschlechterungInfoContainer> {
        if (form.$valid) {
            if (!form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when(this.model);
            }
            if (this.isConfirmationRequired()) {
                return this.DvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
                    title: 'EINKVERS_WARNING',
                    deleteText: 'EINKVERS_WARNING_BESCHREIBUNG'
                }).then(() => {   //User confirmed changes
                    return this.save();
                });
            } else {
                return this.save();
            }
        }
        return undefined;
    }

    private save(): IPromise<TSEinkommensverschlechterungInfoContainer> {
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

            //TODO: Muss hier die einkommensverschlechterung auch noch gelöscht werden oder reicht es so?
        }

        return this.einkommensverschlechterungInfoRS.saveEinkommensverschlechterungInfo(
            this.getEinkommensverschlechterungsInfoContainer(), this.gesuchModelManager.getGesuch().id)
            .then((ekvInfoRespo: TSEinkommensverschlechterungInfoContainer) => {
                this.gesuchModelManager.getGesuch().einkommensverschlechterungInfoContainer = ekvInfoRespo;
                return ekvInfoRespo;
            });

    }

    public isRequired(basisJahrPlus: number): boolean {
        if (basisJahrPlus === 2) {
            return this.getEinkommensverschlechterungsInfo() && !this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus1;
        } else {
            return this.getEinkommensverschlechterungsInfo() && !this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus2;
        }
    }

    /**
     * Confirmation is required when the user already introduced data for the EV and is about to remove it
     * @returns {boolean}
     */
    private isConfirmationRequired(): boolean {
        return (this.initialEinkVersInfo && this.initialEinkVersInfo.einkommensverschlechterungInfoJA)
            && (!this.getEinkommensverschlechterungsInfo() || !this.getEinkommensverschlechterungsInfo().einkommensverschlechterung)
    }

}
