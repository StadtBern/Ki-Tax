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

import {TSEingangsart} from '../../../models/enums/TSEingangsart';
import TSFamiliensituation from '../../../models/TSFamiliensituation';
import TSFamiliensituationContainer from '../../../models/TSFamiliensituationContainer';
import TSGesuchsteller from '../../../models/TSGesuchsteller';
import TSGesuchstellerContainer from '../../../models/TSGesuchstellerContainer';
import {EbeguWebGesuch} from '../../gesuch.module';
import BerechnungsManager from '../../service/berechnungsManager';
import GesuchModelManager from '../../service/gesuchModelManager';
import WizardStepManager from '../../service/wizardStepManager';

describe('finanzielleSituationResultateView', function () {

    let gesuchModelManager: GesuchModelManager;
    let berechnungsManager: BerechnungsManager;

    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    let component: any;
    let scope: angular.IScope;
    let $componentController: angular.IComponentControllerService;

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        $componentController = $injector.get('$componentController');
        gesuchModelManager = $injector.get('GesuchModelManager');
        let wizardStepManager: WizardStepManager = $injector.get('WizardStepManager');
        spyOn(wizardStepManager, 'updateWizardStepStatus').and.returnValue({});
        berechnungsManager = $injector.get('BerechnungsManager');
        let $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
    }));

    beforeEach(function () {
        gesuchModelManager.initGesuch(false, TSEingangsart.PAPIER);
        gesuchModelManager.getGesuch().familiensituationContainer = new TSFamiliensituationContainer();
        gesuchModelManager.getGesuch().familiensituationContainer.familiensituationJA = new TSFamiliensituation();
        gesuchModelManager.getGesuch().gesuchsteller1 = new TSGesuchstellerContainer(new TSGesuchsteller());
    });

    it('should be defined', function () {
        spyOn(berechnungsManager, 'calculateFinanzielleSituation').and.returnValue({});
        let bindings: {};
        component = $componentController('finanzielleSituationResultateView', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });
});
