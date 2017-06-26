import '../../../bootstrap.ts';
import 'angular-mocks';
import HttpErrorInterceptor from './HttpErrorInterceptor';
import TSExceptionReport from '../../../models/TSExceptionReport';
import IRootScopeService = angular.IRootScopeService;
import IQService = angular.IQService;
import IDeferred = angular.IDeferred;
import TestDataUtil from '../../../utils/TestDataUtil';

describe('httpErrorInterceptor', function () {

    let httpErrorInterceptor: HttpErrorInterceptor, $rootScope: IRootScopeService, $q: IQService;



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
        let deferred: IDeferred<any>, successHandler: any, errorHandler: any;
        beforeEach(function () {
            deferred = $q.defer();
            successHandler = jasmine.createSpy('successHandler');
            errorHandler = jasmine.createSpy('errorHanlder');
            deferred.promise.then(successHandler, errorHandler);
        });

        it('should reject the response with a validation report', function () {
            let validationResponse: any = TestDataUtil.createValidationReport();
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
            let exceptionReportResponse: any = TestDataUtil.createExceptionReport();
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
