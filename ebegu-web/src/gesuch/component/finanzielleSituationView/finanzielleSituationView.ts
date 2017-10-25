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
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStammdatenStateParams} from '../../gesuch.route';
import TSFinanzielleSituationContainer from '../../../models/TSFinanzielleSituationContainer';
import BerechnungsManager from '../../service/berechnungsManager';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
import ErrorService from '../../../core/errors/service/ErrorService';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import ITimeoutService = angular.ITimeoutService;
import IQService = angular.IQService;
import IScope = angular.IScope;
import ITranslateService = angular.translate.ITranslateService;
import {FinanzielleSituationAbstractViewController} from '../finanzielleSituationAbstractView';

let template = require('./finanzielleSituationView.html');
require('./finanzielleSituationView.less');

export class FinanzielleSituationViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FinanzielleSituationViewController;
    controllerAs = 'vm';
}

export class FinanzielleSituationViewController extends FinanzielleSituationAbstractViewController {

    public showSelbstaendig: boolean;
    public showSelbstaendigGS: boolean;

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager', 'ErrorService',
        'WizardStepManager', '$q', '$scope', '$translate', '$timeout'];

    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, errorService: ErrorService, wizardStepManager: WizardStepManager,
                $q: IQService, $scope: IScope, private $translate: ITranslateService, $timeout: ITimeoutService) {
        super(gesuchModelManager, berechnungsManager, errorService, $q, wizardStepManager, $scope, $timeout);
        let parsedNum: number = parseInt($stateParams.gesuchstellerNumber, 10);
        if (!parsedNum) {
            parsedNum = 1;
        }
        this.initModel(parsedNum);
        this.gesuchModelManager.setGesuchstellerNumber(parsedNum);
        this.initViewModel();
        this.calculate();
    }

    private initViewModel() {
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        this.showSelbstaendig = this.model.getFiSiConToWorkWith().finanzielleSituationJA.isSelbstaendig();
        this.showSelbstaendigGS = this.model.getFiSiConToWorkWith().finanzielleSituationGS
            ? this.model.getFiSiConToWorkWith().finanzielleSituationGS.isSelbstaendig() : false;
    }

    public showSelbstaendigClicked() {
        if (!this.showSelbstaendig) {
            this.resetSelbstaendigFields();
        }
    }

    private resetSelbstaendigFields() {
        if (this.model.getFiSiConToWorkWith()) {
            this.model.getFiSiConToWorkWith().finanzielleSituationJA.geschaeftsgewinnBasisjahr = undefined;
            this.model.getFiSiConToWorkWith().finanzielleSituationJA.geschaeftsgewinnBasisjahrMinus1 = undefined;
            this.model.getFiSiConToWorkWith().finanzielleSituationJA.geschaeftsgewinnBasisjahrMinus2 = undefined;
            this.calculate();
        }
    }

    showSteuerveranlagung(): boolean {
        return !this.model.gemeinsameSteuererklaerung;
    }

    showSteuererklaerung(): boolean {
        return this.model.getFiSiConToWorkWith().finanzielleSituationJA.steuerveranlagungErhalten === false;
    }

    //hier neu init
    public steuerveranlagungClicked(): void {
        // Wenn Steuerveranlagung JA -> auch StekErhalten -> JA
        // Wenn zusätzlich noch GemeinsameStek -> Dasselbe auch für GS2
        // Wenn Steuerveranlagung erhalten, muss auch STEK ausgefüllt worden sein
        if (this.model.finanzielleSituationContainerGS1.finanzielleSituationJA.steuerveranlagungErhalten === true) {
            this.model.finanzielleSituationContainerGS1.finanzielleSituationJA.steuererklaerungAusgefuellt = true;
            if (this.model.gemeinsameSteuererklaerung === true) {
                this.model.finanzielleSituationContainerGS2.finanzielleSituationJA.steuerveranlagungErhalten = true;
                this.model.finanzielleSituationContainerGS2.finanzielleSituationJA.steuererklaerungAusgefuellt = true;
            }
        } else if (this.model.finanzielleSituationContainerGS1.finanzielleSituationJA.steuerveranlagungErhalten === false) {
            // Steuerveranlagung neu NEIN -> Fragen loeschen
            this.model.finanzielleSituationContainerGS1.finanzielleSituationJA.steuererklaerungAusgefuellt = undefined;
            if (this.model.gemeinsameSteuererklaerung === true) {
                this.model.finanzielleSituationContainerGS2.finanzielleSituationJA.steuerveranlagungErhalten = false;
                this.model.finanzielleSituationContainerGS2.finanzielleSituationJA.steuererklaerungAusgefuellt = undefined;
            }
        }
    }

    calculate() {
        this.berechnungsManager.calculateFinanzielleSituationTemp(this.model);
    }

    resetForm() {
        this.initViewModel();
    }

    public getModel(): TSFinanzielleSituationContainer {
        return this.model.getFiSiConToWorkWith();
    }

    public getResultate(): TSFinanzielleSituationResultateDTO {
        return this.berechnungsManager.finanzielleSituationResultate;
    }

    public getTextSelbstaendigKorrektur() {
        let finSitGS = this.getModel().finanzielleSituationGS;
        if (finSitGS && finSitGS.isSelbstaendig()) {

            let gew1 = finSitGS.geschaeftsgewinnBasisjahr;
            let gew2 = finSitGS.geschaeftsgewinnBasisjahrMinus1;
            let gew3 = finSitGS.geschaeftsgewinnBasisjahrMinus2;
            let basisjahr = this.gesuchModelManager.getBasisjahr();
            return this.$translate.instant('JA_KORREKTUR_SELBSTAENDIG',
                {basisjahr: basisjahr, gewinn1: gew1, gewinn2: gew2, gewinn3: gew3});

            // return this.$translate.instant('JA_KORREKTUR_FACHSTELLE', {
            //     name: fachstelle.fachstelle.name,
            //     pensum: fachstelle.pensum,
            //     von: vonText,
            //     bis: bisText});
        } else {
            return this.$translate.instant('LABEL_KEINE_ANGABE');
        }

    }

    /**
     * Mindestens einer aller Felder von Geschaftsgewinn muss ausgefuellt sein. Mit dieser Methode kann man es pruefen.
     * @returns {boolean}
     */
    public isGeschaeftsgewinnRequired(): boolean {
        return (this.getModel().finanzielleSituationJA.geschaeftsgewinnBasisjahr === null || this.getModel().finanzielleSituationJA.geschaeftsgewinnBasisjahr === undefined)
            && (this.getModel().finanzielleSituationJA.geschaeftsgewinnBasisjahrMinus1 === null || this.getModel().finanzielleSituationJA.geschaeftsgewinnBasisjahrMinus1 === undefined)
            && (this.getModel().finanzielleSituationJA.geschaeftsgewinnBasisjahrMinus2 === null || this.getModel().finanzielleSituationJA.geschaeftsgewinnBasisjahrMinus2 === undefined);
    }
}
