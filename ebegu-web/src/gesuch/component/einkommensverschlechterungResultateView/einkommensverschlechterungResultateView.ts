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

import {IComponentOptions, IPromise} from 'angular';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import ErrorService from '../../../core/errors/service/ErrorService';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import TSEinkommensverschlechterung from '../../../models/TSEinkommensverschlechterung';
import TSEinkommensverschlechterungContainer from '../../../models/TSEinkommensverschlechterungContainer';
import TSFinanzModel from '../../../models/TSFinanzModel';
import {IEinkommensverschlechterungResultateStateParams} from '../../gesuch.route';
import BerechnungsManager from '../../service/berechnungsManager';
import GesuchModelManager from '../../service/gesuchModelManager';
import WizardStepManager from '../../service/wizardStepManager';
import AbstractGesuchViewController from '../abstractGesuchView';
import IQService = angular.IQService;
import IScope = angular.IScope;
import ITimeoutService = angular.ITimeoutService;

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
export class EinkommensverschlechterungResultateViewController extends AbstractGesuchViewController<TSFinanzModel> {

    resultatBasisjahr: TSFinanzielleSituationResultateDTO;
    resultatProzent: string;

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager', 'ErrorService',
        'WizardStepManager', '$q', '$scope', 'AuthServiceRS', '$timeout'];

    /* @ngInject */
    constructor($stateParams: IEinkommensverschlechterungResultateStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private errorService: ErrorService,
                wizardStepManager: WizardStepManager, private $q: IQService, $scope: IScope, private authServiceRS: AuthServiceRS,
                $timeout: ITimeoutService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG, $timeout);
        let parsedBasisJahrPlusNum = parseInt($stateParams.basisjahrPlus, 10);
        this.model = new TSFinanzModel(this.gesuchModelManager.getBasisjahr(), this.gesuchModelManager.isGesuchsteller2Required(), null, parsedBasisJahrPlusNum);
        this.model.copyEkvDataFromGesuch(this.gesuchModelManager.getGesuch());
        this.model.copyFinSitDataFromGesuch(this.gesuchModelManager.getGesuch());
        this.gesuchModelManager.setBasisJahrPlusNumber(parsedBasisJahrPlusNum);
        this.calculate();
        this.resultatBasisjahr = null;
        this.calculateResultateVorjahr();
    }

    showGS2(): boolean {
        return this.model.isGesuchsteller2Required();
    }

    showResult(): boolean {
        if (this.model.getBasisJahrPlus() === 1) {
            let ekvFuerBasisJahrPlus1 = this.model.einkommensverschlechterungInfoContainer.einkommensverschlechterungInfoJA.ekvFuerBasisJahrPlus1
                && this.model.einkommensverschlechterungInfoContainer.einkommensverschlechterungInfoJA.ekvFuerBasisJahrPlus1 === true;
            return ekvFuerBasisJahrPlus1 === true;

        } else {
            return true;
        }
    }

    private save(): IPromise<void> {
        if (this.isGesuchValid()) {
            if (!this.form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                // Update wizardStepStatus also if the form is empty and not dirty
                return this.updateStatus(false);
            }

            this.model.copyEkvSitDataToGesuch(this.gesuchModelManager.getGesuch());
            this.errorService.clearAll();
            if (this.gesuchModelManager.getGesuch().gesuchsteller1) {
                this.gesuchModelManager.setGesuchstellerNumber(1);
                if (this.gesuchModelManager.getGesuch().gesuchsteller2) {
                    return this.gesuchModelManager.saveEinkommensverschlechterungContainer().then(() => {
                        this.gesuchModelManager.setGesuchstellerNumber(2);
                        return this.gesuchModelManager.saveEinkommensverschlechterungContainer().then(() => {
                            return this.updateStatus(true);
                        });
                    });
                } else {
                    return this.gesuchModelManager.saveEinkommensverschlechterungContainer().then(() => {
                        return this.updateStatus(true);
                    });
                }
            }
        }
        return undefined;
    }

    /**
     * Hier wird der Status von WizardStep auf OK (MUTIERT fuer Mutationen) aktualisiert aber nur wenn es die letzt Seite EVResultate
     * gespeichert wird. Sonst liefern wir einfach den aktuellen GS als Promise zurueck.
     */
    private updateStatus(changes: boolean): IPromise<any> {
        if (this.isLastEinkVersStep()) {
            if (this.gesuchModelManager.getGesuch().isMutation()) {
                if (this.wizardStepManager.getCurrentStep().wizardStepStatus === TSWizardStepStatus.NOK || changes) {
                    return this.wizardStepManager.updateCurrentWizardStepStatusMutiert();
                }
            } else {
                return this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK);
            }
        }
        return this.$q.when(this.gesuchModelManager.getStammdatenToWorkWith()); //wenn nichts gespeichert einfach den aktuellen GS zurueckgeben

    }

    calculate() {
        if (this.model && this.model.getBasisJahrPlus()) {
            this.berechnungsManager
                .calculateEinkommensverschlechterungTemp(this.model, this.model.getBasisJahrPlus())
                .then(() => {
                    this.resultatProzent = this.calculateVeraenderung();
                });
        } else {
            console.log('No gesuch and Basisjahr to calculate');
        }
    }

    public getEinkommensverschlechterungContainerGS1(): TSEinkommensverschlechterungContainer {
        return this.model.einkommensverschlechterungContainerGS1;
    }

    public getEinkommensverschlechterungGS1_GS(): TSEinkommensverschlechterung {
        if (this.model.getBasisJahrPlus() === 2) {
            return this.getEinkommensverschlechterungContainerGS1().ekvGSBasisJahrPlus2;
        } else {
            return this.getEinkommensverschlechterungContainerGS1().ekvGSBasisJahrPlus1;
        }
    }

    public getEinkommensverschlechterungGS1_JA(): TSEinkommensverschlechterung {
        if (this.model.getBasisJahrPlus() === 2) {
            return this.getEinkommensverschlechterungContainerGS1().ekvJABasisJahrPlus2;
        } else {
            return this.getEinkommensverschlechterungContainerGS1().ekvJABasisJahrPlus1;
        }
    }

    public getEinkommensverschlechterungContainerGS2(): TSEinkommensverschlechterungContainer {
        return this.model.einkommensverschlechterungContainerGS2;
    }

    public getEinkommensverschlechterungGS2_GS(): TSEinkommensverschlechterung {
        if (this.model.getBasisJahrPlus() === 2) {
            return this.getEinkommensverschlechterungContainerGS2().ekvGSBasisJahrPlus2;
        } else {
            return this.getEinkommensverschlechterungContainerGS2().ekvGSBasisJahrPlus1;
        }
    }

    public getEinkommensverschlechterungGS2_JA(): TSEinkommensverschlechterung {
        if (this.model.getBasisJahrPlus() === 2) {
            return this.getEinkommensverschlechterungContainerGS2().ekvJABasisJahrPlus2;
        } else {
            return this.getEinkommensverschlechterungContainerGS2().ekvJABasisJahrPlus1;
        }
    }

    public getResultate(): TSFinanzielleSituationResultateDTO {
        if (this.model.getBasisJahrPlus() === 2) {
            return this.berechnungsManager.einkommensverschlechterungResultateBjP2;
        } else {
            return this.berechnungsManager.einkommensverschlechterungResultateBjP1;
        }
    }

    public calculateResultateVorjahr() {

        this.berechnungsManager.calculateFinanzielleSituationTemp(this.model).then((resultatVorjahr) => {
            this.resultatBasisjahr = resultatVorjahr;
            this.resultatProzent = this.calculateVeraenderung();
        });
    }

    /**
     *
     * @returns {string} Veraenderung im Prozent im vergleich zum Vorjahr
     */
    public calculateVeraenderung(): string {
        if (this.resultatBasisjahr) {

            let massgebendesEinkVorAbzFamGr = this.getResultate().massgebendesEinkVorAbzFamGr;
            let massgebendesEinkVorAbzFamGrBJ = this.resultatBasisjahr.massgebendesEinkVorAbzFamGr;
            if (massgebendesEinkVorAbzFamGr && massgebendesEinkVorAbzFamGrBJ) {

                // we divide it by 10000 because we need a result with two decimals
                let promil: number = 10000 - (massgebendesEinkVorAbzFamGr * 10000 / massgebendesEinkVorAbzFamGrBJ);
                let sign: string;
                promil = Math.round(promil);
                if (promil > 0) {
                    sign = '- ';
                } else {
                    sign = '+ ';
                }
                return sign + (Math.abs(promil) / 100).toFixed(2) + ' %';
            } else if (!massgebendesEinkVorAbzFamGr && !massgebendesEinkVorAbzFamGrBJ) {
                // case: Kein Einkommen in diesem Jahr und im letzten Jahr
                return '+ 0.00 %';
            } else if (!massgebendesEinkVorAbzFamGr) {
                // case: Kein Einkommen in diesem Jahr aber Einkommen im letzten Jahr
                return '- 100.00 %';
            } else {
                // case: Kein Einkommen im letzten Jahr aber Einkommen in diesem Jahr
                return '+ 100.00 %';
            }
        }
        return '';
    }

    /**
     * Prueft ob es die letzte Seite von EVResultate ist. Es ist die letzte Seite wenn es zum letzten EV-Jahr gehoert
     * @returns {boolean}
     */
    private isLastEinkVersStep(): boolean {
        // Letztes Jahr haengt von den eingegebenen Daten ab
        return (
                this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo().ekvFuerBasisJahrPlus2
                && this.gesuchModelManager.basisJahrPlusNumber === 2
            ) || (
                !this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo().ekvFuerBasisJahrPlus2
                && this.gesuchModelManager.basisJahrPlusNumber === 1
            );
    }
}
