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

import EwkRS from '../../../core/service/ewkRS.rest';
import {TSAntragTyp} from '../../../models/enums/TSAntragTyp';
import {TSEingangsart} from '../../../models/enums/TSEingangsart';
import TSGesuchsteller from '../../../models/TSGesuchsteller';
import TSGesuchstellerContainer from '../../../models/TSGesuchstellerContainer';
import {IStammdatenStateParams} from '../../gesuch.route';
import GesuchModelManager from '../../service/gesuchModelManager';
import WizardStepManager from '../../service/wizardStepManager';
import {StammdatenViewController} from './stammdatenView';

describe('stammdatenView', function () {

    let gesuchModelManager: GesuchModelManager;
    let stammdatenViewController: StammdatenViewController;
    let $stateParams: IStammdatenStateParams;
    let $q: angular.IQService;
    let $rootScope: any;
    let $scope: angular.IScope;
    let ewkRS: EwkRS;
    let $timeout: angular.ITimeoutService;

    beforeEach(angular.mock.module('ebeguWeb.gesuch'));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        let wizardStepManager: WizardStepManager = $injector.get('WizardStepManager');
        spyOn(wizardStepManager, 'updateWizardStepStatus').and.returnValue({});
        $stateParams = $injector.get('$stateParams');
        $stateParams.gesuchstellerNumber = '1';
        gesuchModelManager.initGesuch(false, TSEingangsart.PAPIER);
        $q = $injector.get('$q');
        $rootScope = $injector.get('$rootScope');
        ewkRS = $injector.get('EwkRS');
        $scope = $rootScope.$new();
        $timeout = $injector.get('$timeout');
        stammdatenViewController = new StammdatenViewController($stateParams, undefined, gesuchModelManager,
            undefined, undefined, wizardStepManager, $injector.get('CONSTANTS'), $q, $scope, $injector.get('$translate'),
            undefined, $rootScope, ewkRS, $timeout);
    }));

    describe('disableWohnadresseFor2GS', function () {
        it('should return false for 1GS und Erstgesuch', function () {
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(1);
            expect(stammdatenViewController.disableWohnadresseFor2GS()).toBe(false);
        });
        it('should return false for new 2GS und Mutation', function () {
            stammdatenViewController.gesuchstellerNumber = 2;
            gesuchModelManager.setStammdatenToWorkWith(new TSGesuchstellerContainer(new TSGesuchsteller()));
            gesuchModelManager.getGesuch().typ = TSAntragTyp.MUTATION;
            stammdatenViewController.model = gesuchModelManager.getStammdatenToWorkWith();
            expect(stammdatenViewController.disableWohnadresseFor2GS()).toBe(false);
        });
        it('should return true for old 2GS und Mutation', function () {
            gesuchModelManager.setGesuchstellerNumber(2);
            let gs2 = new TSGesuchstellerContainer(new TSGesuchsteller());
            gs2.vorgaengerId = '123';
            gesuchModelManager.setStammdatenToWorkWith(gs2);
            gesuchModelManager.getGesuch().typ = TSAntragTyp.MUTATION;
            expect(stammdatenViewController.disableWohnadresseFor2GS()).toBe(true);
        });
        it('should return false for 1GS und Erstgesuch', function () {
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(1);
            expect(stammdatenViewController.disableWohnadresseFor2GS()).toBe(false);
        });
        it('should return true for 1GS und Mutation', function () {
            spyOn(gesuchModelManager, 'getGesuchstellerNumber').and.returnValue(1);
            gesuchModelManager.getGesuch().typ = TSAntragTyp.MUTATION;
            expect(stammdatenViewController.disableWohnadresseFor2GS()).toBe(true);
        });
    });

});
