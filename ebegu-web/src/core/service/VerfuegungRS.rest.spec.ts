import IInjectorService = angular.auto.IInjectorService;
import {IHttpBackendService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {EbeguWebCore} from '../core.module';
import TSKindContainer from '../../models/TSKindContainer';
import VerfuegungRS from './verfuegungRS.rest';
import TSKind from '../../models/TSKind';
import TSVerfuegung from '../../models/TSVerfuegung';
import TestDataUtil from '../../utils/TestDataUtil';

describe('VerfuegungRS', function () {

    let verfuegungRS: VerfuegungRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockKindContainerListRest: Array<any> = [];
    let mockKind: TSKindContainer;
    let gesuchId: string = '1234567789';
    let betreuungId: string = '321123';


    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        verfuegungRS = $injector.get('VerfuegungRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));

    beforeEach(() => {
        let kindGS: TSKind = new TSKind('Pedro', 'Bern');
        TestDataUtil.setAbstractFieldsUndefined(kindGS);
        let kindJA: TSKind = new TSKind('Pedro', 'Bern');
        TestDataUtil.setAbstractFieldsUndefined(kindJA);
        mockKind = new TSKindContainer(kindGS, kindJA, []);
        TestDataUtil.setAbstractFieldsUndefined(mockKind);
        mockKind.id = '2afc9d9a-957e-4550-9a22-97624a1d8feb';
        mockKindContainerListRest = ebeguRestUtil.kindContainerToRestObject({}, mockKind);
    });

    describe('Public API', function () {
        it('check URI', function () {
            expect(verfuegungRS.serviceURL).toContain('verfuegung');
        });
        it('check Service name', function () {
            expect(verfuegungRS.getServiceName()).toBe('VerfuegungRS');
        });
        it('should include a findKind() function', function () {
            expect(verfuegungRS.calculateVerfuegung).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('calculate', () => {
            it('should return all KindContainer', () => {
                $httpBackend.expectGET(verfuegungRS.serviceURL + '/calculate/' + gesuchId).respond(mockKindContainerListRest);

                let foundKind: Array<TSKindContainer>;
                verfuegungRS.calculateVerfuegung(gesuchId).then((result) => {
                    foundKind = result;
                });
                $httpBackend.flush();
                expect(foundKind).toBeDefined();
                expect(foundKind.length).toBe(1);
                expect(foundKind[0]).toEqual(mockKind);
            });
        });
        describe('saveVerfuegung', () => {
            it('should save the given Verfuegung', () => {
                let verfuegung: TSVerfuegung = TestDataUtil.createVerfuegung();
                $httpBackend.expectPUT(verfuegungRS.serviceURL + '/' + gesuchId + '/' + betreuungId + '/false').respond(ebeguRestUtil.verfuegungToRestObject({}, verfuegung));
                $httpBackend.expectGET('/ebegu/api/v1/wizard-steps/' + gesuchId).respond({});

                let savedVerfuegung: TSVerfuegung;
                verfuegungRS.saveVerfuegung(verfuegung, gesuchId, betreuungId, false).then((result) => {
                    savedVerfuegung = result;
                });
                $httpBackend.flush();
                expect(savedVerfuegung).toBeDefined();
                expect(savedVerfuegung).toEqual(verfuegung);
            });
        });
    });

});
