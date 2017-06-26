import {TestFaelleRS} from './testFaelleRS.rest';
import {IHttpBackendService} from 'angular';
import {EbeguWebAdmin} from '../admin.module';

describe('TestFaelleRS', function () {

    let testFaelleRS: TestFaelleRS;
    let $httpBackend: IHttpBackendService;


    beforeEach(angular.mock.module(EbeguWebAdmin.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        testFaelleRS = $injector.get('TestFaelleRS');
        $httpBackend = $injector.get('$httpBackend');
    }));

    describe('Public API', function () {
        it('check URI', function () {
            expect(testFaelleRS.serviceURL).toContain('testfaelle');
        });
        it('check Service name', function () {
            expect(testFaelleRS.getServiceName()).toBe('TestFaelleRS');
        });
        it('should include a createTestFall() function', function () {
            expect(testFaelleRS.createTestFall).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('createTestFall', () => {
            it('should call createTestFall', () => {
                $httpBackend.expectGET(testFaelleRS.serviceURL + '/testfall/' + encodeURIComponent('1') + '/null/false/false').respond({});
                testFaelleRS.createTestFall('1', null, false, false);
                $httpBackend.flush();
            });
        });
    });
});
