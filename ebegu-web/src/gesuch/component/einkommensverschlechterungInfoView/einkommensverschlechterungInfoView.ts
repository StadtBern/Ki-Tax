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
import IFormController = angular.IFormController;
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';

let template = require('./einkommensverschlechterungInfoView.html');
require('./einkommensverschlechterungInfoView.less');


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

    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService', 'EbeguUtil', 'WizardStepManager'];
    /* @ngInject */
    constructor($state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private CONSTANTS: any, private errorService: ErrorService, private ebeguUtil: EbeguUtil, wizardStepManager: WizardStepManager) {
        super($state, gesuchModelManager, berechnungsManager, wizardStepManager);

        this.initViewModel();
    }

    private initViewModel() {
        this.gesuchModelManager.initEinkommensverschlechterungInfo();
        this.wizardStepManager.updateWizardStepStatus(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG, TSWizardStepStatus.IN_BEARBEITUNG);
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
        this.save(form, this.navigatePrevious);

    }

    /**
     * Navigation forward
     */
    nextStep(form: IFormController): void {
        this.save(form, this.navigateNext);
    }

    private save(form: angular.IFormController, navigationFunction: (response: any) => any) {
        if (form.$valid) {
            this.errorService.clearAll();
            if (this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus1 === undefined) {
                this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus1 = false;
            }
            if (this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus2 === undefined) {
                this.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus2 = false;
            }

            this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus1 = this.getStichtagFromMonat(this.selectedStichtagBjP1, this.gesuchModelManager.getBasisjahr() + 1);
            this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus2 = this.getStichtagFromMonat(this.selectedStichtagBjP2, this.gesuchModelManager.getBasisjahr() + 2);
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

}
