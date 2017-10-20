/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import {TSErrorAction} from '../../../../models/enums/TSErrorAction';
import TSExceptionReport from '../../../../models/TSExceptionReport';
import TestDataUtil from '../../../../utils/TestDataUtil';
import {DvErrorMessagesPanelComponent} from './dvb-error-messages-panel';

describe('dvbErrorMessages', function () {

    let controller: DvErrorMessagesPanelComponent;
    let exceptionReport: any;

    beforeEach(angular.mock.module('dvbAngular.errors'));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
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
