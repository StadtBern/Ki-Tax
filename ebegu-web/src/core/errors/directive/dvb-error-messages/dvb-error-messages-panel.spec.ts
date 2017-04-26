import '../../../../bootstrap.ts';
import 'angular-mocks';
import IScope = angular.IScope;
import ICompileService = angular.ICompileService;
import IRootScopeService = angular.IRootScopeService;
import {DvErrorMessagesPanelComponent} from './dvb-error-messages-panel';
import TSExceptionReport from '../../../../models/TSExceptionReport';
import {TSErrorType} from '../../../../models/enums/TSErrorType';
import {TSErrorLevel} from '../../../../models/enums/TSErrorLevel';
import {TSErrorAction} from '../../../../models/enums/TSErrorAction';

describe('dvbErrorMessages', function () {

    let controller: DvErrorMessagesPanelComponent;


    beforeEach(angular.mock.module('dvbAngular.errors'));

    beforeEach(angular.mock.inject(function ($injector: any) {
        controller = new DvErrorMessagesPanelComponent($injector.get('$rootScope'), $injector.get('ErrorService'));
        spyOn(controller, 'show').and.returnValue({});
    }));

    describe('displayMessages', () => {
        it('should not add any action', function () {
            let error: TSExceptionReport = new TSExceptionReport(TSErrorType.INTERNAL, TSErrorLevel.SEVERE, 'message', '');
            error.errorCodeEnum = 'OTHER_TYPE';
            let errors: TSExceptionReport[] = [error];
            controller.displayMessages(undefined, errors);

            expect(error.action).toBeUndefined();
        });
        it('should add an action to ERROR_EXISTING_ONLINE_MUTATION', function () {
            let error: TSExceptionReport = new TSExceptionReport(TSErrorType.INTERNAL, TSErrorLevel.SEVERE, 'message', '');
            error.errorCodeEnum = 'ERROR_EXISTING_ONLINE_MUTATION';
            let errors: TSExceptionReport[] = [error];
            controller.displayMessages(undefined, errors);

            expect(error.action).toBe(TSErrorAction.REMOVE_ANTRAG);
        });
    });
});
