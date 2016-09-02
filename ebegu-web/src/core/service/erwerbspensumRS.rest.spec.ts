import {IHttpBackendService, IQService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {EbeguWebCore} from '../core.module';
import ErwerbspensumRS from './erwerbspensumRS.rest';
import TSErwerbspensumContainer from '../../models/TSErwerbspensumContainer';
import TSErwerbspensum from '../../models/TSErwerbspensum';
import TestDataUtil from '../../utils/TestDataUtil';
import IInjectorService = angular.auto.IInjectorService;
import moment = require('moment');
import WizardStepManager from '../../gesuch/service/wizardStepManager';

describe('ErwerbspensumRS', function () {

    let erwerbspensumRS: ErwerbspensumRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockErwerbspensum: TSErwerbspensumContainer;
    let mockErwerbspensumRS: any;
    let gesuchId: string;
    let gesuchstellerId: string;
    let $q: IQService;
    let wizardStepManager: WizardStepManager;


    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
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
            let  changedEwp : TSErwerbspensum = TestDataUtil.createErwerbspensum();
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
