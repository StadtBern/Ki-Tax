import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import {IEinkommensverschlechterungResultateStateParams} from '../../gesuch.route';
import BerechnungsManager from '../../service/berechnungsManager';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
import ErrorService from '../../../core/errors/service/ErrorService';
import TSEinkommensverschlechterungContainer from '../../../models/TSEinkommensverschlechterungContainer';
import TSEinkommensverschlechterung from '../../../models/TSEinkommensverschlechterung';
import IFormController = angular.IFormController;
let template = require('./einkommensverschlechterungResultateView.html');
require('./einkommensverschlechterungResultateView.less');

export class EinkommensverschlechterungResultateViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = EinkommensverschlechterungResultateViewController;
    controllerAs = 'vm';
}

/**
 * Controller fuer die Finanzielle Situation
 */
export class EinkommensverschlechterungResultateViewController extends AbstractGesuchViewController {

    gesuchsteller1EkvCont: TSEinkommensverschlechterungContainer;
    gesuchsteller2EkvCont: TSEinkommensverschlechterungContainer;
    parsedBasisJahrPlusNum: number;
    resultatVorjahr: TSFinanzielleSituationResultateDTO;
    resultatProzent: string;

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService'];
    /* @ngInject */
    constructor($stateParams: IEinkommensverschlechterungResultateStateParams, $state: IStateService, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService) {
        super($state, gesuchModelManager, berechnungsManager);
        this.parsedBasisJahrPlusNum = parseInt($stateParams.basisjahrPlus, 10);
        this.gesuchModelManager.setBasisJahrPlusNumber(this.parsedBasisJahrPlusNum);
        this.initViewModel();
        this.calculate();
        this.resultatVorjahr = null;
        this.calculateResultateVorjahr();
    }

    private initViewModel() {
        //this.gesuchModelManager.initEinkommensverschlechterungContainer();
    }

    showGemeinsam(): boolean {
        return this.gesuchModelManager.isGesuchsteller2Required() &&
            this.gesuchModelManager.getGemeinsameSteuererklaerungToWorkWith() === true;
    }

    showGS1(): boolean {
        return !this.showGemeinsam();
    }

    showGS2(): boolean {
        return this.gesuchModelManager.isGesuchsteller2Required() &&
            this.gesuchModelManager.getGemeinsameSteuererklaerungToWorkWith() === false;
    }

    showResult(): boolean {
        if (this.parsedBasisJahrPlusNum === 1) {
            let ekvFuerBasisJahrPlus1 = this.gesuchModelManager.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1
                && this.gesuchModelManager.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1 === true;
            return ekvFuerBasisJahrPlus1 === true;

        } else {
            return true;
        }
    }

