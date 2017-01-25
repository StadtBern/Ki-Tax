import '../../bootstrap.ts';
import 'angular-mocks';
import {EbeguWebCore} from '../core.module';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpBackendService, IQService} from 'angular';
import TSBetreuung from '../../models/TSBetreuung';
import IInjectorService = angular.auto.IInjectorService;
import BetreuungRS from './betreuungRS.rest';
import {TSBetreuungsstatus} from '../../models/enums/TSBetreuungsstatus';
import TestDataUtil from '../../utils/TestDataUtil';
import WizardStepManager from '../../gesuch/service/wizardStepManager';


describe('betreuungRS', function () {

    let betreuungRS: BetreuungRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockBetreuung: TSBetreuung;
    let wizardStepManager: WizardStepManager;
    let mockBetreuungRest: any;
    let kindId: string;
    let gesuchId: string;
    let $q: IQService;


    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
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
