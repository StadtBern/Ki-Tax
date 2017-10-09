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

import '../../../bootstrap.ts';
import 'angular-mocks';
import {EbeguWebGesuch} from '../../gesuch.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import {EinkommensverschlechterungResultateViewController} from './einkommensverschlechterungResultateView';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
import WizardStepManager from '../../service/wizardStepManager';
import TSFinanzModel from '../../../models/TSFinanzModel';
import TSGesuchstellerContainer from '../../../models/TSGesuchstellerContainer';
import TSGesuchsteller from '../../../models/TSGesuchsteller';
import {TSEingangsart} from '../../../models/enums/TSEingangsart';
import {ITimeoutService} from 'angular';
import IStateService = angular.ui.IStateService;
import IScope = angular.IScope;

describe('einkommensverschlechterungResultateView', function () {

    let gesuchModelManager: GesuchModelManager;
    let berechnungsManager: BerechnungsManager;
    let ekvrvc: EinkommensverschlechterungResultateViewController;
    let $state: IStateService;

    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    let component: any;
    let scope: angular.IScope;
    let $componentController: any;
    let stateParams: any;
    let consta: any;
    let errorservice: any;
    let wizardStepManager: WizardStepManager;
    let $rootScope: IScope;
    let $timeout: ITimeoutService;

    beforeEach(angular.mock.inject(function ($injector: any) {
        $componentController = $injector.get('$componentController');
        gesuchModelManager = $injector.get('GesuchModelManager');
        berechnungsManager = $injector.get('BerechnungsManager');
        $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
        let $q = $injector.get('$q');
        stateParams = $injector.get('$stateParams');
        consta = $injector.get('CONSTANTS');
        errorservice = $injector.get('ErrorService');
        wizardStepManager = $injector.get('WizardStepManager');
        $timeout = $injector.get('$timeout');


        spyOn(berechnungsManager, 'calculateFinanzielleSituation').and.returnValue($q.when({}));

    }));

    beforeEach(function () {
        gesuchModelManager.initGesuch(false, TSEingangsart.PAPIER);
        gesuchModelManager.initFamiliensituation();
        gesuchModelManager.getGesuch().gesuchsteller1 = new TSGesuchstellerContainer(new TSGesuchsteller());
        gesuchModelManager.getGesuch().gesuchsteller2 = new TSGesuchstellerContainer(new TSGesuchsteller());

    });

    it('should be defined', function () {
        spyOn(berechnungsManager, 'calculateEinkommensverschlechterung').and.returnValue({});
        let bindings: {};
        component = $componentController('einkommensverschlechterungResultateView', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });

    describe('calculateVeraenderung', () => {
        beforeEach(function () {
            ekvrvc = new EinkommensverschlechterungResultateViewController(stateParams, gesuchModelManager,
                berechnungsManager, errorservice, wizardStepManager, null, $rootScope, null, $timeout);
            ekvrvc.model = new TSFinanzModel(gesuchModelManager.getBasisjahr(), gesuchModelManager.isGesuchsteller2Required(), null, null);
            ekvrvc.model.copyEkvDataFromGesuch(gesuchModelManager.getGesuch());
            ekvrvc.model.copyFinSitDataFromGesuch(gesuchModelManager.getGesuch());

        });
        it('should return + 0.00 %', () => {

            setValues(0, 0);
            expect(ekvrvc.calculateVeraenderung()).toEqual('+ 0.00 %');
        });

        it('should return + 0.00 %', () => {

            setValues(100, 100);
            expect(ekvrvc.calculateVeraenderung()).toEqual('+ 0.00 %');
        });

        it('should return + 100.00 %', () => {

            setValues(100, 200);
            expect(ekvrvc.calculateVeraenderung()).toEqual('+ 100.00 %');
        });

        it('should return - 50.00 %', () => {

            setValues(200, 100);
            expect(ekvrvc.calculateVeraenderung()).toEqual('- 50.00 %');
        });

        it('should return - 90.00 %', () => {

            setValues(200, 20);
            expect(ekvrvc.calculateVeraenderung()).toEqual('- 90.00 %');
        });

        it('should return - 81.20 %', () => {

            setValues(59720, 11230);
            expect(ekvrvc.calculateVeraenderung()).toEqual('- 81.20 %');
        });

        it('should return - 20.01 %', () => {

            setValues(100000, 79990);
            expect(ekvrvc.calculateVeraenderung()).toEqual('- 20.01 %');
        });

        it('should return - 100.00 %', () => {

            setValues(59720, 0);
            expect(ekvrvc.calculateVeraenderung()).toEqual('- 100.00 %');
        });

        it('should return + 100.00 %', () => {

            setValues(0, 59720);
            expect(ekvrvc.calculateVeraenderung()).toEqual('+ 100.00 %');
        });

        function setValues( massgebendesEinkommen_vj: number, massgebendesEinkommen_bj: number) {
            let finsint: TSFinanzielleSituationResultateDTO = new TSFinanzielleSituationResultateDTO();
            finsint.massgebendesEinkVorAbzFamGr = massgebendesEinkommen_bj;

            let finsintvj: TSFinanzielleSituationResultateDTO = new TSFinanzielleSituationResultateDTO();
            finsintvj.massgebendesEinkVorAbzFamGr = massgebendesEinkommen_vj;

            spyOn(ekvrvc, 'getResultate').and.returnValue(finsint);
            ekvrvc.resultatBasisjahr = finsintvj;
        }

    });
});


