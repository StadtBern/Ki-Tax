/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import {IComponentOptions} from 'angular';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import ErrorService from '../../../core/errors/service/ErrorService';
import EwkRS from '../../../core/service/ewkRS.rest';
import {TSAdressetyp} from '../../../models/enums/TSAdressetyp';
import {TSGeschlecht} from '../../../models/enums/TSGeschlecht';
import {TSGesuchEvent} from '../../../models/enums/TSGesuchEvent';
import {TSRole} from '../../../models/enums/TSRole';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import TSAdresse from '../../../models/TSAdresse';
import TSAdresseContainer from '../../../models/TSAdresseContainer';
import TSGesuchstellerContainer from '../../../models/TSGesuchstellerContainer';
import DateUtil from '../../../utils/DateUtil';
import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {EnumEx} from '../../../utils/EnumEx';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import {IStammdatenStateParams} from '../../gesuch.route';
import BerechnungsManager from '../../service/berechnungsManager';
import GesuchModelManager from '../../service/gesuchModelManager';
import WizardStepManager from '../../service/wizardStepManager';
import AbstractGesuchViewController from '../abstractGesuchView';
import './stammdatenView.less';
import IQService = angular.IQService;
import IPromise = angular.IPromise;
import IScope = angular.IScope;
import ITranslateService = angular.translate.ITranslateService;
import IRootScopeService = angular.IRootScopeService;
import ITimeoutService = angular.ITimeoutService;

let template = require('./stammdatenView.html');
require('./stammdatenView.less');

export class StammdatenViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = StammdatenViewController;
    controllerAs = 'vm';
}

export class StammdatenViewController extends AbstractGesuchViewController<TSGesuchstellerContainer> {
    geschlechter: Array<string>;
    showKorrespondadr: boolean;
    showKorrespondadrGS: boolean;
    showRechnungsadr: boolean;
    showRechnungsadrGS: boolean;
    showIBAN: boolean;
    showIBANGS: boolean;
    ebeguRestUtil: EbeguRestUtil;
    allowedRoles: Array<TSRole>;
    gesuchstellerNumber: number;
    private initialModel: TSGesuchstellerContainer;
    private isLastVerfuegtesGesuch: boolean = false;

