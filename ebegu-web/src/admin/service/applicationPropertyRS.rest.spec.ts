/// <reference path="../../../typings/browser.d.ts" />
describe('applicationPropertyRS', function () {

    var applicationPropertyRS: ebeguWeb.services.IApplicationPropertyRS;
    var $httpBackend: angular.IHttpBackendService;
    var REST_API: string;
    var testName: string = 'myTestName';

    var mockApplicationProperty = {
        name: testName,
        value: 'myTestValue'
    };

    beforeEach(angular.mock.module('ebeguWeb.admin'));

    beforeEach(angular.mock.inject(function (_applicationPropertyRS_, _$httpBackend_, _REST_API_) {
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
                var promise = applicationPropertyRS.getByName(testName);
                var property = null;

                promise.then(function (response) {
                    property = response.data;

                });
                $httpBackend.flush();
                expect(property).toEqual(mockApplicationProperty);

            });

        });

        describe('create', function () {

            it('should create property with name and value', function () {
                $httpBackend.expectPOST(REST_API + 'application-properties/' + testName, mockApplicationProperty.value);
                var promise = applicationPropertyRS.create(mockApplicationProperty.name, mockApplicationProperty.value);
                var property = null;

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
                var promise = applicationPropertyRS.getAllApplicationProperties();
                var list = null;

                promise.then(function (response) {
                    list = response.data;

                });
                $httpBackend.flush();
                expect(list).toEqual([mockApplicationProperty]);

            });
        });

        describe('remove', function () {

            it('should remove a property', function () {
                $httpBackend.expectDELETE(REST_API + 'application-properties/' + testName);
                var promise = applicationPropertyRS.remove(testName);
                var status = null;

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
