import '../../../bootstrap.ts';
import 'angular-mocks';
import {TSErrorLevel} from '../../../models/enums/TSErrorLevel';
import {TSErrorType} from '../../../models/enums/TSErrorType';
import {TSMessageEvent} from '../../../models/enums/TSErrorEvent';
import TSExceptionReport from '../../../models/TSExceptionReport';
import HttpErrorInterceptor from './HttpErrorInterceptor';
import ErrorService from './ErrorService';
import IRootScopeService = angular.IRootScopeService;
describe('errorService', function () {

    var httpErrorInterceptor: HttpErrorInterceptor, errorService: ErrorService;
    var $rootScope: IRootScopeService;

    beforeEach(angular.mock.module('dvbAngular.errors'));

    beforeEach(angular.mock.inject(function ($injector: any) {
        httpErrorInterceptor = $injector.get('HttpErrorInterceptor');
        $rootScope = $injector.get('$rootScope');
        errorService = $injector.get('ErrorService');
    }));

    beforeEach(inject(function () {
        spyOn($rootScope, '$broadcast').and.callThrough();
    }));


    describe('Public API', function () {
        it('should include a getErrors() function', function () {
            expect(errorService.getErrors).toBeDefined();
        });
        it('should include a addValidationError() function', function () {
            expect(errorService.addValidationError).toBeDefined();
        });
        it('should include a clearAll() function', function () {
            expect(errorService.clearAll).toBeDefined();
        });
        it('should include a clearError() function', function () {
            expect(errorService.clearError).toBeDefined();
        });
        it('should include a handleError() function', function () {
            expect(errorService.handleError).toBeDefined();
        });
        it('should include a handleValidationError() function', function () {
            expect(errorService.handleValidationError).toBeDefined();
        });
    });

    describe('Public API usage', function () {
        describe('getErrors()', function () {
            it('should return an array', function () {
                expect(errorService.getErrors()).toEqual([]);
            });
            it('the internal error array should be immutable', function () {
                let errors: Array<TSExceptionReport> = errorService.getErrors();
                let length = errors.length;

                errors.push(TSExceptionReport.createClientSideError(TSErrorLevel.INFO, 'custom test', null));
                expect(errorService.getErrors().length).toEqual(length);
            });
        });

        describe('addValidationError()', function () {
            it('should add a validation error to errors', function () {
                var msgKey = 'TEST';
                var args = {
                    fieldName: 'field',
                    minlenght: '10'
                };
                expect(args).toBeTruthy();
                errorService.addValidationError(msgKey, args);

                var errors: Array<TSExceptionReport> = errorService.getErrors();
                expect(errors.length === 1).toBeTruthy();
                var error: TSExceptionReport = errors[0];
                expect(error.severity === TSErrorLevel.SEVERE);
                expect(error.msgKey).toBe(msgKey);
                expect(error.argumentList).toEqual(args);
                expect(error.type).toBe(TSErrorType.CLIENT_SIDE);
            });

            it('should ignore duplicated errors', function () {
                errorService.addValidationError('TEST');
                var length = errorService.getErrors().length;
                errorService.addValidationError('TEST');
                expect(errorService.getErrors().length === length).toBeTruthy();
            });

            it('should broadcast an UPDATE event', function () {
                errorService.addValidationError('TEST');
                expect($rootScope.$broadcast).toHaveBeenCalledWith(TSMessageEvent.ERROR_UPDATE, errorService.getErrors());
            });
        });

        describe('handleValidationError', function () {
            beforeEach(function () {
                errorService.handleValidationError(false, 'TEST');
            });

            it('should add a validation error on FALSE', function () {
                expect(errorService.getErrors()[0].msgKey).toBe('TEST');
                expect($rootScope.$broadcast).toHaveBeenCalledWith(TSMessageEvent.ERROR_UPDATE, errorService.getErrors());
            });

            it('should remove a validation error on TRUE', function () {
                errorService.handleValidationError(false, 'TEST');
                expect(errorService.getErrors().length === 0);
                expect($rootScope.$broadcast).toHaveBeenCalledWith(TSMessageEvent.ERROR_UPDATE, errorService.getErrors());
            });
        });

        describe('clearAll()', function () {
            it('should clear all errors', function () {
                errorService.addValidationError('foo');
                expect(errorService.getErrors().length === 1).toBeTruthy();
                expect($rootScope.$broadcast).not.toHaveBeenCalledWith(TSMessageEvent.CLEAR);

                errorService.clearAll();
                expect(errorService.getErrors().length === 0).toBeTruthy();
                expect($rootScope.$broadcast).toHaveBeenCalledWith(TSMessageEvent.CLEAR);
            });
        });

        describe('clearError()', function () {
            it('should remove the specified error', function () {
                errorService.addValidationError('KEEP');
                expect($rootScope.$broadcast).toHaveBeenCalledWith(TSMessageEvent.ERROR_UPDATE, errorService.getErrors());
                errorService.addValidationError('REMOVE');
                expect($rootScope.$broadcast).toHaveBeenCalledWith(TSMessageEvent.ERROR_UPDATE, errorService.getErrors());
                errorService.clearError('REMOVE');
                expect($rootScope.$broadcast).toHaveBeenCalledWith(TSMessageEvent.ERROR_UPDATE, errorService.getErrors());
                var errors = errorService.getErrors();
                expect(errors.length === 1).toBeTruthy();
                expect(errors[0].msgKey).toBe('KEEP');
            });
        });

        /*        describe('handleError', function () {
         var length;
         beforeEach(function () {
         length = errorService.getErrors().length;
         });

         it('should add a DvbError to the errors', function () {
         errorService.handleError(new DvbError(TSErrorType.INTERNAL, TSErrorLevel.SEVERE, 'ERR_INTERNAL'));
         expect(errorService.getErrors().length).toBe(length + 1);
         expect($rootScope.$broadcast).toHaveBeenCalledWith(TSMessageEvent.UPDATE, errorService.getErrors());
         });

         it('should ignore invalid DvbErrors', function () {
         errorService.handleError(new DvbError());
         expect(errorService.getErrors().length).toBe(length);
         expect($rootScope.$broadcast).not.toHaveBeenCalledWith(TSMessageEvent.UPDATE, errorService.getErrors());
         });
         });*/
    });
});
