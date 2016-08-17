import {IComponentOptions, IFormController, ILogService} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import {IEinkommensverschlechterungStateParams} from '../../gesuch.route';
import BerechnungsManager from '../../service/berechnungsManager';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
import ErrorService from '../../../core/errors/service/ErrorService';
import TSEinkommensverschlechterung from '../../../models/TSEinkommensverschlechterung';
import TSFinanzielleSituation from '../../../models/TSFinanzielleSituation';
let template = require('./einkommensverschlechterungView.html');
require('./einkommensverschlechterungView.less');


export class EinkommensverschlechterungViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = EinkommensverschlechterungViewController;
    controllerAs = 'vm';
}

export class EinkommensverschlechterungViewController extends AbstractGesuchViewController {

    public showSelbstaendig: boolean;
    public geschaeftsgewinnBasisjahrMinus1: number;
    public geschaeftsgewinnBasisjahrMinus2: number;

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService', '$log'];

    /* @ngInject */
    constructor($stateParams: IEinkommensverschlechterungStateParams, $state: IStateService, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService, private $log: ILogService) {
        super($state, gesuchModelManager, berechnungsManager);
        let parsedGesuchstelllerNum: number = parseInt($stateParams.gesuchstellerNumber, 10);
        let parsedBasisJahrPlusNum: number = parseInt($stateParams.basisjahrPlus, 10);
        this.gesuchModelManager.setGesuchstellerNumber(parsedGesuchstelllerNum);
        this.gesuchModelManager.setBasisJahrPlusNumber(parsedBasisJahrPlusNum);
        this.initViewModel();
        this.calculate();

    }

    private initViewModel() {
        if (this.gesuchModelManager) {
            this.gesuchModelManager.initEinkommensverschlechterungContainer(this.gesuchModelManager.getBasisJahrPlusNumber(),
                this.gesuchModelManager.getGesuchstellerNumber());

            this.getGeschaeftsgewinnFromFS();

            this.showSelbstaendig = this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationJA.isSelbstaendig()
                || (this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().geschaeftsgewinnBasisjahr !== null
                && this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().geschaeftsgewinnBasisjahr !== undefined);
        }
    }

    public showSelbstaendigClicked() {
        if (!this.showSelbstaendig) {
            this.resetSelbstaendigFields();
        }
    }

    private resetSelbstaendigFields() {
        if (this.gesuchModelManager.getEinkommensverschlechterungToWorkWith()) {
            this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().geschaeftsgewinnBasisjahr = undefined;
            this.calculate();
        }
    }

    showSteuerveranlagung(): boolean {
        return !this.gesuchModelManager.getGemeinsameSteuererklaerungToWorkWith() || this.gesuchModelManager.getGemeinsameSteuererklaerungToWorkWith() === false;
    }

    showSteuererklaerung(): boolean {
        return this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === false;
    }

    showHintSteuererklaerung(): boolean {
        return this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuererklaerungAusgefuellt === true &&
            this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === false;
    }

    showHintSteuerveranlagung(): boolean {
        return this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === true;
    }

    steuerveranlagungClicked(): void {
        // Wenn Steuerveranlagung JA -> auch StekErhalten -> JA
        if (this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === true) {
            this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuererklaerungAusgefuellt = true;
        } else if (this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === false) {
            // Steuerveranlagung neu NEIN -> Fragen loeschen
            this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuererklaerungAusgefuellt = undefined;
        }
    }

    previousStep(form: IFormController): void {
        this.save(form, this.navigatePrevious);
    }

    nextStep(form: IFormController): void {
        this.save(form, this.navigateNext);
    }

    private save(form: angular.IFormController, navigationFunction: (gesuch: any) => any) {
        if (form.$valid) {
            this.errorService.clearAll();
            this.gesuchModelManager.saveEinkommensverschlechterungContainer().then(navigationFunction);

        }

    }