    previousStep(form: IFormController): void {
        if (form.$valid) {
            this.errorService.clearAll();
            this.gesuchModelManager.updateGesuch().then((gesuch: any) => {
                if (this.parsedBasisJahrPlusNum === 2) {
                    this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '1'});
                } else {
                    // baisjahrPlus1

                    let gesuchsteller2Required: boolean = this.gesuchModelManager.isGesuchsteller2Required();
                    let basisJahr2Required: boolean = this.gesuchModelManager.isBasisJahr2Required();

                    if (gesuchsteller2Required && basisJahr2Required) {
                        this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '2', basisjahrPlus: '2'});
                    } else if (gesuchsteller2Required) {
                        this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '2', basisjahrPlus: '1'});
                    } else if (basisJahr2Required) {
                        this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '1', basisjahrPlus: '2'});
                    } else {
                        this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '1', basisjahrPlus: '1'});
                    }

                }
            });
        }
    }

    nextStep(form: IFormController): void {
        if (form.$valid) {
            this.errorService.clearAll();
            this.gesuchModelManager.updateGesuch().then((gesuch: any) => {
                if (this.parsedBasisJahrPlusNum === 2) {
                    this.state.go('gesuch.dokumente');
                } else {
                    let ekvFuerBasisJahrPlus2 = this.gesuchModelManager.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2
                        && this.gesuchModelManager.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2 === true;
                    if (ekvFuerBasisJahrPlus2) {
                        this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '2'});
                    } else {
                        this.state.go('gesuch.dokumente');
                    }
                }
            });
        }
    }

    calculate() {
        if (this.gesuchModelManager.gesuch && this.parsedBasisJahrPlusNum) {
            this.berechnungsManager
                .calculateEinkommensverschlechterung(this.gesuchModelManager.gesuch, this.parsedBasisJahrPlusNum)
                .then(() => {
                    this.resultatProzent = this.calculateVeraenderung();
                });
        } else {
            console.log('No gesuch and Basisjahr to calculate');
        }
    }

    public getEinkommensverschlechterungContainerGS1(): TSEinkommensverschlechterungContainer {
        if (!this.gesuchsteller1EkvCont) {
            if (this.gesuchModelManager.gesuch.gesuchsteller1) {
                this.gesuchsteller1EkvCont = this.gesuchModelManager.gesuch.gesuchsteller1.einkommensverschlechterungContainer;
            } else {
                this.gesuchsteller1EkvCont = new TSEinkommensverschlechterungContainer();
            }
        }
        return this.gesuchsteller1EkvCont;

    }

    public getEinkommensverschlechterungGS1_GS(): TSEinkommensverschlechterung {
        if (this.parsedBasisJahrPlusNum === 2) {
            return this.getEinkommensverschlechterungContainerGS1().ekvGSBasisJahrPlus2;
        } else {
            return this.getEinkommensverschlechterungContainerGS1().ekvGSBasisJahrPlus1;
        }
    }

    public getEinkommensverschlechterungGS1_JA(): TSEinkommensverschlechterung {
        if (this.parsedBasisJahrPlusNum === 2) {
            return this.getEinkommensverschlechterungContainerGS1().ekvJABasisJahrPlus2;
        } else {
            return this.getEinkommensverschlechterungContainerGS1().ekvJABasisJahrPlus1;
        }
    }

    public getEinkommensverschlechterungContainerGS2(): TSEinkommensverschlechterungContainer {
        if (!this.gesuchsteller2EkvCont) {
            if (this.gesuchModelManager.gesuch.gesuchsteller2) {
                this.gesuchsteller2EkvCont = this.gesuchModelManager.gesuch.gesuchsteller2.einkommensverschlechterungContainer;
            } else {
                this.gesuchsteller2EkvCont = new TSEinkommensverschlechterungContainer();
            }
        }
        return this.gesuchsteller2EkvCont;
    }

    public getEinkommensverschlechterungGS2_GS(): TSEinkommensverschlechterung {
        if (this.parsedBasisJahrPlusNum === 2) {
            return this.getEinkommensverschlechterungContainerGS2().ekvGSBasisJahrPlus2;
        } else {
            return this.getEinkommensverschlechterungContainerGS2().ekvGSBasisJahrPlus1;
        }
    }

    public getEinkommensverschlechterungGS2_JA(): TSEinkommensverschlechterung {
        if (this.parsedBasisJahrPlusNum === 2) {
            return this.getEinkommensverschlechterungContainerGS2().ekvJABasisJahrPlus2;
        } else {
            return this.getEinkommensverschlechterungContainerGS2().ekvJABasisJahrPlus1;
        }
    }

    public getResultate(): TSFinanzielleSituationResultateDTO {
        if (this.parsedBasisJahrPlusNum === 2) {
            return this.berechnungsManager.einkommensverschlechterungResultateBjP2;
        } else {
            return this.berechnungsManager.einkommensverschlechterungResultateBjP1;
        }
    }

    public calculateResultateVorjahr() {

        if (this.parsedBasisJahrPlusNum === 2) {
            this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.gesuch, 1).then((resultatVorjahr) => {
                this.resultatVorjahr = resultatVorjahr;
                this.resultatProzent = this.calculateVeraenderung();
            });
        } else {
            this.berechnungsManager.calculateFinanzielleSituation(this.gesuchModelManager.gesuch).then((resultatVorjahr) => {
                this.resultatVorjahr = resultatVorjahr;
                this.resultatProzent = this.calculateVeraenderung();
            });
        }
    }


    /**
     *
     * @returns {string} Veraenderung im Prozent im vergleich zum Vorjahr
     */
    public calculateVeraenderung(): string {
        if (this.resultatVorjahr) {

            let massgebendesEinkommen = this.getResultate().massgebendesEinkommen;
            let massgebendesEinkommenVJ = this.resultatVorjahr.massgebendesEinkommen;
            if (massgebendesEinkommen && massgebendesEinkommenVJ) {

                let promil: number = 1000 - (massgebendesEinkommen * 1000 / massgebendesEinkommenVJ);
                let sign: string;
                promil = Math.round(promil);
                if (promil > 0) {
                    sign = '- ';
                } else {
                    sign = '+ ';
                }
                return sign + Math.abs(Math.floor(promil / 10)) + '.' + Math.abs(promil % 10) + ' %';
            } else if (!massgebendesEinkommen && !massgebendesEinkommenVJ) {
                // case: Kein Einkommen in diesem Jahr und im letzten Jahr
                return '+ 0 %';
            } else if (!massgebendesEinkommen) {
                // case: Kein Einkommen in diesem Jahr aber Einkommen im letzten Jahr
                return '- 100 %';
            } else {
                // case: Kein Einkommen im letzten Jahr aber Einkommen in diesem Jahr
                return '+ 100 %';
            }
        }
        return '';
    }
}
