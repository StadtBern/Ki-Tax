import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import {IEinkommensverschlechterungStateParams} from '../../gesuch.route';
import BerechnungsManager from '../../service/berechnungsManager';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
import ErrorService from '../../../core/errors/service/ErrorService';
import TSGesuch from '../../../models/TSGesuch';
import TSEinkommensverschlechterung from '../../../models/TSEinkommensverschlechterung';
import IFormController = angular.IFormController;
let template = require('./einkommensverschlechterungView.html');
require('./einkommensverschlechterungView.less');


export class EinkommensverschlechterungViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = EinkommensverschlechterungViewController;
    controllerAs = 'vm';
}

export class EinkommensverschlechterungViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService'];
    /* @ngInject */
    constructor($stateParams: IEinkommensverschlechterungStateParams, $state: IStateService, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService) {
        super($state, gesuchModelManager, berechnungsManager);
        let parsedGesuchstelllerNum: number = parseInt($stateParams.gesuchstellerNumber, 10);
        let parsedBasisJahrPlusNum: number = parseInt($stateParams.basisjahrPlus, 10);
        this.gesuchModelManager.setGesuchstellerNumber(parsedGesuchstelllerNum);
        this.gesuchModelManager.setBasisJahrPlusNumber(parsedBasisJahrPlusNum);
        this.initViewModel();
        this.calculate();
    }

    private initViewModel() {
        this.gesuchModelManager.initEinkommensverschlechterungContainer(this.gesuchModelManager.getEkvFuerBasisJahrPlus(2));
    }

    getGesuch(): TSGesuch {
        if (!this.gesuchModelManager.gesuch) {
            this.gesuchModelManager.initGesuch(false);
        }
        return this.gesuchModelManager.gesuch;
    }


    showSteuerveranlagung(): boolean {
        return this.gesuchModelManager.getEkvFuerBasisJahrPlusToWorkWith() === false;
    }

    showSteuererklaerung(): boolean {
        return this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === false;
    }

    showSelbstaendig(): boolean {
        return this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().selbstaendig === true;
    }

    private steuerveranlagungClicked(): void {
        // Wenn Steuerveranlagung JA -> auch StekErhalten -> JA
        if (this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === true) {
            this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuererklaerungAusgefuellt = true;
        } else if (this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuerveranlagungErhalten === false) {
            // Steuerveranlagung neu NEIN -> Fragen loeschen
            this.gesuchModelManager.getEinkommensverschlechterungToWorkWith().steuererklaerungAusgefuellt = undefined;
        }
    }

    previousStep() {
        if ((this.gesuchModelManager.getGesuchstellerNumber() === 2)) {
            this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: 1});
        } else if ((this.gesuchModelManager.gesuchstellerNumber === 1) && this.gesuchModelManager.isGesuchsteller2Required()) {
            this.state.go('gesuch.finanzielleSituationStart');
        } else {
            this.state.go('gesuch.kinder');
        }
    }

    nextStep() {
        if ((this.gesuchModelManager.getGesuchstellerNumber() === 1) && this.gesuchModelManager.isGesuchsteller2Required()) {
            this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: '2'});
        } else {
            this.state.go('gesuch.finanzielleSituationResultate');
        }
    }

    submit(form: IFormController) {
        if (form.$valid) {
            // Speichern ausloesen
            this.errorService.clearAll();
            this.gesuchModelManager.saveFinanzielleSituation().then((finanzielleSituationResponse: any) => {
                this.nextStep();
            });
        }
    }

    calculate() {
        this.berechnungsManager.calculateFinanzielleSituation(this.gesuchModelManager.gesuch);
    }

    resetForm() {
        this.initViewModel();
    }

    public getEinkommensverschlechterung(): TSEinkommensverschlechterung {
        return this.gesuchModelManager.getEinkommensverschlechterungToWorkWith();
    }

    public getResultate(): TSFinanzielleSituationResultateDTO {
        return this.berechnungsManager.finanzielleSituationResultate;
    }
}
