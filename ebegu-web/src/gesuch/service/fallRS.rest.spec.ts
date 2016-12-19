import {EbeguWebCore} from '../../core/core.module';
import FallRS from './fallRS.rest';
import {IHttpBackendService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSFall from '../../models/TSFall';

describe('fallRS', function () {

    let fallRS: FallRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let REST_API: string;
    let mockFall: TSFall;
    let mockFallRest: any;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        fallRS = $injector.get('FallRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
        REST_API = $injector.get('REST_API');
    }));

    beforeEach(() => {
        mockFall = new TSFall();
        mockFall.fallNummer = 2;
        mockFallRest = ebeguRestUtil.fallToRestObject({}, mockFall);
    });

    describe('Public API', function () {
        it('check URI', function () {
            expect(fallRS.serviceURL).toContain('falle');
        });
        it('check Service name', function () {
            expect(fallRS.getServiceName()).toBe('FallRS');
        });
        it('should include a createFall() function', function () {
            expect(fallRS.createFall).toBeDefined();
        });
        it('should include a findFall() function', function () {
            expect(fallRS.findFall).toBeDefined();
        });
        it('should include a updateFall() function', function () {
            expect(fallRS.updateFall).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('findFall', () => {
            it('should return the Fall by id', () => {
                $httpBackend.expectGET(fallRS.serviceURL + '/id/' + mockFall.id).respond(mockFallRest);

                let foundFall: TSFall;
                fallRS.findFall(mockFall.id).then((result) => {
                    foundFall = result;
                });
                $httpBackend.flush();
                expect(foundFall).toBeDefined();
                expect(foundFall.fallNummer).toEqual(mockFall.fallNummer);
            });
        });
        describe('createFall', () => {
            it('should create an fall', () => {
                let createdFall: TSFall;
                $httpBackend.expectPUT(fallRS.serviceURL, mockFallRest).respond(mockFallRest);

                fallRS.createFall(mockFall).then((result) => {
                    createdFall = result;
                });
                $httpBackend.flush();
                expect(createdFall).toBeDefined();
                expect(createdFall.fallNummer).toEqual(mockFall.fallNummer);
            });
        });
    });
});
