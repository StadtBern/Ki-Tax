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
import TSFinanzielleSituation from '../../../models/TSFinanzielleSituation';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import IQService = angular.IQService;
import IScope = angular.IScope;
import ITimeoutService = angular.ITimeoutService;
import IPromise = angular.IPromise;
import AbstractGesuchViewController from '../abstractGesuchView';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import TSFinanzModel from '../../../models/TSFinanzModel';
import TSGesuch from '../../../models/TSGesuch';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';

let template = require('./finanzielleSituationStartView.html');
require('./finanzielleSituationStartView.less');
let removeDialogTemplate = require('../../dialog/removeDialogTemplate.html');

export class FinanzielleSituationStartViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FinanzielleSituationStartViewController;
    controllerAs = 'vm';
}

export class FinanzielleSituationStartViewController extends AbstractGesuchViewController<TSFinanzModel> {

    finanzielleSituationRequired: boolean;
    areThereOnlySchulamtangebote: boolean;
    areThereOnlyFerieninsel: boolean;
    allowedRoles: Array<TSRoleUtil>;
    private initialModel: TSFinanzModel;


    static $inject: string[] = ['GesuchModelManager', 'BerechnungsManager', 'ErrorService',
        'WizardStepManager', '$q', '$scope', '$timeout', 'DvDialog'];

    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager, private errorService: ErrorService,
                wizardStepManager: WizardStepManager, private $q: IQService, $scope: IScope, $timeout: ITimeoutService,
                private dvDialog: DvDialog) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.FINANZIELLE_SITUATION, $timeout);

        this.model = new TSFinanzModel(this.gesuchModelManager.getBasisjahr(), this.gesuchModelManager.isGesuchsteller2Required(), null);
        this.model.copyFinSitDataFromGesuch(this.gesuchModelManager.getGesuch());
        this.initialModel = angular.copy(this.model);

        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        this.areThereOnlySchulamtangebote = this.gesuchModelManager.areThereOnlySchulamtAngebote(); // so we load it just once
        this.areThereOnlyFerieninsel = this.gesuchModelManager.areThereOnlyFerieninsel(); // so we load it just once
    }

    showSteuerveranlagung(): boolean {
        return this.model.gemeinsameSteuererklaerung === true;
    }

    showSteuererklaerung(): boolean {
        return this.getFinanzielleSituationGS1().steuerveranlagungErhalten === false;
    }

    private save(): IPromise<TSGesuch> {
        this.errorService.clearAll();
        return this.gesuchModelManager.saveFinanzielleSituationStart()
            .then((gesuch: TSGesuch) => {
                // Noetig, da nur das ganze Gesuch upgedated wird und die Aeenderng bei der FinSit sonst nicht
                // bemerkt werden
                if (this.gesuchModelManager.getGesuch().isMutation()) {
                    this.wizardStepManager.updateCurrentWizardStepStatusMutiert();
                }
                return gesuch;
            });
    }

    private confirmAndSave(): IPromise<TSGesuch> {
        if (this.isGesuchValid()) {
            this.model.copyFinSitDataToGesuch(this.gesuchModelManager.getGesuch());
            if (!this.form.$dirty) {
                if (this.updateStepDueToOnlyFerieninsel()) {
                    return this.wizardStepManager.updateWizardStepStatus(TSWizardStepName.FINANZIELLE_SITUATION, TSWizardStepStatus.OK).then(() => {
                        return this.gesuchModelManager.getGesuch();
                    });
                }
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when(this.gesuchModelManager.getGesuch());
            }
            if (this.finanzielleSituationTurnedNotRequired()) {
                return this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
                    title: 'FINSIT_WARNING',
                    deleteText: 'FINSIT_WARNING_BESCHREIBUNG',
                    parentController: undefined,
                    elementID: undefined,
                    form: this.form
                }).then(() => {   //User confirmed changes
                    return this.save();
                });
            } else {
                return this.save();
            }
        }
        return undefined;
    }

    /**
     * Id the Step is still in status IN_BEARBEITUNG and there are only Ferieninsel, the Gesuch must be updated.
     */
    private updateStepDueToOnlyFerieninsel() {
        return this.wizardStepManager.hasStepGivenStatus(TSWizardStepName.FINANZIELLE_SITUATION, TSWizardStepStatus.IN_BEARBEITUNG)
            && this.gesuchModelManager.getGesuch().areThereOnlyFerieninsel();
    }

    public finanzielleSituationTurnedNotRequired(): boolean {
        return this.initialModel.isFinanzielleSituationDesired() && !this.model.isFinanzielleSituationDesired();
    }

    public getFinanzielleSituationGS1(): TSFinanzielleSituation {
        return this.model.finanzielleSituationContainerGS1.finanzielleSituationJA;
    }

    private getFinanzielleSituationGS2(): TSFinanzielleSituation {
        return this.model.finanzielleSituationContainerGS2.finanzielleSituationJA;
    }

    public isFinanziellesituationRequired(): boolean {
        return this.finanzielleSituationRequired;
    }

    private hasTagesschulenAnmeldung(): boolean {
        return this.gesuchModelManager.getGesuchsperiode().hasTagesschulenAnmeldung();
    }

    public gemeinsameStekClicked(): void {
        if (this.model.gemeinsameSteuererklaerung === false && this.model.finanzielleSituationContainerGS1 && !this.model.finanzielleSituationContainerGS1.isNew()) {
            // Wenn neu NEIN und schon was eingegeben -> Fragen mal auf false setzen und Status auf nok damit man sicher noch weiter muss!
            this.initSteuerFragen();
            this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.NOK);
        } else if (this.model.gemeinsameSteuererklaerung === false) {
            // Wenn neu NEIN -> Fragen loeschen wenn noch nichts eingegeben worden ist
            this.model.finanzielleSituationContainerGS1 = undefined;
            this.model.finanzielleSituationContainerGS2 = undefined;
        } else {
            this.model.initFinSit();
        }
    }

    /**
     * Es muss ein Wert geschrieben werden, um finsit persisierten zu können -> setzt die Antwort der Fragen auf false
     */
    private initSteuerFragen() {
        if (this.model.finanzielleSituationContainerGS1) {
            let gs1FinanzielleSituationJA = this.model.finanzielleSituationContainerGS1.finanzielleSituationJA;
            gs1FinanzielleSituationJA.steuererklaerungAusgefuellt = !gs1FinanzielleSituationJA.steuererklaerungAusgefuellt ? false : gs1FinanzielleSituationJA.steuererklaerungAusgefuellt;
            gs1FinanzielleSituationJA.steuerveranlagungErhalten = !gs1FinanzielleSituationJA.steuerveranlagungErhalten ? false : gs1FinanzielleSituationJA.steuerveranlagungErhalten;
        }
        if (this.model.finanzielleSituationContainerGS2) {
            let gs2FinanzielleSituationJA = this.model.finanzielleSituationContainerGS2.finanzielleSituationJA;
            gs2FinanzielleSituationJA.steuererklaerungAusgefuellt = !gs2FinanzielleSituationJA.steuererklaerungAusgefuellt ? false : gs2FinanzielleSituationJA.steuererklaerungAusgefuellt;
            gs2FinanzielleSituationJA.steuerveranlagungErhalten = !gs2FinanzielleSituationJA.steuerveranlagungErhalten ? false : gs2FinanzielleSituationJA.steuerveranlagungErhalten;

        }
    }

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

    public steuererklaerungClicked() {
        if (this.model.gemeinsameSteuererklaerung === true) {
            this.model.finanzielleSituationContainerGS2.finanzielleSituationJA.steuererklaerungAusgefuellt =
                this.model.finanzielleSituationContainerGS1.finanzielleSituationJA.steuererklaerungAusgefuellt;
        }
    }

    public is2GSRequired(): boolean {
        return this.gesuchModelManager.isGesuchsteller2Required();
    }
}
