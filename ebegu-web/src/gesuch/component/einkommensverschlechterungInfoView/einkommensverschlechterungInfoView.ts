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

    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService', 'EbeguUtil'];
    /* @ngInject */
    constructor($state: IStateService, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService, private ebeguUtil: EbeguUtil) {
        super($state, gesuchModelManager, berechnungsManager);

        this.initViewModel();
    }

    private initViewModel() {
        this.gesuchModelManager.initEinkommensverschlechterungInfo();
        this.monthsStichtage = getTSMonthValues();
        this.selectedStichtagBjP1 = this.getMonatFromStichtag(this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus1);
        this.selectedStichtagBjP2 = this.getMonatFromStichtag(this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus2);
    }

    getGesuch(): TSGesuch {
        if (!this.gesuchModelManager.gesuch) {
            this.gesuchModelManager.initGesuch(false);
        }
        return this.gesuchModelManager.gesuch;
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
        return this.ebeguUtil.getBasisJahrPlusAsString(this.gesuchModelManager.gesuch.gesuchsperiode, jahr);
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
    previousStep() {
        this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: '1'});
    }

    /**
     * Navigation forward
     */
    nextStep() {
        //todo gapa muesste hier nicht vm.gesuchModelManager.gesuch.einkommensverschlechterungInfo.einkommensverschlechterung verwendet werden
        if (this.getEinkommensverschlechterungsInfo().einkommensverschlechterung) { // was muss hier sein?
            if (this.gesuchModelManager.isGesuchsteller2Required()) {
                this.state.go('gesuch.einkommensverschlechterungSteuern');
            } else {
                this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '1'});
            }
        } else {
            this.state.go('gesuch.dokumente');
        }
    }

    submit(form: IFormController) {
        if (form.$valid) {
            // Speichern ausloesen
            this.errorService.clearAll();

            this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus1 = this.getStichtagFromMonat(this.selectedStichtagBjP1, this.gesuchModelManager.getBasisjahr() + 1);
            this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus2 = this.getStichtagFromMonat(this.selectedStichtagBjP2, this.gesuchModelManager.getBasisjahr() + 2);

            this.gesuchModelManager.updateGesuch().then((gesuch: any) => {
                this.nextStep();
            });
        }
    }

    resetForm() {
        this.initViewModel();
    }

}
