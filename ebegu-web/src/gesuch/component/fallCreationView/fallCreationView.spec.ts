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

import {EbeguWebCore} from '../../../core/core.module';
import {FallCreationViewController} from './fallCreationView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IQService, IScope} from 'angular';
import {IStateService} from 'angular-ui-router';
import TestDataUtil from '../../../utils/TestDataUtil';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import TSGesuch from '../../../models/TSGesuch';
import {TSAntragTyp} from '../../../models/enums/TSAntragTyp';

describe('fallCreationView', function () {

    let fallCreationview: FallCreationViewController;
    let gesuchModelManager: GesuchModelManager;
    let $state: IStateService;
    let $q: IQService;
    let $rootScope: IScope;
    let form: any;
    let gesuch: TSGesuch;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        TestDataUtil.mockLazyGesuchModelManagerHttpCalls($injector.get('$httpBackend'));
        $state = $injector.get('$state');
        $q = $injector.get('$q');
        $rootScope = $injector.get('$rootScope');
        form = {};
        form.$valid = true;
        form.$dirty = true;
        fallCreationview = new FallCreationViewController(gesuchModelManager, $injector.get('BerechnungsManager'),
            $injector.get('ErrorService'), $injector.get('$stateParams'), $injector.get('WizardStepManager'),
            $injector.get('$translate'), $q, $rootScope, $injector.get('AuthServiceRS'), $injector.get('GesuchsperiodeRS'),
            $injector.get('$timeout'));
        fallCreationview.form = form;
        spyOn(fallCreationview, 'isGesuchValid').and.callFake(function () {
            return form.$valid;
        });
        gesuch = new TSGesuch();
        gesuch.typ = TSAntragTyp.ERSTGESUCH;
    }));

    describe('nextStep', () => {
        it('submitted but rejected -> it does not go to the next step', () => {
            spyOn($state, 'go');
            let reject = $q.reject({}).catch( () => {
                //need to catch rejected promise
            });
            spyOn(gesuchModelManager, 'saveGesuchAndFall').and.returnValue(reject);
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(new TSGesuch());
            fallCreationview.save();
            $rootScope.$apply();
            expect(gesuchModelManager.saveGesuchAndFall).toHaveBeenCalled();
            expect($state.go).not.toHaveBeenCalled();
        });
        it('should submit the form and go to the next page', () => {
            spyOn($state, 'go');
            spyOn(gesuchModelManager, 'saveGesuchAndFall').and.returnValue($q.when({}));
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(new TSGesuch());
            fallCreationview.save();
            $rootScope.$apply();
            expect(gesuchModelManager.saveGesuchAndFall).toHaveBeenCalled();
        });
        it('should not submit the form and not go to the next page because form is invalid', () => {
            spyOn($state, 'go');
            spyOn(gesuchModelManager, 'saveGesuchAndFall');
            form.$valid = false;
            fallCreationview.save();
            expect(gesuchModelManager.saveGesuchAndFall).not.toHaveBeenCalled();
        });
    });
    describe('getTitle', () => {
        it('should return Änderung Ihrer Daten', () => {
            spyOn(gesuchModelManager, 'isGesuch').and.returnValue(false);
            expect(fallCreationview.getTitle()).toBe('Änderung Ihrer Daten');
        });
        it('should return Ki-Tax – Erstgesuch der Periode', () => {
            let gesuchsperiode: TSGesuchsperiode = TestDataUtil.createGesuchsperiode20162017();
            spyOn(gesuchModelManager, 'getGesuchsperiode').and.returnValue(gesuchsperiode);
            spyOn(gesuchModelManager, 'isGesuch').and.returnValue(true);
            spyOn(gesuchModelManager, 'isGesuchSaved').and.returnValue(true);
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);
            expect(fallCreationview.getTitle()).toBe('Ki-Tax – Erstgesuch der Periode 2016/17');
        });
        it('should return Ki-Tax – Erstgesuch', () => {
            spyOn(gesuchModelManager, 'isGesuch').and.returnValue(true);
            spyOn(gesuchModelManager, 'isGesuchSaved').and.returnValue(false);
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);
            expect(fallCreationview.getTitle()).toBe('Ki-Tax – Erstgesuch');
        });
    });
});
