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
import IFormController = angular.IFormController;
let template = require('./erwerbspensumView.html');


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

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', '$scope'];
    /* @ngInject */
    constructor($stateParams: IErwerbspensumStateParams, state: IStateService, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager,  private CONSTANTS: any, private $scope: any) {
        super(state, gesuchModelManager, berechnungsManager);
        var vm = this;
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

    }

    getTaetigkeitenList(): Array<TSTaetigkeit> {
        return getTSTaetigkeit();
    }

    getZuschlagsgrundList(): Array<TSZuschlagsgrund> {
        if (this.isGesuchstellerRole()) {
            return getTSZuschlagsgruendeForGS();
        } else {
            return getTSZuschlagsgrunde();
        }
    }

    submit(form: IFormController) {
        if (form.$valid) {
            this.maybeResetZuschlagsgrund(this.erwerbspensum);
            this.gesuchModelManager.saveErwerbspensum(this.gesuchsteller, this.erwerbspensum).then((response: any) => {
                this.state.go('gesuch.erwerbsPensen');
            });
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

    cancel() {
        this.state.go('gesuch.erwerbsPensen');
    }

    private initEmptyEwpContainer(): TSErwerbspensumContainer {
        let ewp = new TSErwerbspensum();
        let ewpContainer = new TSErwerbspensumContainer();
        ewpContainer.erwerbspensumJA = ewp;
        return ewpContainer;

    }
}
