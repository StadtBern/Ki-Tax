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
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStammdatenStateParams} from '../../gesuch.route';
import TSFinanzielleSituationContainer from '../../../models/TSFinanzielleSituationContainer';
import BerechnungsManager from '../../service/berechnungsManager';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
import ErrorService from '../../../core/errors/service/ErrorService';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import TSFinanzModel from '../../../models/TSFinanzModel';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import IScope = angular.IScope;
import ITimeoutService = angular.ITimeoutService;

let template = require('./finanzielleSituationResultateView.html');
require('./finanzielleSituationResultateView.less');

export class FinanzielleSituationResultateViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FinanzielleSituationResultateViewController;
    controllerAs = 'vm';
}

/**
 * Controller fuer die Finanzielle Situation
 */
export class FinanzielleSituationResultateViewController extends AbstractGesuchViewController<TSFinanzModel> {

    private initialModel: TSFinanzModel;

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager', 'ErrorService',
        'WizardStepManager', '$scope', '$timeout'];

    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private errorService: ErrorService,
                wizardStepManager: WizardStepManager, $scope: IScope, $timeout: ITimeoutService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.FINANZIELLE_SITUATION, $timeout);

        this.model = new TSFinanzModel(this.gesuchModelManager.getBasisjahr(), this.gesuchModelManager.isGesuchsteller2Required(), null);
        this.model.copyFinSitDataFromGesuch(this.gesuchModelManager.getGesuch());
        this.initialModel = angular.copy(this.model);

        this.calculate();
    }

    showGS2(): boolean {
        return this.model.isGesuchsteller2Required();
    }

    private save(): IPromise<void> {
        if (this.isGesuchValid()) {
            this.model.copyFinSitDataToGesuch(this.gesuchModelManager.getGesuch());
            if (!this.form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                // Update wizardStepStatus also if the form is empty and not dirty
                return this.updateWizardStepStatus();
            }
            this.errorService.clearAll();
            if (this.gesuchModelManager.getGesuch().gesuchsteller1) {
                this.gesuchModelManager.setGesuchstellerNumber(1);
                if (this.gesuchModelManager.getGesuch().gesuchsteller2) {
                    return this.gesuchModelManager.saveFinanzielleSituation().then(() => {
                        this.gesuchModelManager.setGesuchstellerNumber(2);
                        return this.saveFinanzielleSituation();
                    });
                } else {
                    return this.saveFinanzielleSituation();
                }
            }
        }
        return undefined;
    }

    private saveFinanzielleSituation(): IPromise<void> {
        return this.gesuchModelManager.saveFinanzielleSituation().then(() => {
            return this.updateWizardStepStatus();
        });
    }

    /**
     * updates the Status of the Step depending on whether the Gesuch is a Mutation or not
     */
    private updateWizardStepStatus(): IPromise<void> {
        if (this.gesuchModelManager.getGesuch().isMutation()) {
            return this.wizardStepManager.updateCurrentWizardStepStatusMutiert();
        } else {
            return this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK);
        }
    }

    calculate() {
        this.berechnungsManager.calculateFinanzielleSituationTemp(this.model);
    }

    //init weg

    public getFinanzielleSituationGS1(): TSFinanzielleSituationContainer {
        return this.model.finanzielleSituationContainerGS1;

    }

    public getFinanzielleSituationGS2(): TSFinanzielleSituationContainer {
        return this.model.finanzielleSituationContainerGS2;
    }

    public getResultate(): TSFinanzielleSituationResultateDTO {
        return this.berechnungsManager.finanzielleSituationResultate;
    }
}
