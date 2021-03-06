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
import TSErwerbspensum from '../../models/TSErwerbspensum';
import TSErwerbspensumContainer from '../../models/TSErwerbspensumContainer';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TestDataUtil from '../../utils/TestDataUtil';
import {EbeguWebCore} from '../core.module';
import ErwerbspensumRS from './erwerbspensumRS.rest';

describe('ErwerbspensumRS', function () {

    let erwerbspensumRS: ErwerbspensumRS;
    let $httpBackend: angular.IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockErwerbspensum: TSErwerbspensumContainer;
    let mockErwerbspensumRS: any;
    let gesuchId: string;
    let gesuchstellerId: string;
    let $q: angular.IQService;
    let wizardStepManager: WizardStepManager;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        erwerbspensumRS = $injector.get('ErwerbspensumRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
        wizardStepManager = $injector.get('WizardStepManager');
        $q = $injector.get('$q');
        spyOn(wizardStepManager, 'findStepsFromGesuch').and.returnValue($q.when({}));
    }));

    beforeEach(() => {
        mockErwerbspensum = TestDataUtil.createErwerbspensumContainer();
        mockErwerbspensum.id = '2afc9d9a-957e-4550-9a22-97624a1d8feb';
        gesuchstellerId = '2afc9d9a-957e-4550-9a22-97624a1d8fe1';
        gesuchId = '2afc9d9a-957e-4550-9a22-97624a1d8fe2';
        mockErwerbspensumRS = ebeguRestUtil.erwerbspensumContainerToRestObject({}, mockErwerbspensum);
    });

    describe('Public API', function () {
        it('check URI', function () {
            expect(erwerbspensumRS.serviceURL).toContain('erwerbspensen');
        });
        it('check Service name', function () {
            expect(erwerbspensumRS.getServiceName()).toBe('ErwerbspensumRS');
        });
        it('should include a findErwerbspensum() function', function () {
            expect(erwerbspensumRS.findErwerbspensum).toBeDefined();
        });
        it('should include a saveErwerbspensum() function', function () {
            expect(erwerbspensumRS.saveErwerbspensum).toBeDefined();
        });
        it('should include a removeErwerbspensum() function', function () {
            expect(erwerbspensumRS.removeErwerbspensum).toBeDefined();
        });
    });
    describe('API Usage', function () {
        describe('findErwerbspensumContainer', () => {
            it('should return the Erwerbspensumcontainer by id', () => {
                $httpBackend.expectGET(erwerbspensumRS.serviceURL + '/' + mockErwerbspensum.id).respond(mockErwerbspensumRS);

                let ewpContainer: TSErwerbspensumContainer;
                erwerbspensumRS.findErwerbspensum(mockErwerbspensum.id).then((result) => {
                    ewpContainer = result;
                });
                $httpBackend.flush();
                checkFieldValues(ewpContainer);
            });

        });
    });
    describe('createErwerbspensumContainer', () => {
        it('should create a ErwerbspensumContainer', () => {
            let createdEWPContainer: TSErwerbspensumContainer;
            $httpBackend.expectPUT(erwerbspensumRS.serviceURL + '/' + gesuchstellerId + '/' + gesuchId, mockErwerbspensumRS).respond(mockErwerbspensumRS);

            erwerbspensumRS.saveErwerbspensum(mockErwerbspensum, gesuchstellerId, gesuchId)
                .then((result) => {
                    createdEWPContainer = result;
                });
            $httpBackend.flush();
            expect(wizardStepManager.findStepsFromGesuch).toHaveBeenCalledWith(gesuchId);
            checkFieldValues(createdEWPContainer);
        });
    });

    describe('updateErwerbspensumContainer', () => {
        it('should update an ErwerbspensumContainer', () => {
            let changedEwp: TSErwerbspensum = TestDataUtil.createErwerbspensum();
            changedEwp.pensum = 40;
            changedEwp.zuschlagsprozent = 10;
            mockErwerbspensum.erwerbspensumJA = changedEwp;
            mockErwerbspensumRS = ebeguRestUtil.erwerbspensumContainerToRestObject({}, mockErwerbspensum);
            let updatedErwerbspensumContainerContainer: TSErwerbspensumContainer;
            $httpBackend.expectPUT(erwerbspensumRS.serviceURL + '/' + gesuchstellerId + '/' + gesuchId, mockErwerbspensumRS).respond(mockErwerbspensumRS);

            erwerbspensumRS.saveErwerbspensum(mockErwerbspensum, gesuchstellerId, gesuchId)
                .then((result) => {
                    updatedErwerbspensumContainerContainer = result;
                });
            $httpBackend.flush();
            expect(wizardStepManager.findStepsFromGesuch).toHaveBeenCalledWith(gesuchId);
            checkFieldValues(updatedErwerbspensumContainerContainer);
        });
    });

    describe('removeErwerbspensumContainer', () => {
        it('should remove a ErwerbspensumContainer', () => {
            $httpBackend.expectDELETE(erwerbspensumRS.serviceURL + '/gesuchId/' + gesuchId + '/erwPenId/' + encodeURIComponent(mockErwerbspensum.id))
                .respond(200);

            let deleteResult: any;
            erwerbspensumRS.removeErwerbspensum(mockErwerbspensum.id, gesuchId)
                .then((result) => {
                    deleteResult = result;
                });
            $httpBackend.flush();
            expect(wizardStepManager.findStepsFromGesuch).toHaveBeenCalledWith(gesuchId);
            expect(deleteResult).toBeDefined();
            expect(deleteResult.status).toEqual(200);
        });
    });

    function checkFieldValues(foundEWPCont: TSErwerbspensumContainer) {
        expect(foundEWPCont).toBeDefined();
        expect(foundEWPCont.erwerbspensumJA).toBeDefined();
        TestDataUtil.checkGueltigkeitAndSetIfSame(foundEWPCont.erwerbspensumJA, mockErwerbspensum.erwerbspensumJA);
        expect(foundEWPCont.erwerbspensumJA).toEqual(mockErwerbspensum.erwerbspensumJA);
        expect(foundEWPCont.erwerbspensumGS).toBeDefined();
        TestDataUtil.checkGueltigkeitAndSetIfSame(foundEWPCont.erwerbspensumGS, mockErwerbspensum.erwerbspensumGS);
        expect(foundEWPCont.erwerbspensumGS).toEqual(mockErwerbspensum.erwerbspensumGS);
        expect(foundEWPCont).toEqual(mockErwerbspensum);
    }

});
