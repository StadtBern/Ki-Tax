import '../../../../bootstrap.ts';
import 'angular-mocks';
import IScope = angular.IScope;
import ICompileService = angular.ICompileService;
import IRootScopeService = angular.IRootScopeService;
import {DvErrorMessagesPanelComponent} from './dvb-error-messages-panel';
import TSExceptionReport from '../../../../models/TSExceptionReport';
import {TSErrorAction} from '../../../../models/enums/TSErrorAction';
import TestDataUtil from '../../../../utils/TestDataUtil';

describe('dvbErrorMessages', function () {

    let controller: DvErrorMessagesPanelComponent;
    let exceptionReport: any;

    beforeEach(angular.mock.module('dvbAngular.errors'));

    beforeEach(angular.mock.inject(function ($injector: any) {
        controller = new DvErrorMessagesPanelComponent($injector.get('$rootScope'), $injector.get('ErrorService'),
            undefined, undefined);
        spyOn(controller, 'show').and.returnValue({});
        exceptionReport = TestDataUtil.createExceptionReport();
    }));

    describe('displayMessages', () => {
        it('should not add any action', function () {
            exceptionReport.errorCodeEnum = 'OTHER_TYPE';
            let error: TSExceptionReport = TSExceptionReport.createFromExceptionReport(exceptionReport);
            let errors: TSExceptionReport[] = [error];
            controller.displayMessages(undefined, errors);

            expect(error.action).toBeUndefined();
        });
        it('should add an action to ERROR_EXISTING_ONLINE_MUTATION', function () {
            exceptionReport.errorCodeEnum = 'ERROR_EXISTING_ONLINE_MUTATION';
            let error: TSExceptionReport = TSExceptionReport.createFromExceptionReport(exceptionReport);
            let errors: TSExceptionReport[] = [error];
            controller.displayMessages(undefined, errors);

            expect(error.action).toBe(TSErrorAction.REMOVE_ONLINE_MUTATION);
        });
    });
});
