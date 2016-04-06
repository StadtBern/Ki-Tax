import {IHttpBackendService} from 'angular';
import ApplicationPropertyRS from './applicationPropertyRS.rest';
import TSApplicationProperty from '../../models/TSApplicationProperty';

describe('applicationPropertyRS', function () {

    let applicationPropertyRS: ApplicationPropertyRS;
    let $httpBackend: IHttpBackendService;
    let REST_API: string;
    let testName: string = 'myTestName';

    let mockApplicationProperty = {
        name: testName,
        value: 'myTestValue'
    };

    beforeEach(angular.mock.module('ebeguWeb.admin'));

    beforeEach(angular.mock.inject(function (_applicationPropertyRS_: ApplicationPropertyRS, _$httpBackend_: IHttpBackendService, _REST_API_: string) {
        applicationPropertyRS = _applicationPropertyRS_;
        $httpBackend = _$httpBackend_;
        REST_API = _REST_API_;
    }));

    // set the mock response
    beforeEach(function () {
        $httpBackend.when('GET', REST_API + 'application-properties/' + testName).respond(mockApplicationProperty);
        $httpBackend.when('GET', REST_API + 'application-properties/').respond([mockApplicationProperty]);
        $httpBackend.when('DELETE', REST_API + 'application-properties/' + testName).respond(200, '');
        $httpBackend.when('POST', REST_API + 'application-properties/' + testName)
            .respond(201, mockApplicationProperty, {Location: 'http://localhost:8080/ebegu/api/v1/application-properties/test2'});

    });

    describe('Public API', function () {
        it('should include a getByName() function', function () {
            expect(applicationPropertyRS.getByName).toBeDefined();
        });
        it('should include a create() function', function () {
            expect(applicationPropertyRS.create).toBeDefined();
        });

        it('should include a remove() function', function () {
            expect(applicationPropertyRS.remove).toBeDefined();
        });

        it('should include a getAllApplicationProperties() function', function () {
            expect(applicationPropertyRS.getAllApplicationProperties()).toBeDefined();
        });

    });

    describe('API Usage', function () {
        describe('getByName', function () {

            it('should fetch property with given name', function () {
                $httpBackend.expectGET(REST_API + 'application-properties/' + testName);
                let promise = applicationPropertyRS.getByName(testName);
                let property: TSApplicationProperty = undefined;

                promise.then(function (data) {
                    property = data;
                });
                $httpBackend.flush();
                expect(property).toEqual(mockApplicationProperty);

            });

        });

        describe('create', function () {

            it('should create property with name and value', function () {
                $httpBackend.expectPOST(REST_API + 'application-properties/' + testName, mockApplicationProperty.value);
                let promise = applicationPropertyRS.create(mockApplicationProperty.name, mockApplicationProperty.value);
                let property: TSApplicationProperty = undefined;

                promise.then(function (response) {
                    property = response.data;
                });
                $httpBackend.flush();
                expect(property).toEqual(mockApplicationProperty);

            });
        });

        describe('getAllApplicationProperties', function () {

            it('should fetch a list of all properties', function () {
                $httpBackend.expectGET(REST_API + 'application-properties/');
                let promise = applicationPropertyRS.getAllApplicationProperties();
                let list: TSApplicationProperty[] = undefined;

                promise.then(function (data) {
                    list = data;
                });
                $httpBackend.flush();
                expect(list).toEqual([mockApplicationProperty]);

            });
        });

        describe('remove', function () {

            it('should remove a property', function () {
                $httpBackend.expectDELETE(REST_API + 'application-properties/' + testName);
                let promise = applicationPropertyRS.remove(testName);
                let status: string = undefined;

                promise.then(function (response) {
                    status = response.status;

                });
                $httpBackend.flush();
                expect(200).toEqual(status);

            });
        });

    });

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });
});
