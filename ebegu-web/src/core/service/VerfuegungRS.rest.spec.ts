import IInjectorService = angular.auto.IInjectorService;
import {IHttpBackendService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {EbeguWebCore} from '../core.module';
import TSKindContainer from '../../models/TSKindContainer';
import VerfuegungRS from './verfuegungRS.rest';
import TestDataUtil from '../../utils/TestDataUtil';
import TSKind from '../../models/TSKind';

describe('VerfuegungRS', function () {

    let verfuegungRS: VerfuegungRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockKindContainerListRest: Array<any> = [];
    let mockKind: TSKindContainer;


    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        verfuegungRS = $injector.get('VerfuegungRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));

    beforeEach(() => {
        let kindGS: TSKind = new TSKind('Pedro', 'Bern');
        TestDataUtil.setAbstractFieldsUndefined(kindGS);
        let kindJA: TSKind = new TSKind('Johan', 'Basel');
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
        describe('findKind', () => {
            it('should return all KindContainer', () => {
                let gesuchId: string = '1234567789';
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
    });

});
