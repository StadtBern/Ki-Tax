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

import WizardStepManager from '../../gesuch/service/wizardStepManager';
import {TSBetreuungsstatus} from '../../models/enums/TSBetreuungsstatus';
import TSBetreuung from '../../models/TSBetreuung';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TestDataUtil from '../../utils/TestDataUtil';
import {EbeguWebCore} from '../core.module';
import BetreuungRS from './betreuungRS.rest';

describe('betreuungRS', function () {

    let betreuungRS: BetreuungRS;
    let $httpBackend: angular.IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockBetreuung: TSBetreuung;
    let wizardStepManager: WizardStepManager;
    let mockBetreuungRest: any;
    let kindId: string;
    let gesuchId: string;
    let $q: angular.IQService;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        betreuungRS = $injector.get('BetreuungRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
        wizardStepManager = $injector.get('WizardStepManager');
        $q = $injector.get('$q');
        spyOn(wizardStepManager, 'findStepsFromGesuch').and.returnValue($q.when({}));
    }));

    beforeEach(() => {
        kindId = '2afc9d9a-957e-4550-9a22-97624a000feb';
        gesuchId = '2afc9d9a-957e-4550-9a22-97624a000a12';
        mockBetreuung = new TSBetreuung(undefined, TSBetreuungsstatus.AUSSTEHEND, []);
        TestDataUtil.setAbstractFieldsUndefined(mockBetreuung);
        mockBetreuungRest = ebeguRestUtil.betreuungToRestObject({}, mockBetreuung);

        $httpBackend.whenGET(betreuungRS.serviceURL + '/' + encodeURIComponent(mockBetreuung.id)).respond(mockBetreuungRest);
    });

    describe('Public API', function () {
        it('check URI', function () {
            expect(betreuungRS.serviceURL).toContain('betreuungen');
        });
        it('check Service name', function () {
            expect(betreuungRS.getServiceName()).toBe('BetreuungRS');
        });
        it('should include a findBetreuung() function', function () {
            expect(betreuungRS.findBetreuung).toBeDefined();
        });
        it('should include a saveBetreuung() function', function () {
            expect(betreuungRS.saveBetreuung).toBeDefined();
        });
        it('should include a removeBetreuung() function', function () {
            expect(betreuungRS.removeBetreuung).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('findBetreuung', () => {
            it('should return the Betreuung by id', () => {
                $httpBackend.expectGET(betreuungRS.serviceURL + '/' + mockBetreuung.id).respond(mockBetreuungRest);

                let foundBetreuung: TSBetreuung;
                betreuungRS.findBetreuung(mockBetreuung.id).then((result) => {
                    foundBetreuung = result;
                });
                $httpBackend.flush();
                expect(foundBetreuung).toBeDefined();
                expect(foundBetreuung).toEqual(mockBetreuung);
            });

        });
        describe('createBetreuung', () => {
            it('should create a Betreuung', () => {
                let createdBetreuung: TSBetreuung;
                $httpBackend.expectPUT(betreuungRS.serviceURL + '/betreuung/' + kindId + '/false', mockBetreuungRest).respond(mockBetreuungRest);

                betreuungRS.saveBetreuung(mockBetreuung, kindId, gesuchId, false)
                    .then((result) => {
                        createdBetreuung = result;
                    });
                $httpBackend.flush();
                expect(wizardStepManager.findStepsFromGesuch).toHaveBeenCalledWith(gesuchId);
                expect(createdBetreuung).toBeDefined();
                expect(createdBetreuung).toEqual(mockBetreuung);
            });
        });
        describe('removeBetreuung', () => {
            it('should remove a Betreuung', () => {
                $httpBackend.expectDELETE(betreuungRS.serviceURL + '/' + encodeURIComponent(mockBetreuung.id))
                    .respond(200);

                let deleteResult: any;
                betreuungRS.removeBetreuung(mockBetreuung.id, gesuchId)
                    .then((result) => {
                        deleteResult = result;
                    });
                $httpBackend.flush();
                expect(wizardStepManager.findStepsFromGesuch).toHaveBeenCalledWith(gesuchId);
                expect(deleteResult).toBeDefined();
                expect(deleteResult.status).toEqual(200);
            });
        });
    });

});
