import {EbeguWebGesuch} from '../../gesuch.module';
import {FreigabeViewController} from './freigabeView';
import TSGesuch from '../../../models/TSGesuch';
import IScope = angular.IScope;
import IFormController = angular.IFormController;
import IPromise = angular.IPromise;
import WizardStepManager from '../../service/wizardStepManager';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import IQService = angular.IQService;
import TSDownloadFile from '../../../models/TSDownloadFile';
import {TSGeneratedDokumentTyp} from '../../../models/enums/TSGeneratedDokumentTyp';
import GesuchModelManager from '../../service/gesuchModelManager';
import TestDataUtil from '../../../utils/TestDataUtil';
import IHttpBackendService = angular.IHttpBackendService;

describe('freigabeView', function () {

    let controller: FreigabeViewController;
    let $scope: IScope;
    let wizardStepManager: WizardStepManager;
    let dialog: DvDialog;
    let downloadRS: DownloadRS;
    let $q: IQService;
    let gesuchModelManager: GesuchModelManager;
    let $httpBackend: IHttpBackendService;


    beforeEach(angular.mock.module(EbeguWebGesuch.name));


    beforeEach(angular.mock.inject(function ($injector: any) {
        $scope = $injector.get('$rootScope');
        wizardStepManager = $injector.get('WizardStepManager');
        dialog = $injector.get('DvDialog');
        downloadRS = $injector.get('DownloadRS');
        $q = $injector.get('$q');
        gesuchModelManager = $injector.get('GesuchModelManager');
        $httpBackend = $injector.get('$httpBackend');

        spyOn(wizardStepManager, 'updateCurrentWizardStepStatus'). and.returnValue({});

        controller = new FreigabeViewController(gesuchModelManager, $injector.get('BerechnungsManager'),
            wizardStepManager, dialog, downloadRS);
    }));

    describe('gesuchFreigeben', function () {
        it('should return undefined when the form is not valid', function () {
            let form: any = {};
            form.$valid = false;

            let returned: IPromise<void> = controller.gesuchFreigeben(form);

            expect(returned).toBeUndefined();
        });
        it('should return undefined when the form is not valid', function () {
            let form: any = {};
            form.$valid = true;
            controller.bestaetigungFreigabequittung = false;

            let returned: IPromise<void> = controller.gesuchFreigeben(form);

            expect(returned).toBeUndefined();
        });
        it('should return a Promise when the form is valid', function () {
            TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
            controller.bestaetigungFreigabequittung = true;

            let form: any = {};
            form.$valid = true;
            spyOn(dialog, 'showDialog').and.returnValue($q.when({}));
            let downloadFile: TSDownloadFile = new TSDownloadFile();
            downloadFile.accessToken = 'token';
            downloadFile.filename = 'name';
            spyOn(downloadRS, 'getAccessTokenGeneratedDokument').and.returnValue($q.when(downloadFile));
            spyOn(downloadRS, 'startDownload').and.returnValue($q.when({}));
            let gesuch: TSGesuch = new TSGesuch();
            gesuch.id = '123';
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);

            let returned: IPromise<void> = controller.gesuchFreigeben(form);
            $scope.$apply();

            expect(downloadRS.getAccessTokenGeneratedDokument).toHaveBeenCalledWith(gesuch.id, TSGeneratedDokumentTyp.FREIGABEQUITTUNG, false);
            expect(downloadRS.startDownload).toHaveBeenCalledWith(downloadFile.accessToken, downloadFile.filename, false);
            expect(returned).toBeDefined();
        });
    });
});
