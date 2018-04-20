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

import {EbeguWebAdmin} from '../../../admin/admin.module';
import {ApplicationPropertyRS} from '../../../admin/service/applicationPropertyRS.rest';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import TSDownloadFile from '../../../models/TSDownloadFile';
import TSGesuch from '../../../models/TSGesuch';
import TestDataUtil from '../../../utils/TestDataUtil';
import {EbeguWebGesuch} from '../../gesuch.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import WizardStepManager from '../../service/wizardStepManager';
import {FreigabeViewController} from './freigabeView';

describe('freigabeView', function () {

    let controller: FreigabeViewController;
    let $scope: angular.IScope;
    let wizardStepManager: WizardStepManager;
    let dialog: DvDialog;
    let downloadRS: DownloadRS;
    let $q: angular.IQService;
    let gesuchModelManager: GesuchModelManager;
    let $httpBackend: angular.IHttpBackendService;
    let applicationPropertyRS: any;
    let authServiceRS: AuthServiceRS;
    let $timeout: angular.ITimeoutService;

    let gesuch: TSGesuch;

    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    beforeEach(angular.mock.module(EbeguWebAdmin.name));  //to inject applicationPropertyRS

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        $scope = $injector.get('$rootScope');
        wizardStepManager = $injector.get('WizardStepManager');
        dialog = $injector.get('DvDialog');
        downloadRS = $injector.get('DownloadRS');
        $q = $injector.get('$q');
        gesuchModelManager = $injector.get('GesuchModelManager');
        $httpBackend = $injector.get('$httpBackend');
        applicationPropertyRS = $injector.get('ApplicationPropertyRS');
        authServiceRS = $injector.get('AuthServiceRS');
        $timeout = $injector.get('$timeout');

        spyOn(applicationPropertyRS, 'isDevMode').and.returnValue($q.when(false));
        spyOn(authServiceRS, 'isOneOfRoles').and.returnValue(true);
        spyOn(wizardStepManager, 'updateCurrentWizardStepStatus').and.returnValue({});

        controller = new FreigabeViewController(gesuchModelManager, $injector.get('BerechnungsManager'),
            wizardStepManager, dialog, downloadRS, $scope, applicationPropertyRS, authServiceRS, $timeout);
        controller.form = <angular.IFormController>{};

        spyOn(controller, 'isGesuchValid').and.callFake(function () {
            return controller.form.$valid;
        });

        let form = TestDataUtil.createDummyForm();
        // $rootScope.form = form;
        controller.form = form;
    }));
    describe('canBeFreigegeben', function () {
        it('should return false when not all steps are true', function () {
            spyOn(wizardStepManager, 'areAllStepsOK').and.returnValue(false);
            spyOn(wizardStepManager, 'hasStepGivenStatus').and.returnValue(true);
            expect(controller.canBeFreigegeben()).toBe(false);
        });
        it('should return false when all steps are true but not all Betreuungen are accepted', function () {
            spyOn(wizardStepManager, 'areAllStepsOK').and.returnValue(true);
            spyOn(wizardStepManager, 'hasStepGivenStatus').and.returnValue(false);

            expect(controller.canBeFreigegeben()).toBe(false);

            expect(wizardStepManager.hasStepGivenStatus).toHaveBeenCalledWith(TSWizardStepName.BETREUUNG, TSWizardStepStatus.OK);
        });
        it('should return false when all steps are true and all Betreuungen are accepted and the Gesuch is ReadOnly', function () {
            spyOn(wizardStepManager, 'areAllStepsOK').and.returnValue(true);
            spyOn(wizardStepManager, 'hasStepGivenStatus').and.returnValue(true);
            spyOn(gesuchModelManager, 'isGesuchReadonly').and.returnValue(true);
            expect(controller.canBeFreigegeben()).toBe(false);
        });
        it('should return true when all steps are true and all Betreuungen are accepted and the Gesuch is not ReadOnly', function () {
            spyOn(wizardStepManager, 'areAllStepsOK').and.returnValue(true);
            spyOn(wizardStepManager, 'hasStepGivenStatus').and.returnValue(true);
            spyOn(gesuchModelManager, 'isGesuchReadonly').and.returnValue(false);
            spyOn(controller, 'isGesuchInStatus').and.returnValue(true);
            expect(controller.canBeFreigegeben()).toBe(true);
        });
    });
    describe('gesuchFreigeben', function () {
        it('should return undefined when the form is not valid', function () {
            controller.form.$valid = false;

            let returned: angular.IPromise<void> = controller.gesuchEinreichen();

            expect(returned).toBeUndefined();
        });
        it('should return undefined when the form is not valid', function () {
            controller.form.$valid = true;
            controller.bestaetigungFreigabequittung = false;

            let returned: angular.IPromise<void> = controller.gesuchEinreichen();

            expect(returned).toBeUndefined();
        });
        it('should call showDialog when form is valid', function () {
            TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
            controller.bestaetigungFreigabequittung = true;

            controller.form.$valid = true;
            spyOn(dialog, 'showDialog').and.returnValue($q.when({}));

            let returned: angular.IPromise<void> = controller.gesuchEinreichen();
            $scope.$apply();

            expect(dialog.showDialog).toHaveBeenCalled();
            expect(returned).toBeDefined();
        });
    });
    describe('confirmationCallback', function () {
        it('should return a Promise when the form is valid', function () {
            TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
            controller.bestaetigungFreigabequittung = true;

            controller.form.$valid = true;

            spyOn(dialog, 'showDialog').and.returnValue($q.when({}));
            let downloadFile: TSDownloadFile = new TSDownloadFile();
            downloadFile.accessToken = 'token';
            downloadFile.filename = 'name';
            spyOn(downloadRS, 'getFreigabequittungAccessTokenGeneratedDokument').and.returnValue($q.when(downloadFile));
            spyOn(downloadRS, 'startDownload').and.returnValue($q.when({}));
            spyOn(gesuchModelManager, 'openGesuch').and.returnValue($q.when({}));
            let gesuch: TSGesuch = new TSGesuch();
            gesuch.id = '123';
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);

            controller.confirmationCallback();
            $scope.$apply();

            expect(downloadRS.getFreigabequittungAccessTokenGeneratedDokument).toHaveBeenCalledWith(gesuch.id, true);
            expect(downloadRS.startDownload).toHaveBeenCalledWith(downloadFile.accessToken, downloadFile.filename, false, jasmine.any(Object));
        });
    });
    describe('openFreigabequittungPDF', function () {
        beforeEach(() => {
            TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
            spyOn(gesuchModelManager, 'openGesuch').and.returnValue($q.when({}));
            spyOn(downloadRS, 'startDownload').and.returnValue($q.when({}));
            spyOn(downloadRS, 'getFreigabequittungAccessTokenGeneratedDokument').and.returnValue($q.when({}));
            gesuch = new TSGesuch();
            gesuch.id = '123';
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);
        });
        it('should call the service for Erstgesuch', function () {
            spyOn(gesuchModelManager, 'isGesuch').and.returnValue(true);

            controller.openFreigabequittungPDF(false);
            $scope.$apply();

            expect(downloadRS.getFreigabequittungAccessTokenGeneratedDokument).toHaveBeenCalledWith(gesuch.id, false);
        });
    });
});
