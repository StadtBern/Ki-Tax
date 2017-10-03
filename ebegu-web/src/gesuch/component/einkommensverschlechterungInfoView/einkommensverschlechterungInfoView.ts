import {IComponentOptions, IPromise} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import EbeguUtil from '../../../utils/EbeguUtil';
import {getTSMonthValues, getTSMonthWithVorjahrValues, TSMonth} from '../../../models/enums/TSMonth';
import TSEinkommensverschlechterungInfo from '../../../models/TSEinkommensverschlechterungInfo';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {TSRole} from '../../../models/enums/TSRole';
import TSEinkommensverschlechterungInfoContainer from '../../../models/TSEinkommensverschlechterungInfoContainer';
import EinkommensverschlechterungInfoRS from '../../service/einkommensverschlechterungInfoRS.rest';
import * as moment from 'moment';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import TSEinkommensverschlechterungContainer from '../../../models/TSEinkommensverschlechterungContainer';
import TSGesuchstellerContainer from '../../../models/TSGesuchstellerContainer';
import EinkommensverschlechterungContainerRS from '../../service/einkommensverschlechterungContainerRS.rest';
import {isAtLeastFreigegeben} from '../../../models/enums/TSAntragStatus';
import IQService = angular.IQService;
import IScope = angular.IScope;
import ITimeoutService = angular.ITimeoutService;

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
    monthsStichtageWithVorjahr: Array<TSMonth>;
    selectedStichtagBjP1: TSMonth = undefined;
    selectedStichtagBjP2: TSMonth = undefined;
    selectedStichtagBjP1_GS: TSMonth = undefined;
    selectedStichtagBjP2_GS: TSMonth = undefined;
    initialEinkVersInfo: TSEinkommensverschlechterungInfoContainer;
    allowedRoles: Array<TSRole>;
    basisJahrUndPeriode = {
        jahr1periode: this.gesuchModelManager.getBasisjahrPlus(1),
        jahr2periode: this.gesuchModelManager.getBasisjahrPlus(2),
        basisjahr: this.gesuchModelManager.getBasisjahr()
    };

    static $inject: string[] = ['GesuchModelManager', 'BerechnungsManager', 'ErrorService', 'EbeguUtil'
        , 'WizardStepManager', 'DvDialog', '$q', 'EinkommensverschlechterungInfoRS', '$scope', 'AuthServiceRS',
        'EinkommensverschlechterungContainerRS', '$timeout'];

    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private errorService: ErrorService, private ebeguUtil: EbeguUtil, wizardStepManager: WizardStepManager,
                private DvDialog: DvDialog, private $q: IQService, private einkommensverschlechterungInfoRS: EinkommensverschlechterungInfoRS,
                $scope: IScope, private authServiceRS: AuthServiceRS, private ekvContainerRS: EinkommensverschlechterungContainerRS,
                $timeout: ITimeoutService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG, $timeout);
        this.initialEinkVersInfo = angular.copy(this.gesuchModelManager.getGesuch().einkommensverschlechterungInfoContainer);
        this.model = angular.copy(this.initialEinkVersInfo);
        this.initViewModel();
        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
    }

    private initViewModel() {
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        this.monthsStichtage = getTSMonthValues();
        this.monthsStichtageWithVorjahr = getTSMonthWithVorjahrValues();
        this.selectedStichtagBjP1 = this.getMonatFromStichtag(this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus1, 1);
        this.selectedStichtagBjP2 = this.getMonatFromStichtag(this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus2, 2);
        if (this.getEinkommensverschlechterungsInfoGS()) {
            this.selectedStichtagBjP1_GS = this.getMonatFromStichtag(this.getEinkommensverschlechterungsInfoGS().stichtagFuerBasisJahrPlus1, 1);
            this.selectedStichtagBjP2_GS = this.getMonatFromStichtag(this.getEinkommensverschlechterungsInfoGS().stichtagFuerBasisJahrPlus2, 2);
        }
        this.initializeEKVContainers();
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

    getEinkommensverschlechterungsInfoGS(): TSEinkommensverschlechterungInfo {
        return this.getEinkommensverschlechterungsInfoContainer().einkommensverschlechterungInfoGS;
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
     * @param basisJahrPlus
     * @returns {any}
     */
    private getStichtagFromMonat(monat: TSMonth, basisJahrPlus: number): moment.Moment {
        if (monat) {
            let jahr: number = this.gesuchModelManager.getBasisjahr() + basisJahrPlus;
            if (monat === TSMonth.VORJAHR) {
                return moment([jahr - 1, 11]); // 1. Dezember des Vorjahres
            }
            return moment([jahr, this.monthsStichtage.indexOf(monat)]);
        } else {
            return null;
        }
    }

    /**
     * Gibt den Monat enum anhand des Stichtages zurück
     * @param stichtag
     * @param basisJahrPlus
     * @returns {any}
     */
    private getMonatFromStichtag(stichtag: moment.Moment, basisJahrPlus: number): TSMonth {
        if (stichtag) {
            if ((this.gesuchModelManager.getBasisjahr() + basisJahrPlus) !== stichtag.year()) {
                return TSMonth.VORJAHR;
            }
            return this.monthsStichtage[stichtag.month()];
        } else {
            return null;
        }
    }

    public confirmAndSave(): IPromise<TSEinkommensverschlechterungInfoContainer> {
        if (this.isGesuchValid()) {
            if (!this.form.$dirty && !this.isThereSomethingNew()) {
                // If the model is new (it hasn't been saved yet) we need to save it
                // If there are no changes in form we don't need anything to update on Server and we could
                // return the promise immediately
                return this.$q.when(this.model);
            }
            if (this.isConfirmationRequired()) {
                return this.DvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
                    title: 'EINKVERS_WARNING',
                    deleteText: 'EINKVERS_WARNING_BESCHREIBUNG',
                    parentController: undefined,
                    elementID: undefined
                }).then(() => {   //User confirmed changes
                    return this.save();
                });
            } else {
                return this.save();
            }
        }
        return undefined;
    }

    /**
     * Sometimes there is something new to save though the form hasn't changed. This is the case when the model i.e.
     * the Einkommensverschlechterung is new (it hasn't been saved yet) or when due to a change in the
     * Familiensituation the GS2 is new and doesn't have an EKVContainer yet.
     */
    private isThereSomethingNew() {
        return (this.model && this.model.isNew())
            || this.isThereAnyEinkommenverschlechterung() && (this.gesuchModelManager.isGesuchsteller2Required() && this.gesuchModelManager.getGesuch().gesuchsteller2
                && (!this.gesuchModelManager.getGesuch().gesuchsteller2.einkommensverschlechterungContainer
                    || this.gesuchModelManager.getGesuch().gesuchsteller2.einkommensverschlechterungContainer.isNew()));
    }

    private isThereAnyEinkommenverschlechterung(): boolean {
        return (
            this.gesuchModelManager.getGesuch().einkommensverschlechterungInfoContainer &&
            this.gesuchModelManager.getGesuch().einkommensverschlechterungInfoContainer.einkommensverschlechterungInfoJA &&
            this.gesuchModelManager.getGesuch().einkommensverschlechterungInfoContainer.einkommensverschlechterungInfoJA.einkommensverschlechterung);
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
            this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus1 = this.getStichtagFromMonat(this.selectedStichtagBjP1, 1);
            this.getEinkommensverschlechterungsInfo().stichtagFuerBasisJahrPlus2 = this.getStichtagFromMonat(this.selectedStichtagBjP2, 2);

            this.initializeEKVContainers();
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

        return this.einkommensverschlechterungInfoRS.saveEinkommensverschlechterungInfo(
            this.getEinkommensverschlechterungsInfoContainer(), this.gesuchModelManager.getGesuch().id)
            .then((ekvInfoRespo: TSEinkommensverschlechterungInfoContainer) => {
                this.gesuchModelManager.getGesuch().einkommensverschlechterungInfoContainer = ekvInfoRespo;
                return this.loadEKVContainersFromServer().then(() => {
                    return ekvInfoRespo;
                });
            });

    }

    private initializeEKVContainers(): void {
        if (this.gesuchModelManager.getGesuch().gesuchsteller1 && !this.gesuchModelManager.getGesuch().gesuchsteller1.einkommensverschlechterungContainer) {
            this.gesuchModelManager.getGesuch().gesuchsteller1.einkommensverschlechterungContainer = new TSEinkommensverschlechterungContainer();
        }
        if (this.gesuchModelManager.isGesuchsteller2Required() && !this.gesuchModelManager.getGesuch().gesuchsteller2.einkommensverschlechterungContainer) {
            this.gesuchModelManager.getGesuch().gesuchsteller2.einkommensverschlechterungContainer = new TSEinkommensverschlechterungContainer();
        }
    }

    private loadEKVContainersFromServer(): IPromise<TSEinkommensverschlechterungContainer> {
        if (this.gesuchModelManager.getGesuch().gesuchsteller1) {
            return this.ekvContainerRS.findEKVContainerForGesuchsteller(this.gesuchModelManager.getGesuch().gesuchsteller1.id)
                .then((responseGS1: TSEinkommensverschlechterungContainer) => {
                    this.gesuchModelManager.getGesuch().gesuchsteller1.einkommensverschlechterungContainer = responseGS1;

                    if (this.gesuchModelManager.isGesuchsteller2Required() && this.gesuchModelManager.getGesuch().gesuchsteller2) {
                        return this.ekvContainerRS.findEKVContainerForGesuchsteller(this.gesuchModelManager.getGesuch().gesuchsteller2.id)
                            .then((responseGS2: TSEinkommensverschlechterungContainer) => {
                                return this.gesuchModelManager.getGesuch().gesuchsteller2.einkommensverschlechterungContainer = responseGS2;
                            });
                    } else {
                        return this.gesuchModelManager.getGesuch().gesuchsteller1.einkommensverschlechterungContainer;
                    }
                });
        }
        return undefined;
    }

    private removeEkvBasisJahrPlus1(gesuchsteller: TSGesuchstellerContainer): void {
        if (gesuchsteller && gesuchsteller.einkommensverschlechterungContainer) {
            gesuchsteller.einkommensverschlechterungContainer.ekvJABasisJahrPlus1 = undefined;
        }
    }

    private removeEkvBasisJahrPlus2(gesuchsteller: TSGesuchstellerContainer): void {
        if (gesuchsteller && gesuchsteller.einkommensverschlechterungContainer) {
            gesuchsteller.einkommensverschlechterungContainer.ekvJABasisJahrPlus2 = undefined;
        }
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
        return this.initialEinkVersInfo && this.initialEinkVersInfo.einkommensverschlechterungInfoJA
            && this.getEinkommensverschlechterungsInfo() && !this.getEinkommensverschlechterungsInfo().einkommensverschlechterung
            && this.hasGS1Ekv();
    }

    /**
     * Checks whether the GS1 exists and has an Einkommensverschlechterung
     */
    private hasGS1Ekv(): boolean {
        return this.gesuchModelManager.getGesuch().gesuchsteller1
            && this.gesuchModelManager.getGesuch().gesuchsteller1.einkommensverschlechterungContainer !== null
            && this.gesuchModelManager.getGesuch().gesuchsteller1.einkommensverschlechterungContainer !== undefined
            && !this.gesuchModelManager.getGesuch().gesuchsteller1.einkommensverschlechterungContainer.isEmpty();
    }

    public isSteueramtLetzterStep(): boolean {
        if (this.authServiceRS.isOneOfRoles(TSRoleUtil.getSteueramtOnlyRoles())) {
            return !this.getEinkommensverschlechterungsInfo().einkommensverschlechterung;
        }
        return false;
    }

    public isJugendamt(): boolean {
        return this.authServiceRS.isOneOfRoles(TSRoleUtil.getAdministratorJugendamtRole());
    }

    public isGesuchFreigegeben(): boolean {
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().status) {
            return isAtLeastFreigegeben(this.gesuchModelManager.getGesuch().status);
        }
        return false;
    }

    public showAblehnungBasisJahrPlus1(): boolean {
        return (!this.isJugendamt() && this.showEkvi() && this.showJahrPlus1()
            && this.getEinkommensverschlechterungsInfo().ekvBasisJahrPlus1Annulliert && this.isGesuchFreigegeben())
            || (this.isJugendamt() && this.showEkvi() && this.showJahrPlus1());
    }

    public showAblehnungBasisJahrPlus2(): boolean {
        return (!this.isJugendamt() && this.showEkvi() && this.showJahrPlus2()
            && this.getEinkommensverschlechterungsInfo().ekvBasisJahrPlus2Annulliert && this.isGesuchFreigegeben())
            || (this.isJugendamt() && this.showEkvi() && this.showJahrPlus2());
    }

}

