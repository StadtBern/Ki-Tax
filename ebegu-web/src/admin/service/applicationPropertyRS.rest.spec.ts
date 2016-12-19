import '../../bootstrap.ts';
import 'angular-mocks';
import '../admin.module';
import {ApplicationPropertyRS} from './applicationPropertyRS.rest';
import TSApplicationProperty from '../../models/TSApplicationProperty';
import {EbeguWebAdmin} from '../admin.module';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;

import IPromise = angular.IPromise;
import IHttpPromise = angular.IHttpPromise;

describe('ApplicationPropertyRS', function () {

    let applicationPropertyRS: ApplicationPropertyRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let REST_API: string;
    let testName: string = 'myTestName';

    let mockApplicationProp = new TSApplicationProperty(testName, 'myTestValue');

    let mockApplicationPropertyRest = {
        name: testName,
        value: 'myTestValue'
    };

    beforeEach(angular.mock.module(EbeguWebAdmin.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        applicationPropertyRS = $injector.get('ApplicationPropertyRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
        REST_API = $injector.get('REST_API');
    }));

    // set the mock response
    beforeEach(function () {
        $httpBackend.when('GET', REST_API + 'application-properties/key/' + testName).respond(mockApplicationPropertyRest);
        $httpBackend.when('GET', REST_API + 'application-properties/').respond([mockApplicationPropertyRest]);
        $httpBackend.when('DELETE', REST_API + 'application-properties/' + testName).respond(200, '');
        $httpBackend.when('POST', REST_API + 'application-properties/' + testName)
            .respond(201, mockApplicationPropertyRest, {Location: 'http://localhost:8080/ebegu/api/v1/application-properties/key/test2'});

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
            expect(applicationPropertyRS.getAllApplicationProperties).toBeDefined();
        });

    });

    describe('API Usage', function () {
        describe('getByName', function () {

            it('should fetch property with given name', function () {
                $httpBackend.expectGET(REST_API + 'application-properties/key/' + testName);
                let promise: IPromise<TSApplicationProperty> = applicationPropertyRS.getByName(testName);
                let property: TSApplicationProperty = undefined;

                promise.then(function (data: any) {
                    property = data;
                });
                $httpBackend.flush();
                expect(property.name).toEqual(mockApplicationProp.name);
                expect(property.value).toEqual(mockApplicationProp.value);

            });

        });

        describe('create', function () {

            it('should create property with name and value', function () {
                $httpBackend.expectPOST(REST_API + 'application-properties/' + testName, mockApplicationPropertyRest.value);
                let promise: IHttpPromise<any> = applicationPropertyRS.create(mockApplicationPropertyRest.name, mockApplicationPropertyRest.value);
                let property: TSApplicationProperty = undefined;

                promise.then(function (response: any) {
                    property = response.data;
                });
                $httpBackend.flush();
                expect(property.name).toEqual(mockApplicationProp.name);
                expect(property.value).toEqual(mockApplicationProp.value);

            });
        });

        describe('getAllApplicationProperties', function () {

            it('should fetch a list of all properties', function () {
                $httpBackend.expectGET(REST_API + 'application-properties/');
                let promise: IPromise<TSApplicationProperty[]> = applicationPropertyRS.getAllApplicationProperties();
                let list: TSApplicationProperty[] = undefined;

                promise.then(function (data: any) {
                    list = data;
                });
                $httpBackend.flush();

                for (var i = 0; i < list.length; i++) {
                    let mockArray = [mockApplicationPropertyRest];
                    expect(list[i].name).toEqual(mockArray[i].name);
                    expect(list[i].value).toEqual(mockArray[i].value);
                }
            });
        });

        describe('remove', function () {

            it('should remove a property', function () {
                $httpBackend.expectDELETE(REST_API + 'application-properties/' + testName);
                let promise = applicationPropertyRS.remove(testName);
                let status: number = undefined;

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
