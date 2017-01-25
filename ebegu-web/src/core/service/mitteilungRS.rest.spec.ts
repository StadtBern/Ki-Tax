import {IHttpBackendService, IQService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {EbeguWebCore} from '../core.module';
import TSKindContainer from '../../models/TSKindContainer';
import WizardStepManager from '../../gesuch/service/wizardStepManager';
import MitteilungRS from './mitteilungRS.rest';
import IInjectorService = angular.auto.IInjectorService;

describe('MitteilungRS', function () {

    let mitteilungRS: MitteilungRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockKind: TSKindContainer;
    let mockKindRest: any;
    let gesuchId: string;
    let $q: IQService;
    let wizardStepManager: WizardStepManager;


    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        mitteilungRS = $injector.get('MitteilungRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
        wizardStepManager = $injector.get('WizardStepManager');
        $q = $injector.get('$q');
    }));

    describe('Public API', function () {
        it('check URI', function () {
            expect(mitteilungRS.serviceURL).toContain('mitteilungen');
        });
        it('check Service name', function () {
            expect(mitteilungRS.getServiceName()).toBe('MitteilungRS');
        });
    });
});
