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

import TSFinanzModel from '../../models/TSFinanzModel';
import AbstractGesuchViewController from './abstractGesuchView';
import {TSWizardStepName} from '../../models/enums/TSWizardStepName';
import BerechnungsManager from '../service/berechnungsManager';
import GesuchModelManager from '../service/gesuchModelManager';
import WizardStepManager from '../service/wizardStepManager';
import IScope = angular.IScope;
import ITimeoutService = angular.ITimeoutService;
import IPromise = angular.IPromise;
import IQService = angular.IQService;
import ErrorService from '../../core/errors/service/ErrorService';
import {TSRoleUtil} from '../../utils/TSRoleUtil';
import TSGesuch from '../../models/TSGesuch';

export class FinanzielleSituationAbstractViewController extends AbstractGesuchViewController<TSFinanzModel> {

    allowedRoles: Array<TSRoleUtil>;
    finanzielleSituationRequired: boolean;
    areThereOnlySchulamtangebote: boolean;
    private initialModel: TSFinanzModel;

    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager, private errorService: ErrorService,
                private $q: IQService, wizardStepManager: WizardStepManager, $scope: IScope, $timeout: ITimeoutService) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.FINANZIELLE_SITUATION, $timeout);
        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
        this.areThereOnlySchulamtangebote = this.gesuchModelManager.areThereOnlySchulamtAngebote(); // so we load it just once
    }

    public initModel(gsNumber: number): void {
        this.model = new TSFinanzModel(this.gesuchModelManager.getBasisjahr(), this.gesuchModelManager.isGesuchsteller2Required(), gsNumber);
        this.model.copyFinSitDataFromGesuch(this.gesuchModelManager.getGesuch());
        this.initialModel = angular.copy(this.model);
    }

    public isFinanziellesituationRequired(): boolean {
        return this.finanzielleSituationRequired;
    }

    public save(): IPromise<TSGesuch> {
        if (this.isGesuchValid()) {
            this.model.copyFinSitDataToGesuch(this.gesuchModelManager.getGesuch());
            this.initialModel = angular.copy(this.model);
            if (!this.form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when(this.gesuchModelManager.getGesuch());
            }
            this.errorService.clearAll();
            return this.gesuchModelManager.updateGesuch()
                .then((gesuch: TSGesuch) => {
                    // Noetig, da nur das ganze Gesuch upgedated wird und die Aeenderng bei der FinSit sonst nicht
                    // bemerkt werden
                    if (this.gesuchModelManager.getGesuch().isMutation()) {
                        this.wizardStepManager.updateCurrentWizardStepStatusMutiert();
                    }
                    return gesuch;
                });
        }
        return undefined;
    }

}
