import '../../../bootstrap.ts';
import 'angular-mocks';
import HttpErrorInterceptor from './HttpErrorInterceptor';
import TSExceptionReport from '../../../models/TSExceptionReport';
import IRootScopeService = angular.IRootScopeService;
import IQService = angular.IQService;
import IDeferred = angular.IDeferred;

describe('httpErrorInterceptor', function () {

    let httpErrorInterceptor: HttpErrorInterceptor, $rootScope: IRootScopeService, $q: IQService;

    let validationResponse: any = {
        status: 400,
        data: {
            parameterViolations: [],
            classViolations: [],
            fieldViolations: [],
            propertyViolations: [{
                constraintType: 'PARAMETER',
                path: 'markAsRead.arg1',
                message: 'Die Länge des Feldes muss zwischen 36 und 36 sein',
                value: '8a146418-ab12-456f-9b17-aad6990f51'
            }],
            returnValueViolations: []
        }
    };
    let exceptionReportResponse: any = {
        status: 500,
        data: {
            errorCodeEnum: 'ERROR_ENTITY_NOT_FOUND',
            exceptionName: 'EbeguRuntimeException',
            methodName: 'doTest',
            stackTrace: null,
            translatedMessage: '',
            customMessage: 'test',
            argumentList: null,
        }
    };

    beforeEach(angular.mock.module('dvbAngular.errors'));


    beforeEach(angular.mock.inject(function ($injector: any) {
        httpErrorInterceptor = $injector.get('HttpErrorInterceptor');
        $rootScope = $injector.get('$rootScope');
        $q = $injector.get('$q');
    }));

    describe('Public API', function () {
        it('should include a responseError() function', function () {
            expect(httpErrorInterceptor.responseError).toBeDefined();
        });
    });

    describe('API usage', function () {
        var deferred: IDeferred<any>, successHandler: any, errorHandler: any;
        beforeEach(function () {
            deferred = $q.defer();
            successHandler = jasmine.createSpy('successHandler');
            errorHandler = jasmine.createSpy('errorHanlder');
            deferred.promise.then(successHandler, errorHandler);
        });

        it('should reject the response with a validation report', function () {
            httpErrorInterceptor.responseError(validationResponse).then(function () {
                deferred.resolve();
            }, function (errors) {
                deferred.reject(errors);
            });


            let errors: Array<TSExceptionReport> = [(TSExceptionReport.createFromViolation('PARAMETER',
                'Die Länge des Feldes muss zwischen 36 und 36 sein', 'markAsRead.arg1', '8a146418-ab12-456f-9b17-aad6990f51'))];
            $rootScope.$digest();
            expect(errorHandler).toHaveBeenCalledWith(errors);
        });

        it('should reject the response containing an exceptionReport', function () {
            httpErrorInterceptor.responseError(exceptionReportResponse).then(function () {
                deferred.resolve();
            }, function (error) {
                deferred.reject(error);
            });

            let errors: Array<TSExceptionReport> = [(TSExceptionReport.createFromExceptionReport(exceptionReportResponse.data))];
            $rootScope.$digest();
            expect(errorHandler).toHaveBeenCalledWith(errors);
        });
    });
});
