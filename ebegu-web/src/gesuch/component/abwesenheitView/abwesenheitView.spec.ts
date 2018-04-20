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

import {AbwesenheitViewController, KindBetreuungUI} from './abwesenheitView';
import {EbeguWebCore} from '../../../core/core.module';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import ErrorService from '../../../core/errors/service/ErrorService';
import TSBetreuung from '../../../models/TSBetreuung';
import TSInstitution from '../../../models/TSInstitution';
import TSInstitutionStammdaten from '../../../models/TSInstitutionStammdaten';
import TSKind from '../../../models/TSKind';
import TSKindContainer from '../../../models/TSKindContainer';
import BerechnungsManager from '../../service/berechnungsManager';
import GesuchModelManager from '../../service/gesuchModelManager';
import WizardStepManager from '../../service/wizardStepManager';

describe('abwesenheitView', function () {

    let abwesenheitController: AbwesenheitViewController;
    let gesuchModelManager: GesuchModelManager;
    let wizardStepManager: WizardStepManager;
    let berechnungsManager: BerechnungsManager;
    let errorService: ErrorService;
    let $translate: angular.translate.ITranslateService;
    let dialog: DvDialog;
    let $q: angular.IQService;
    let $scope: angular.IScope;
    let $timeout: angular.ITimeoutService;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        wizardStepManager = $injector.get('WizardStepManager');
        spyOn(wizardStepManager, 'updateWizardStepStatus').and.returnValue({});
        berechnungsManager = $injector.get('BerechnungsManager');
        errorService = $injector.get('ErrorService');
        $translate = $injector.get('$translate');
        dialog = $injector.get('DvDialog');
        $q = $injector.get('$q');
        $scope = $injector.get('$rootScope');
        $timeout = $injector.get('$timeout');
    }));

    describe('getNameFromBetroffene', function () {
        beforeEach(function () {
            abwesenheitController = new AbwesenheitViewController(gesuchModelManager, berechnungsManager,
                wizardStepManager, dialog, $translate, $q, errorService, $scope, $timeout);
        });
        it('should return empty string for undefined kindBetreuung', function () {
            let kindBetreuung: KindBetreuungUI = new KindBetreuungUI();
            expect(abwesenheitController.getTextForBetreuungDDL(kindBetreuung)).toBe('');
        });
        it('should return empty string for empty data', function () {
            let kindBetreuung: KindBetreuungUI = new KindBetreuungUI();
            expect(abwesenheitController.getTextForBetreuungDDL(kindBetreuung)).toBe('');
        });
        it('should return Name of KindBetreuung', function () {
            let kindBetreuung: KindBetreuungUI = new KindBetreuungUI();
            let betreuung = new TSBetreuung();
            let institutionStammdaten = new TSInstitutionStammdaten();
            let ins = new TSInstitution();
            ins.name = 'InstitutionTest';
            institutionStammdaten.institution = ins;
            betreuung.institutionStammdaten = institutionStammdaten;
            kindBetreuung.betreuung = betreuung;

            let kind = new TSKindContainer();
            let kindJA = new TSKind();
            kindJA.vorname = 'Pedrito';
            kindJA.nachname = 'Contreras';
            kind.kindJA = kindJA;
            kindBetreuung.kind = kind;

            expect(abwesenheitController.getTextForBetreuungDDL(kindBetreuung)).toBe('Pedrito Contreras - InstitutionTest');
        });
    });
    describe('createAbwesenheit', function () {
        it('should return empty string for empty data', function () {
            expect(abwesenheitController.getAbwesenheiten().length).toBe(0);
            abwesenheitController.createAbwesenheit();
            expect(abwesenheitController.getAbwesenheiten().length).toBe(1);
            expect(abwesenheitController.getAbwesenheiten()[0]).toBeDefined();
            expect(abwesenheitController.getAbwesenheiten()[0].kindBetreuung).toBeUndefined();
            expect(abwesenheitController.getAbwesenheiten()[0].abwesenheit).toBeDefined();
        });
    });
});