    //muss als instance arrow function definiert werden statt als prototyp funktionw eil sonst this undefined ist
    private navigateNext = (einkommensverschlechterungResponse: any) => {
        if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 1)) {
            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) {
                // 1 , 1
                if (this.gesuchModelManager.isBasisJahr2Required()) {
                    this.state.go('gesuch.einkommensverschlechterung', {
                        gesuchstellerNumber: '1',
                        basisjahrPlus: '2'
                    });
                } else if (this.gesuchModelManager.isGesuchsteller2Required()) {
                    this.state.go('gesuch.einkommensverschlechterung', {
                        gesuchstellerNumber: '2',
                        basisjahrPlus: '1'
                    });
                } else {
                    this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '1'});
                }

            } else { //gesuchsteller ===2

                // 2 , 1
                if (this.gesuchModelManager.isBasisJahr2Required()) {
                    this.state.go('gesuch.einkommensverschlechterung', {
                        gesuchstellerNumber: '2',
                        basisjahrPlus: '2'
                    });
                } else {
                    this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '1'});
                }

            }

        } else if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 2)) {

            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) {
                // 1 , 2
                if (this.gesuchModelManager.isGesuchsteller2Required()) {
                    this.state.go('gesuch.einkommensverschlechterung', {
                        gesuchstellerNumber: '2',
                        basisjahrPlus: '1'
                    });
                } else {
                    this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '1'});
                }

            } else { //gesuchsteller ===2
                // 2 , 2
                this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '1'});
            }
        }
    };

    //muss als instance arrow function definiert werden statt als prototyp funktionw eil sonst this undefined ist
    private navigatePrevious = (einkommensverschlechterungResponse: any) => {
        if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 1)) {
            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) {
                // 1 , 1
                if (this.gesuchModelManager.isGesuchsteller2Required()) {
                    this.state.go('gesuch.einkommensverschlechterungSteuern');
                } else {
                    this.state.go('gesuch.einkommensverschlechterungInfo');
                }
            } else { //gesuchsteller ===2

                // 2 , 1
                if (this.gesuchModelManager.isBasisJahr2Required()) {
                    this.state.go('gesuch.einkommensverschlechterung', {
                        gesuchstellerNumber: '1',
                        basisjahrPlus: '2'
                    });
                } else {
                    this.state.go('gesuch.einkommensverschlechterung', {
                        gesuchstellerNumber: '1',
                        basisjahrPlus: '1'
                    });
                }

            }

        } else if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 2)) {

            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) {
                // 1 , 2
                this.state.go('gesuch.einkommensverschlechterung', {
                    gesuchstellerNumber: '1',
                    basisjahrPlus: '1'
                });
            } else { //gesuchsteller ===2
                // 2 , 2
                this.state.go('gesuch.einkommensverschlechterung', {
                    gesuchstellerNumber: '2',
                    basisjahrPlus: '1'
                });
            }
        }
    };

    calculate() {
        this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.gesuch, this.gesuchModelManager.getBasisJahrPlusNumber());
    }

    resetForm() {
        this.initViewModel();
    }

    public getEinkommensverschlechterung(): TSEinkommensverschlechterung {
        return this.gesuchModelManager.getEinkommensverschlechterungToWorkWith();
    }

    public getResultate(): TSFinanzielleSituationResultateDTO {
        return this.berechnungsManager.getEinkommensverschlechterungResultate(this.gesuchModelManager.getBasisJahrPlusNumber());
    }

    public getGeschaeftsgewinnFromFS(): void {
        if (!this.gesuchModelManager.getStammdatenToWorkWith() || !this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer
            || !this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationJA) {
            // TODO: Wenn die finanzielleSituation noch nicht existiert haben wir ein Problem
            this.$log.debug('Fehler: FinSit muss existieren');
            return;
        }

        let fs: TSFinanzielleSituation = this.gesuchModelManager.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationJA;
        if (this.gesuchModelManager.basisJahrPlusNumber === 1) {
            this.geschaeftsgewinnBasisjahrMinus1 = fs.geschaeftsgewinnBasisjahr;
            this.geschaeftsgewinnBasisjahrMinus2 = fs.geschaeftsgewinnBasisjahrMinus1;
        } else {
            //basisjahr Plus 2
            this.geschaeftsgewinnBasisjahrMinus1 = this.gesuchModelManager.getStammdatenToWorkWith().einkommensverschlechterungContainer.ekvJABasisJahrPlus1.geschaeftsgewinnBasisjahr;
            this.geschaeftsgewinnBasisjahrMinus2 = fs.geschaeftsgewinnBasisjahr;
        }
    }

}
