import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import EbeguUtil from '../../../utils/EbeguUtil';
import {TSMonth, getTSMonthValues} from '../../../models/enums/TSMonth';
import TSGesuch from '../../../models/TSGesuch';
import TSEinkommensverschlechterungInfo from '../../../models/TSEinkommensverschlechterungInfo';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import IFormController = angular.IFormController;
import ITranslateService = angular.translate.ITranslateService;

let template = require('./einkommensverschlechterungInfoView.html');
require('./einkommensverschlechterungInfoView.less');
let removeDialogTemplate = require('../../dialog/removeDialogTemplate.html');


export class EinkommensverschlechterungInfoViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = EinkommensverschlechterungInfoViewController;
    controllerAs = 'vm';
}

export class EinkommensverschlechterungInfoViewController extends AbstractGesuchViewController {

    monthsStichtage: Array<TSMonth>;
    selectedStichtagBjP1: TSMonth = undefined;
    selectedStichtagBjP2: TSMonth = undefined;
    initialEinkVersInfo: TSEinkommensverschlechterungInfo;

    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService', 'EbeguUtil'
        , 'WizardStepManager', 'DvDialog'];
    /* @ngInject */
    constructor($state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private CONSTANTS: any, private errorService: ErrorService, private ebeguUtil: EbeguUtil, wizardStepManager: WizardStepManager,
                private DvDialog: DvDialog) {
        super($state, gesuchModelManager, berechnungsManager, wizardStepManager);

        this.initViewModel();
        this.initialEinkVersInfo = angular.copy(this.getGesuch().einkommensverschlechterungInfo);
    }

    private initViewModel() {
        this.gesuchModelManager.initEinkommensverschlechterungInfo();
        this.wizardStepManager.setCurrentStep(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        this.monthsStichtage = getTSMonthValues();
        this.selectedStichtagBjP1 = this.getMonatFromStichtag(this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus1);
        this.selectedStichtagBjP2 = this.getMonatFromStichtag(this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus2);
    }

    getGesuch(): TSGesuch {
        if (!this.gesuchModelManager.getGesuch()) {
            this.gesuchModelManager.initGesuch(false);
        }
        return this.gesuchModelManager.getGesuch();
    }

    getEinkommensverschlechterungsInfo(): TSEinkommensverschlechterungInfo {
        if (this.getGesuch().einkommensverschlechterungInfo == null) {
            this.gesuchModelManager.initEinkommensverschlechterungInfo();
        }
        return this.getGesuch().einkommensverschlechterungInfo;
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

    /**
     * Navigation back
     */
    previousStep(form: IFormController): void {
        this.confirmAndSave(form, this.navigatePrevious);

    }

    /**
     * Navigation forward
     */
    nextStep(form: IFormController): void {
        this.confirmAndSave(form, this.navigateNext);
    }

    private confirmAndSave(form: angular.IFormController, navigationFunction: (response: any) => any) {
        if (this.isConfirmationRequired()) {
            this.DvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
                title: 'EINKVERS_WARNING',
                deleteText: 'EINKVERS_WARNING_BESCHREIBUNG'
            }).then(() => {   //User confirmed changes
                this.save(form, navigationFunction);
            });
        } else {
            this.save(form, navigationFunction);
        }
    }

    private save(form: angular.IFormController, navigationFunction: (response: any) => any) {
        if (form.$valid) {
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
            this.gesuchModelManager.updateEinkommensverschlechterungsInfo().then(navigationFunction);
        }
    }

    //muss als instance arrow function definiert werden statt als prototyp funktionw eil sonst this undefined ist
    private navigateNext = (response: any) => {
        if (this.getEinkommensverschlechterungsInfo().einkommensverschlechterung) { // was muss hier sein?
            if (this.gesuchModelManager.isGesuchsteller2Required()) {
                this.state.go('gesuch.einkommensverschlechterungSteuern');
            } else {
                this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '1'});
            }
        } else {
            this.state.go('gesuch.dokumente');
        }
    };

    private navigatePrevious = (response: any) => {
        this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: '1'});
    };


    isRequired(basisJahrPlus: number): boolean {
        let ekv: TSEinkommensverschlechterungInfo = this.getEinkommensverschlechterungsInfo();
        if (basisJahrPlus === 2) {
            return ekv.einkommensverschlechterung && !ekv.ekvFuerBasisJahrPlus1;
        } else {
            return ekv.einkommensverschlechterung && !ekv.ekvFuerBasisJahrPlus2;
        }
    }

    /**
     * Confirmation is required when the user already introduced data for the EV and is about to remove it
     * @returns {boolean}
     */
    private isConfirmationRequired(): boolean {
        return (this.initialEinkVersInfo.einkommensverschlechterung !== undefined && this.initialEinkVersInfo.einkommensverschlechterung !== null
            && !this.getGesuch().einkommensverschlechterungInfo.einkommensverschlechterung
            && this.getGesuch().gesuchsteller1 && this.getGesuch().gesuchsteller1.einkommensverschlechterungContainer !== null
            && this.getGesuch().gesuchsteller1.einkommensverschlechterungContainer !== undefined);
    }

}
