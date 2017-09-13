import {IComponentOptions, IPromise, IQService, IScope, ITimeoutService} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IErwerbspensumStateParams} from '../../gesuch.route';
import TSErwerbspensumContainer from '../../../models/TSErwerbspensumContainer';
import {getTSTaetigkeit, TSTaetigkeit} from '../../../models/enums/TSTaetigkeit';
import {getTSZuschlagsgruendeForGS, getTSZuschlagsgrunde, TSZuschlagsgrund} from '../../../models/enums/TSZuschlagsgrund';
import TSErwerbspensum from '../../../models/TSErwerbspensum';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import WizardStepManager from '../../service/wizardStepManager';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import TSGesuchstellerContainer from '../../../models/TSGesuchstellerContainer';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import TSEbeguParameter from '../../../models/TSEbeguParameter';
import {TSEbeguParameterKey} from '../../../models/enums/TSEbeguParameterKey';
import GlobalCacheService from '../../service/globalCacheService';
import {EbeguParameterRS} from '../../../admin/service/ebeguParameterRS.rest';
import {TSCacheTyp} from '../../../models/enums/TSCacheTyp';
import ITranslateService = angular.translate.ITranslateService;
import DateUtil from '../../../utils/DateUtil';

let template: string = require('./erwerbspensumView.html');
require('./erwerbspensumView.less');

export class ErwerbspensumViewComponentConfig implements IComponentOptions {
    transclude: boolean;
    bindings: any;
    template: string;
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
    maxZuschlagsprozent: number = 100;
    lastTaetigkeit: TSTaetigkeit = undefined;

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager',
        'CONSTANTS', '$scope', 'ErrorService', 'AuthServiceRS', 'WizardStepManager', '$q', '$translate', 'EbeguParameterRS', 'GlobalCacheService', '$timeout'];

    /* @ngInject */
    constructor($stateParams: IErwerbspensumStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, $scope: IScope, private errorService: ErrorService,
                private authServiceRS: AuthServiceRS, wizardStepManager: WizardStepManager, private $q: IQService,
                private $translate: ITranslateService, private ebeguParameterRS: EbeguParameterRS, private globalCacheService: GlobalCacheService,
                $timeout: ITimeoutService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.ERWERBSPENSUM, $timeout);
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
        ebeguParameterRS.getEbeguParameterByGesuchsperiodeCached(
            this.gesuchModelManager.getGesuchsperiode().id,
            this.globalCacheService.getCache(TSCacheTyp.EBEGU_PARAMETER)).then((response: TSEbeguParameter[]) => {
            for (let i = 0; i < response.length; i++) {
                if (response[i].name === TSEbeguParameterKey.PARAM_MAXIMALER_ZUSCHLAG_ERWERBSPENSUM) {
                    // max Wert für Zuschlag Erwerbspensum
                    this.maxZuschlagsprozent = Number(response[i].value);
                }
            }
        });
        this.lastTaetigkeit = this.model.erwerbspensumJA.taetigkeit;
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
        if (this.model.erwerbspensumJA.taetigkeit === TSTaetigkeit.KEINE) {
            this.model.erwerbspensumJA.pensum = 0;
            this.model.erwerbspensumJA.gueltigkeit.gueltigAb = this.gesuchModelManager.getGesuchsperiode().gueltigkeit.gueltigAb;
            this.model.erwerbspensumJA.gueltigkeit.gueltigBis = this.gesuchModelManager.getGesuchsperiode().gueltigkeit.gueltigBis;
        } else if (this.lastTaetigkeit === TSTaetigkeit.KEINE) {
            // Wechsel von KEINE zu etwas anderes -> die (fuer KEINE unsichtbaren) Defaults entfernen
            this.model.erwerbspensumJA.pensum = undefined;
            this.model.erwerbspensumJA.gueltigkeit.gueltigAb = undefined;
            this.model.erwerbspensumJA.gueltigkeit.gueltigBis = undefined;
        }
        this.lastTaetigkeit = this.model.erwerbspensumJA.taetigkeit;
    }

    erwerbspensumDisabled(): boolean {
        // Disabled wenn Mutation, ausser bei Bearbeiter Jugendamt
        return this.model.erwerbspensumJA.vorgaengerId && !this.authServiceRS.isOneOfRoles(TSRoleUtil.getAdministratorJugendamtRole());
    }

    erwerbspensumVisible(): boolean {
        return this.model.erwerbspensumJA.taetigkeit !== TSTaetigkeit.KEINE;
    }

    public getTextZuschlagErwerbspensumKorrekturJA(): string {
        if (this.model.erwerbspensumGS && this.model.erwerbspensumGS.zuschlagZuErwerbspensum === true) {
            let ewp: TSErwerbspensum = this.model.erwerbspensumGS;
            let grundText = this.$translate.instant(ewp.zuschlagsgrund.toString());
            return this.$translate.instant('JA_KORREKTUR_ZUSCHLAG_ERWERBSPENSUM', {
                zuschlagsgrund: grundText,
                zuschlagsprozent: ewp.zuschlagsprozent
            });
        } else {
            return this.$translate.instant('LABEL_KEINE_ANGABE');
        }
    }

    public getZuschlagHelpText(): string {
        return this.$translate.instant('ZUSCHLAGSGRUND_HELP', {
            maxzuschlag: this.maxZuschlagsprozent
        });
    }

}
