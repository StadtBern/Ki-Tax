import {IComponentOptions, IQService, IPromise, IScope} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IErwerbspensumStateParams} from '../../gesuch.route';
import TSErwerbspensumContainer from '../../../models/TSErwerbspensumContainer';
import {TSTaetigkeit, getTSTaetigkeit} from '../../../models/enums/TSTaetigkeit';
import {
    TSZuschlagsgrund,
    getTSZuschlagsgruendeForGS,
    getTSZuschlagsgrunde
} from '../../../models/enums/TSZuschlagsgrund';
import TSErwerbspensum from '../../../models/TSErwerbspensum';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import WizardStepManager from '../../service/wizardStepManager';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import TSGesuchstellerContainer from '../../../models/TSGesuchstellerContainer';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import ITranslateService = angular.translate.ITranslateService;
let template = require('./erwerbspensumView.html');
require('./erwerbspensumView.less');


export class ErwerbspensumViewComponentConfig implements IComponentOptions {
    transclude: boolean;
    bindings: any;
    template: string | Function;
    controller: any;
    controllerAs: string;

    constructor() {
        this.transclude = false;
        this.bindings = {};
        this.template = template;
        this.controller = ErwerbspensumViewController;
        this.controllerAs = 'vm';
    }
}


export class ErwerbspensumViewController extends AbstractGesuchViewController<TSErwerbspensumContainer> {

    gesuchsteller: TSGesuchstellerContainer;
    patternPercentage: string;

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager',
        'CONSTANTS', '$scope', 'ErrorService', 'AuthServiceRS', 'WizardStepManager', '$q', '$translate'];
    /* @ngInject */
    constructor($stateParams: IErwerbspensumStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, $scope: IScope, private errorService: ErrorService,
                private authServiceRS: AuthServiceRS, wizardStepManager: WizardStepManager, private $q: IQService, private $translate: ITranslateService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.ERWERBSPENSUM);
        this.patternPercentage = this.CONSTANTS.PATTERN_PERCENTAGE;
        this.gesuchModelManager.setGesuchstellerNumber(parseInt($stateParams.gesuchstellerNumber));
        this.gesuchsteller = this.gesuchModelManager.getStammdatenToWorkWith();
        if (this.gesuchsteller) {
            if ($stateParams.erwerbspensumNum) {
                let ewpNum = parseInt($stateParams.erwerbspensumNum) | 0;
                this.model = angular.copy(this.gesuchsteller.erwerbspensenContainer[ewpNum]);
            } else {
                //wenn erwerbspensum nummer nicht definiert ist heisst dass, das wir ein neues erstellen sollten
                this.model = this.initEmptyEwpContainer();
            }
        } else {
            errorService.addMesageAsError('Unerwarteter Zustand: Gesuchsteller unbekannt');
            console.log('kein gesuchsteller gefunden');
        }

        //hier muessen wir den event beim verlassen nicht abfangen da wir die daten erst beim erfolgreichen speichern ins model einfuegen
    }

    getTaetigkeitenList(): Array<TSTaetigkeit> {
        return getTSTaetigkeit();
    }

    getZuschlagsgrundList(): Array<TSZuschlagsgrund> {
        if (this.authServiceRS.isOneOfRoles(TSRoleUtil.getGesuchstellerOnlyRoles())) {
            return getTSZuschlagsgruendeForGS();
        } else {
            return getTSZuschlagsgrunde();
        }
    }

    /**
     * Beim speichern wird geschaut ob Zuschlagsgrund noetig ist, wenn nicht zuruecksetzten
     * @param erwerbspensum
     */
    private maybeResetZuschlagsgrund(erwerbspensum: TSErwerbspensumContainer) {
        if (erwerbspensum && !erwerbspensum.erwerbspensumJA.zuschlagZuErwerbspensum) {
            erwerbspensum.erwerbspensumJA.zuschlagsprozent = undefined;
            erwerbspensum.erwerbspensumJA.zuschlagsgrund = undefined;
        }
    }

    save(): IPromise<any> {
        if (this.isGesuchValid()) {

            if (!this.form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when(this.model);
            }
            this.maybeResetZuschlagsgrund(this.model);
            this.errorService.clearAll();
            return this.gesuchModelManager.saveErwerbspensum(this.gesuchsteller, this.model);
        }
        return undefined;
    }

    cancel() {
        this.form.$setPristine();
    }


    private initEmptyEwpContainer(): TSErwerbspensumContainer {
        let ewp = new TSErwerbspensum();
        let ewpContainer = new TSErwerbspensumContainer();
        ewpContainer.erwerbspensumJA = ewp;
        return ewpContainer;

    }

    viewZuschlag(): boolean {
        return this.model.erwerbspensumJA.taetigkeit === TSTaetigkeit.ANGESTELLT ||
            this.model.erwerbspensumJA.taetigkeit === TSTaetigkeit.AUSBILDUNG ||
            this.model.erwerbspensumJA.taetigkeit === TSTaetigkeit.SELBSTAENDIG;
    }

    taetigkeitChanged() {
        if (!this.viewZuschlag()) {
            this.model.erwerbspensumJA.zuschlagZuErwerbspensum = false;
            this.model.erwerbspensumJA.zuschlagsprozent = undefined;
            this.model.erwerbspensumJA.zuschlagsgrund = undefined;
        }
    }

    erwerbspensumDisabled(): boolean {
        // Disabled wenn Mutation, ausser bei Bearbeiter Jugendamt
        return this.model.erwerbspensumJA.vorgaengerId && !this.authServiceRS.isOneOfRoles(TSRoleUtil.getAdministratorJugendamtRole());
    }

    public getTextZuschlagErwerbspensumKorrekturJA() : string {
        if (this.model.erwerbspensumGS && this.model.erwerbspensumGS.zuschlagZuErwerbspensum === true) {
            let ewp: TSErwerbspensum = this.model.erwerbspensumGS;
            let grundText = this.$translate.instant(ewp.zuschlagsgrund.toString());
            return this.$translate.instant('JA_KORREKTUR_ZUSCHLAG_ERWERBSPENSUM', {
                zuschlagsgrund: grundText,
                zuschlagsprozent: ewp.zuschlagsprozent});
        } else {
            return this.$translate.instant('LABEL_KEINE_ANGABE');
        }
    }
}
