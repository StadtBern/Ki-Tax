import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IErwerbspensumStateParams} from '../../gesuch.route';
import TSGesuchsteller from '../../../models/TSGesuchsteller';
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
import {TSRole} from '../../../models/enums/TSRole';
import IFormController = angular.IFormController;
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


export class ErwerbspensumViewController extends AbstractGesuchViewController {

    gesuchsteller: TSGesuchsteller;
    erwerbspensum: TSErwerbspensumContainer;
    patternPercentage: string;

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'BerechnungsManager',
        'CONSTANTS', '$scope', 'ErrorService', 'AuthServiceRS'];
    /* @ngInject */
    constructor($stateParams: IErwerbspensumStateParams, state: IStateService, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private $scope: any, private errorService: ErrorService,
                private authServiceRS: AuthServiceRS) {
        super(state, gesuchModelManager, berechnungsManager);
        var vm = this;
        this.gesuchModelManager.initGesuch(false);  //wird aufgerufen um einen restorepunkt des aktullen gesuchs zu machen
        this.patternPercentage = this.CONSTANTS.PATTERN_PERCENTAGE;
        this.gesuchModelManager.setGesuchstellerNumber(parseInt($stateParams.gesuchstellerNumber));
        this.gesuchsteller = this.gesuchModelManager.getStammdatenToWorkWith();
        if (this.gesuchsteller) {
            if ($stateParams.erwerbspensumNum) {
                let ewpNum = parseInt($stateParams.erwerbspensumNum) | 0;
                this.erwerbspensum = this.gesuchsteller.erwerbspensenContainer[ewpNum];
            } else {
                //wenn erwerbspensum nummer nicht definiert ist heisst dass, das wir ein neues erstellen sollten
                this.erwerbspensum = this.initEmptyEwpContainer();
            }

        } else {
            console.log('kein gesuchsteller gefunden');
        }
        //Wir verlassen uns hier darauf, dass zuerst das popup vom unsavedChanges plugin kommt welches den user fragt ob er die ungesp. changes verwerfen will
        $scope.$on('$stateChangeStart', (navEvent: any, toState: any, toParams: any, fromState: any, fromParams: any) => {
            //Wenn die Maske verlassen wird, werden automatisch die Eintraege entfernt, die noch nicht in der DB gespeichert wurden
            if (navEvent.defaultPrevented !== undefined && navEvent.defaultPrevented === false) {
                this.reset();
            }
        });
    }

    getTaetigkeitenList(): Array<TSTaetigkeit> {
        return getTSTaetigkeit();
    }

    getZuschlagsgrundList(): Array<TSZuschlagsgrund> {
        if (this.authServiceRS.isRole(TSRole.GESUCHSTELLER)) {
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

    save(form: IFormController) {
        if (form.$valid) {
            this.maybeResetZuschlagsgrund(this.erwerbspensum);
            this.errorService.clearAll();
            this.gesuchModelManager.saveErwerbspensum(this.gesuchsteller, this.erwerbspensum).then((response: any) => {
                form.$setPristine();
                this.state.go('gesuch.erwerbsPensen');
            });
        }
    }

    cancel(form: IFormController) {
        this.reset();
        form.$setPristine();
        this.state.go('gesuch.erwerbsPensen');
    }

    reset() {
        this.gesuchModelManager.restoreBackupOfPreviousGesuch();
    }

    private initEmptyEwpContainer(): TSErwerbspensumContainer {
        let ewp = new TSErwerbspensum();
        let ewpContainer = new TSErwerbspensumContainer();
        ewpContainer.erwerbspensumJA = ewp;
        return ewpContainer;

    }

    viewZuschlag(): boolean {
        return this.erwerbspensum.erwerbspensumJA.taetigkeit === TSTaetigkeit.ANGESTELLT ||
            this.erwerbspensum.erwerbspensumJA.taetigkeit === TSTaetigkeit.AUSBILDUNG ||
            this.erwerbspensum.erwerbspensumJA.taetigkeit === TSTaetigkeit.SELBSTAENDIG;
    }

    taetigkeitChanged() {
        if (!this.viewZuschlag()) {
            this.erwerbspensum.erwerbspensumJA.zuschlagZuErwerbspensum = false;
            this.erwerbspensum.erwerbspensumJA.zuschlagsprozent = undefined;
            this.erwerbspensum.erwerbspensumJA.zuschlagsgrund = undefined;
        }
    }
}
