import {IHttpBackendService, IQService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {EbeguWebCore} from '../core.module';
import TSKindContainer from '../../models/TSKindContainer';
import WizardStepManager from '../../gesuch/service/wizardStepManager';
import MitteilungRS from './mitteilungRS.rest';
import TSFall from '../../models/TSFall';
import TSBetreuung from '../../models/TSBetreuung';
import TSBetreuungsmitteilung from '../../models/TSBetreuungsmitteilung';
import IInjectorService = angular.auto.IInjectorService;
import IPromise = angular.IPromise;
import IRootScopeService = angular.IRootScopeService;

describe('MitteilungRS', function () {

    let mitteilungRS: MitteilungRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockKind: TSKindContainer;
    let mockKindRest: any;
    let gesuchId: string;
    let $q: IQService;
    let wizardStepManager: WizardStepManager;
    let $rootScope: IRootScopeService;


    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        mitteilungRS = $injector.get('MitteilungRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
        wizardStepManager = $injector.get('WizardStepManager');
        $q = $injector.get('$q');
        $rootScope = $injector.get('$rootScope');
    }));

    describe('Public API', function () {
        it('check URI', function () {
            expect(mitteilungRS.serviceURL).toContain('mitteilungen');
        });
        it('check Service name', function () {
            expect(mitteilungRS.getServiceName()).toBe('MitteilungRS');
        });
    });
    describe('sendbetreuungsmitteilung', function () {
        it('should create the betreuungsmitteilung and send it', function () {
            let fall: TSFall = new TSFall();
            let betreuung: TSBetreuung = new TSBetreuung();
            betreuung.betreuungNummer = 123;
            let restMitteilung: any = {};

            spyOn(ebeguRestUtil, 'betreuungsmitteilungToRestObject').and.returnValue(restMitteilung);
            spyOn(ebeguRestUtil, 'parseBetreuungsmitteilung').and.returnValue(betreuung);
            $httpBackend.expectPUT(mitteilungRS.serviceURL + '/sendbetreuungsmitteilung', restMitteilung).respond($q.when(restMitteilung));

            let result: IPromise<TSBetreuungsmitteilung> = mitteilungRS.sendbetreuungsmitteilung(fall, betreuung);
            $httpBackend.flush();
            $rootScope.$apply();

            expect(result).toBeDefined();
            result.then(response => {
                expect(response).toBe(betreuung);
            });
            $rootScope.$apply();

        });
    });
});
