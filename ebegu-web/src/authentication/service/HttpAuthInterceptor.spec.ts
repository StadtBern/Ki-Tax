import {IDeferred, IRootScopeService, IQService} from 'angular';
import HttpAuthInterceptor from './HttpAuthInterceptor';
import {EbeguAuthentication} from '../authentication.module';
import {EbeguWebCore} from '../../core/core.module';

describe('HttpAuthInterceptor', function () {

    let httpAuthInterceptor: HttpAuthInterceptor;
    let $rootScope: IRootScopeService;
    let $q: IQService;

    let authErrorResponse: any = {
        status: 401,
        data: '',
        statusText: 'Unauthorized'
    };

    // beforeEach(angular.mock.module(EbeguWebCore.name));
    beforeEach(angular.mock.module(EbeguAuthentication.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        httpAuthInterceptor = $injector.get('HttpAuthInterceptor');
        $rootScope = $injector.get('$rootScope');
        $q = $injector.get('$q');
    }));

    describe('Public API', function () {
        // it('should include a responseError() function', function () {
        //     expect(httpAuthInterceptor.responseError).toBeDefined();
        // });
    });

    describe('API usage', function () {
        // it('should reject the response with a validation report', function () {
        //     let deferred: IDeferred<any>;
        //     httpAuthInterceptor.responseError(authErrorResponse).then(function () {
        //         deferred.resolve();
        //     }, function (errors) {
        //         deferred.reject(errors);
        //     });
        //
        //     // let errors: Array<TSExceptionReport> = [(TSExceptionReport.createFromViolation('PARAMETER',
        //     //     'Die Länge des Feldes muss zwischen 36 und 36 sein', 'markAsRead.arg1', '8a146418-ab12-456f-9b17-aad6990f51'))];
        //     // $rootScope.$digest();
        //     // expect(errorHandler).toHaveBeenCalledWith(errors);
        // });
    });
});