    static $inject = ['$stateParams', 'EbeguRestUtil', 'GesuchModelManager', 'BerechnungsManager', 'ErrorService', 'WizardStepManager',
        'CONSTANTS', '$q', '$scope', '$translate', 'AuthServiceRS', '$rootScope', 'EwkRS', '$timeout'];

    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, ebeguRestUtil: EbeguRestUtil, gesuchModelManager: GesuchModelManager,
        berechnungsManager: BerechnungsManager, private errorService: ErrorService,
        wizardStepManager: WizardStepManager, private CONSTANTS: any, private $q: IQService, $scope: IScope,
        private $translate: ITranslateService, private authServiceRS: AuthServiceRS, private $rootScope: IRootScopeService,
        private ewkRS: EwkRS, $timeout: ITimeoutService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.GESUCHSTELLER, $timeout);
        this.ebeguRestUtil = ebeguRestUtil;
        this.gesuchstellerNumber = parseInt($stateParams.gesuchstellerNumber, 10);
        this.gesuchModelManager.setGesuchstellerNumber(this.gesuchstellerNumber);

    }

    $onInit() {
        this.initViewmodel();

    }

    private initViewmodel() {
        this.gesuchModelManager.initStammdaten();
        this.model = angular.copy(this.gesuchModelManager.getStammdatenToWorkWith());
        this.initialModel = angular.copy(this.model);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        this.geschlechter = EnumEx.getNames(TSGeschlecht);
        this.showKorrespondadr = (this.model.korrespondenzAdresse && this.model.korrespondenzAdresse.adresseJA) ? true : false;
        this.showKorrespondadrGS = (this.model.korrespondenzAdresse && this.model.korrespondenzAdresse.adresseGS) ? true : false;
        this.showRechnungsadr = (this.model.rechnungsAdresse && this.model.rechnungsAdresse.adresseJA) ? true : false;
        this.showRechnungsadrGS = (this.model.rechnungsAdresse && this.model.rechnungsAdresse.adresseGS) ? true : false;
        this.showIBAN = (this.getModelJA() && this.getModelJA().iban) ? true : false;
        this.showIBANGS = (this.model.gesuchstellerGS && this.model.gesuchstellerGS.iban) ? true : false;
        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
        this.getModel().showUmzug = this.getModel().showUmzug || this.getModel().isThereAnyUmzug();
        this.setLastVerfuegtesGesuch();

        this.$rootScope.$on(TSGesuchEvent[TSGesuchEvent.EWK_PERSON_SELECTED], (event: any, gsNummer: number, ewkId: string) => {
            if (gsNummer === this.gesuchModelManager.gesuchstellerNumber) {
                this.getModelJA().ewkPersonId = ewkId;
                this.getModelJA().ewkAbfrageDatum = DateUtil.today();
                this.form.$dirty = true;
            }
        });
    }

    korrespondenzAdrClicked() {
        if (this.showKorrespondadr) {
            if (!this.model.korrespondenzAdresse) {
                this.model.korrespondenzAdresse = this.initAdresse(TSAdressetyp.KORRESPONDENZADRESSE);
            } else if (!this.model.korrespondenzAdresse.adresseJA) {
                this.initKorrespondenzAdresseJA();
            }
        }
    }

    rechnungsAdrClicked() {
        if (this.showRechnungsadr) {
            if (!this.model.rechnungsAdresse) {
                this.model.rechnungsAdresse = this.initAdresse(TSAdressetyp.RECHNUNGSADRESSE);
            } else if (!this.model.rechnungsAdresse.adresseJA) {
                this.initRechnungsAdresseJA();
            }
        }
    }

    private setLastVerfuegtesGesuch(): void {
        this.isLastVerfuegtesGesuch = this.gesuchModelManager.isNeuestesGesuch();
    }

    private save(): IPromise<TSGesuchstellerContainer> {
        if (this.isGesuchValid()) {
            this.gesuchModelManager.setStammdatenToWorkWith(this.model);
            if (!this.form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                if ((this.gesuchModelManager.getGesuchstellerNumber() === 1 && !this.gesuchModelManager.isGesuchsteller2Required())
                    || this.gesuchModelManager.getGesuchstellerNumber() === 2) {
                    this.updateGSDependentWizardSteps();
                }

                return this.$q.when(this.model);
            }
            // wenn keine Korrespondenzaddr oder Rechnungsadr da ist koennen wir sie wegmachen
            this.maybeResetKorrespondadr();
            this.maybeResetRechnungsadr();
            this.maybeResetIBAN();

            if ((this.gesuchModelManager.getGesuch().gesuchsteller1 && this.gesuchModelManager.getGesuch().gesuchsteller1.showUmzug)
                || (this.gesuchModelManager.getGesuch().gesuchsteller2 && this.gesuchModelManager.getGesuch().gesuchsteller2.showUmzug)
                || this.isMutation()) {
                this.wizardStepManager.unhideStep(TSWizardStepName.UMZUG);
            } else {
                this.wizardStepManager.hideStep(TSWizardStepName.UMZUG);
            }
            this.errorService.clearAll();
            // todo bei Aenderungen von Kontaktdaten sollte man nicht den ganzen GS updaten sondern nur die Kontakdaten
            return this.gesuchModelManager.updateGesuchsteller(false);
        }
        return undefined;
    }

    /**
     * Aktualisiert alle Steps die Abhaengigkeiten mit dem Status von GS haben.
     */
    private updateGSDependentWizardSteps() {
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK); // GESUCHSTELLER
        if (this.wizardStepManager.hasStepGivenStatus(TSWizardStepName.FINANZIELLE_SITUATION, TSWizardStepStatus.NOK)) {
            this.wizardStepManager.updateWizardStepStatus(TSWizardStepName.FINANZIELLE_SITUATION, TSWizardStepStatus.OK);
        }
        if (this.wizardStepManager.hasStepGivenStatus(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG, TSWizardStepStatus.NOK)) {
            this.wizardStepManager.updateWizardStepStatus(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG, TSWizardStepStatus.OK);
        }
    }

    public getModel(): TSGesuchstellerContainer {
        return this.model;
    }

    public getModelJA() {
        return this.model.gesuchstellerJA;
    }

    public getModelGS() {
        return this.model.gesuchstellerGS;
    }

    /**
     * Die Wohnadresse des GS2 darf bei Mutationen in denen der GS2 bereits existiert, nicht geaendert werden.
     * Die Wohnadresse des GS1 darf bei Mutationen nie geaendert werden
     * @returns {boolean}
     */
    public disableWohnadresseFor2GS(): boolean {
        return this.isMutation() && (this.gesuchstellerNumber === 1
            || (this.model.vorgaengerId !== null
                && this.model.vorgaengerId !== undefined));
    }

    public isThereAnyUmzug(): boolean {
        return this.gesuchModelManager.getGesuch().isThereAnyUmzug();
    }

    private maybeResetKorrespondadr(): void {
        if (!this.showKorrespondadr && !this.showKorrespondadrGS) {
            this.getModel().korrespondenzAdresse = undefined; //keine korrAdr weder von GS noch von JA -> entfernen
        } else if (!this.showKorrespondadr) {
            this.getModel().korrespondenzAdresse.adresseJA = undefined; //nur adresse JA wird zurueckgesetzt die GS kann bleiben
        }
    }

    private maybeResetRechnungsadr(): void {
        if (!this.showRechnungsadr && !this.showRechnungsadrGS) {
            this.getModel().rechnungsAdresse = undefined; //keine rechnungsAdresse weder von GS noch von JA -> entfernen
        } else if (!this.showRechnungsadr) {
            this.getModel().rechnungsAdresse.adresseJA = undefined; //nur adresse JA wird zurueckgesetzt die GS kann bleiben
        }
    }

    private maybeResetIBAN(): void {
        if (!this.showIBAN) {
            this.getModelJA().iban = undefined;
            this.getModelJA().kontoinhaber = undefined;
        }
    }

    private initAdresse(adresstyp: TSAdressetyp) {
        let adresseContanier: TSAdresseContainer = new TSAdresseContainer();
        let adresse = new TSAdresse();
        adresse.adresseTyp = adresstyp;
        adresseContanier.showDatumVon = false;
        adresseContanier.adresseJA = adresse;
        return adresseContanier;
    }

    private initKorrespondenzAdresseJA() {
        let addr = new TSAdresse();
        addr.adresseTyp = TSAdressetyp.KORRESPONDENZADRESSE;
        this.model.korrespondenzAdresse.adresseJA = addr;
        this.model.korrespondenzAdresse.showDatumVon = false;
    }

    private initRechnungsAdresseJA() {
        let addr = new TSAdresse();
        addr.adresseTyp = TSAdressetyp.RECHNUNGSADRESSE;
        this.model.rechnungsAdresse.adresseJA = addr;
        this.model.rechnungsAdresse.showDatumVon = false;
    }

    public getTextAddrKorrekturJA(adresseContainer: TSAdresseContainer): string {
        if (adresseContainer && adresseContainer.adresseGS) {
            let adr: TSAdresse = adresseContainer.adresseGS;
            let organisation: string = adr.organisation ? adr.organisation : '-';
            let strasse: string = adr.strasse ? adr.strasse : '-';
            let hausnummer: string = adr.hausnummer ? adr.hausnummer : '-';
            let zusatzzeile: string = adr.zusatzzeile ? adr.zusatzzeile : '-';
            let plz: string = adr.plz ? adr.plz : '-';
            let ort: string = adr.ort ? adr.ort : '-';
            let land: string = this.$translate.instant('Land_' + adr.land);
            return this.$translate.instant('JA_KORREKTUR_ADDR', {
                organisation: organisation,
                strasse: strasse,
                hausnummer: hausnummer,
                zusatzzeile: zusatzzeile,
                plz: plz,
                ort: ort,
                land: land,
            });
        } else {
            return this.$translate.instant('LABEL_KEINE_ANGABE');
        }

    }

    /**
     * Checks whether the fields Email and Telefon are editable or not. The conditions for knowing if it is
     * editable or not are the same ones of isGesuchReadonly(). But in this case, if the user is from the jugenadamt
     * and the current gesuch is the newest one they may also edit those fields
     */
    public areEmailTelefonEditable(): boolean {
        if (this.isLastVerfuegtesGesuch && this.authServiceRS.isOneOfRoles(TSRoleUtil.getGesuchstellerJugendamtSchulamtRoles())) {
            return true;
        } else {
            return !this.isGesuchReadonly();
        }
    }

    public checkAllEwkRelevantDataPresent(): void {
        if (this.getModelJA()) {
            if (this.gesuchModelManager.gesuchstellerNumber === 1) {
                this.ewkRS.gesuchsteller1 = this.getModel();
            } else if (this.gesuchModelManager.gesuchstellerNumber === 2) {
                this.ewkRS.gesuchsteller2 = this.getModel();
            } else {
                console.log('Unbekannte Gesuchstellernummer', this.gesuchstellerNumber);
            }
        }
    }

    public getTextIbanKorrekturJA(): string {
        if (!this.showIBAN && this.showIBANGS) {
            const iban: string = this.getModelGS().iban;
            const kontoinhaber: string = this.getModelGS().kontoinhaber;
            return this.$translate.instant('JA_KORREKTUR_IBAN', {
                iban,
                kontoinhaber,
            });

        }

        return this.$translate.instant('LABEL_KEINE_ANGABE');
    }

    public showIbanDaten(): boolean {
        return (this.gesuchModelManager.getGesuchstellerNumber() === 1)
            && this.gesuchModelManager.getGesuchsperiode()
            && this.gesuchModelManager.getGesuchsperiode().isVerpflegungActive();
    }
}
