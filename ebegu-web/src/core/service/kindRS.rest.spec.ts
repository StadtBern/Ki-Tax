import KindRS from './kindRS.rest';
import IInjectorService = angular.auto.IInjectorService;
import {IHttpBackendService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {EbeguWebCore} from '../core.module';
import TSKindContainer from '../../models/TSKindContainer';
import TSKind from '../../models/TSKind';
import TSAbstractEntity from '../../models/TSAbstractEntity';

describe('KindRS', function () {

    let kindRS: KindRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockKind: TSKindContainer;
    let mockKindRest: any;


    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        kindRS = $injector.get('KindRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));

    beforeEach(() => {
        let kindGS: TSKind = new TSKind('Pedro', 'Bern');
        setAbstractFieldsUndefined(kindGS);
        let kindJA: TSKind = new TSKind('Johan', 'Basel');
        setAbstractFieldsUndefined(kindJA);
        mockKind = new TSKindContainer(kindGS, kindJA);
        setAbstractFieldsUndefined(mockKind);
        mockKind.id = '2afc9d9a-957e-4550-9a22-97624a1d8feb';
        mockKindRest = ebeguRestUtil.kindContainerToRestObject({}, mockKind);
    });

    describe('Public API', function () {
        it('check URI', function () {
            expect(kindRS.serviceURL).toContain('kinder');
        });
        it('check Service name', function () {
            expect(kindRS.getServiceName()).toBe('KindRS');
        });
        it('should include a findKind() function', function () {
            expect(kindRS.findKind).toBeDefined();
        });
        it('should include a createKind() function', function () {
            expect(kindRS.createKind).toBeDefined();
        });
        it('should include a updateKind() function', function () {
            expect(kindRS.updateKind).toBeDefined();
        });
        it('should include a removeKind() function', function () {
            expect(kindRS.removeKind).toBeDefined();
        });
    });
    describe('API Usage', function () {
        describe('findKind', () => {
            it('should return the Kind by id', () => {
                $httpBackend.expectGET(kindRS.serviceURL + '/' + mockKind.id).respond(mockKindRest);

                let foundKind: TSKindContainer;
                kindRS.findKind(mockKind.id).then((result) => {
                    foundKind = result;
                });
                $httpBackend.flush();
                checkFieldValues(foundKind);
            });

        });
    });
    describe('createKind', () => {
        it('should create a Kind', () => {
            let createdKind: TSKindContainer;
            $httpBackend.expectPUT(kindRS.serviceURL, mockKindRest).respond(mockKindRest);

            kindRS.createKind(mockKind)
                .then((result) => {
                    createdKind = result;
                });
            $httpBackend.flush();
            checkFieldValues(createdKind);
        });
    });

    describe('updateKind', () => {
        it('should update a Kind', () => {
            let kindJA2: TSKind = new TSKind('Johan', 'Basel');
            setAbstractFieldsUndefined(kindJA2);
            mockKind.kindJA = kindJA2;
            mockKindRest = ebeguRestUtil.kindContainerToRestObject({}, mockKind);
            let updatedKindContainer: TSKindContainer;
            $httpBackend.expectPUT(kindRS.serviceURL, mockKindRest).respond(mockKindRest);

            kindRS.updateKind(mockKind)
                .then((result) => {
                    updatedKindContainer = result;
                });
            $httpBackend.flush();
            checkFieldValues(updatedKindContainer);
        });
    });

    describe('removeKind', () => {
        it('should remove a Kind', () => {
            $httpBackend.expectDELETE(kindRS.serviceURL + '/' + encodeURIComponent(mockKind.id))
                .respond(200);

            let deleteResult: any;
            kindRS.removeKind(mockKind.id)
                .then((result) => {
                    deleteResult = result;
                });
            $httpBackend.flush();
            expect(deleteResult).toBeDefined();
            expect(deleteResult.status).toEqual(200);
        });
    });


    function checkFieldValues(foundKind: TSKindContainer) {
        expect(foundKind).toBeDefined();
        expect(foundKind).toEqual(mockKind);
        expect(foundKind.kindGS).toBeDefined();
        expect(foundKind.kindGS).toEqual(mockKind.kindGS);
        expect(foundKind.kindJA).toBeDefined();
        expect(foundKind.kindJA).toEqual(mockKind.kindJA);
    }

    function setAbstractFieldsUndefined(abstractEntity: TSAbstractEntity) {
        abstractEntity.id = undefined;
        abstractEntity.timestampErstellt = undefined;
        abstractEntity.timestampMutiert = undefined;
    }
});
