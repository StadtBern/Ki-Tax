import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import MitteilungRS from '../../core/service/mitteilungRS.rest';
import BerechnungsManager from '../../gesuch/service/berechnungsManager';
import GesuchModelManager from '../../gesuch/service/gesuchModelManager';
import GesuchRS from '../../gesuch/service/gesuchRS.rest';
import WizardStepManager from '../../gesuch/service/wizardStepManager';
import {TSMitteilungStatus} from '../../models/enums/TSMitteilungStatus';
import {TSMitteilungTeilnehmerTyp} from '../../models/enums/TSMitteilungTeilnehmerTyp';
import {TSRole} from '../../models/enums/TSRole';
import TSFall from '../../models/TSFall';
import TSMitteilung from '../../models/TSMitteilung';
import TSUser from '../../models/TSUser';
import EbeguUtil from '../../utils/EbeguUtil';
import TestDataUtil from '../../utils/TestDataUtil';
import {EbeguWebPosteingang} from '../posteingang.module';
import {PosteingangViewController} from './posteingangView';

describe('posteingangView', function () {

    let authServiceRS: AuthServiceRS;
    let gesuchRS: GesuchRS;
    let ebeguUtil: EbeguUtil;
    let mitteilungRS: MitteilungRS;
    let posteingangViewController: PosteingangViewController;
    let $q: angular.IQService;
    let $rootScope: angular.IRootScopeService;
    let $filter: angular.IFilterService;
    let $httpBackend: angular.IHttpBackendService;
    let gesuchModelManager: GesuchModelManager;
    let berechnungsManager: BerechnungsManager;
    let $state: angular.ui.IStateService;
    let $log: any;
    let CONSTANTS: any;
    let wizardStepManager: WizardStepManager;
    let mockMitteilung: TSMitteilung;

    beforeEach(angular.mock.module(EbeguWebPosteingang.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        authServiceRS = $injector.get('AuthServiceRS');
        mitteilungRS = $injector.get('MitteilungRS');
        gesuchRS = $injector.get('GesuchRS');
        $q = $injector.get('$q');
        $rootScope = $injector.get('$rootScope');
        $filter = $injector.get('$filter');
        $httpBackend = $injector.get('$httpBackend');
        gesuchModelManager = $injector.get('GesuchModelManager');
        berechnungsManager = $injector.get('BerechnungsManager');
        $state = $injector.get('$state');
        $log = $injector.get('$log');
        CONSTANTS = $injector.get('CONSTANTS');
        wizardStepManager = $injector.get('WizardStepManager');
        mockMitteilung = mockGetMitteilung();
    }));

    describe('API Usage', function () {
        describe('getMitteilungen', function () {
            it('should return the list of Mitteilungen', function () {
                mockRestCalls();
                posteingangViewController = new PosteingangViewController(mitteilungRS, ebeguUtil, CONSTANTS, undefined);
                $rootScope.$apply();
                expect(mitteilungRS.getMitteilungenForPosteingang).toHaveBeenCalled();
                let list: Array<TSMitteilung> = posteingangViewController.getMitteilungen();
                expect(list).toBeDefined();
                expect(list.length).toBe(1);
                expect(list[0]).toEqual(mockMitteilung);
            });
        });
    });

    function mockGetMitteilung(): TSMitteilung {
        let mockFall: TSFall = new TSFall();
        mockFall.fallNummer = 123;
        let gesuchsteller: TSUser = new TSUser();
        gesuchsteller.role = TSRole.GESUCHSTELLER;
        let mockMitteilung: TSMitteilung = new TSMitteilung(mockFall, undefined, TSMitteilungTeilnehmerTyp.GESUCHSTELLER, TSMitteilungTeilnehmerTyp.JUGENDAMT,
            gesuchsteller, undefined, 'Frage', 'Warum ist die Banane krumm?', TSMitteilungStatus.NEU, undefined);
        let dtoList: Array<TSMitteilung> = [mockMitteilung];
        let totalSize: number = 1;
        spyOn(mitteilungRS, 'getMitteilungenForPosteingang').and.returnValue($q.when(dtoList));
        return mockMitteilung;
    }

    function mockRestCalls(): void {
        TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
        $httpBackend.when('GET', '/ebegu/api/v1/institutionen').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/benutzer').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/gesuchsperioden/active').respond({});
    }
});

