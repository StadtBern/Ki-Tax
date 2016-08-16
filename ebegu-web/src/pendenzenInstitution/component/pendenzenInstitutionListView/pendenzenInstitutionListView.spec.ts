import {EbeguWebPendenzenInstitution} from '../../pendenzenInstitution.module';
import PendenzInstitutionRS from '../../service/PendenzInstitutionRS.rest';
import {PendenzenInstitutionListViewController} from './pendenzenInstitutionListView';
import {IScope, IQService, IFilterService, IHttpBackendService} from 'angular';
import TSPendenzInstitution from '../../../models/TSPendenzInstitution';
import {TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import {InstitutionRS} from '../../../core/service/institutionRS.rest';
import GesuchRS from '../../../gesuch/service/gesuchRS.rest';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import TestDataUtil from '../../../utils/TestDataUtil';
import BerechnungsManager from '../../../gesuch/service/berechnungsManager';
import {InstitutionStammdatenRS} from '../../../core/service/institutionStammdatenRS.rest';

describe('pendenzenInstitutionListView', function () {

    let institutionRS: InstitutionRS;
    let gesuchsperiodeRS: GesuchsperiodeRS;
    let gesuchRS: GesuchRS;
    let institutionStammdatenRS: InstitutionStammdatenRS;
    let pendenzInstitutionRS: PendenzInstitutionRS;
    let pendenzInstitutionListViewController: PendenzenInstitutionListViewController;
    let $q: IQService;
    let $scope: IScope;
    let $filter: IFilterService;
    let $httpBackend: IHttpBackendService;
    let gesuchModelManager: GesuchModelManager;
    let berechnungsManager: BerechnungsManager;
    let $state: IStateService;
    let CONSTANTS: any;


    beforeEach(angular.mock.module(EbeguWebPendenzenInstitution.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        pendenzInstitutionRS = $injector.get('PendenzInstitutionRS');
        institutionRS = $injector.get('InstitutionRS');
        institutionStammdatenRS = $injector.get('InstitutionStammdatenRS');
        gesuchsperiodeRS = $injector.get('GesuchsperiodeRS');
        gesuchRS = $injector.get('GesuchRS');
        $q = $injector.get('$q');
        $scope = $injector.get('$rootScope');
        $filter = $injector.get('$filter');
        $httpBackend = $injector.get('$httpBackend');
        gesuchModelManager = $injector.get('GesuchModelManager');
        berechnungsManager = $injector.get('BerechnungsManager');
        $state = $injector.get('$state');
        CONSTANTS = $injector.get('CONSTANTS');
    }));

    describe('API Usage', function () {
        describe('init Pendenzenliste', function () {
            it('should return the list with all pendenzen', function () {
                let mockPendenz: TSPendenzInstitution = mockGetPendenzenList();
                mockRestCalls();
                pendenzInstitutionListViewController = new PendenzenInstitutionListViewController(pendenzInstitutionRS, undefined, $filter,
                    institutionRS, institutionStammdatenRS, gesuchsperiodeRS, gesuchRS, gesuchModelManager, berechnungsManager, $state, CONSTANTS);

                $scope.$apply();
                expect(pendenzInstitutionRS.getPendenzenList).toHaveBeenCalled();

                let list: Array<TSPendenzInstitution> = pendenzInstitutionListViewController.getPendenzenList();
                expect(list).toBeDefined();
                expect(list.length).toBe(1);
                expect(list[0]).toEqual(mockPendenz);
            });
        });
    });

    function mockGetPendenzenList(): TSPendenzInstitution {
        let mockPendenz: TSPendenzInstitution = new TSPendenzInstitution('123.12.12.12', '123', 'Kind', 'Kilian', undefined,
            'Platzbestaetigung', undefined, undefined, TSBetreuungsangebotTyp.KITA, undefined);
        let result: Array<TSPendenzInstitution> = [mockPendenz];
        spyOn(pendenzInstitutionRS, 'getPendenzenList').and.returnValue($q.when(result));
        return mockPendenz;
    }

    function mockRestCalls(): void {
        TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
        $httpBackend.when('GET', '/ebegu/api/v1/institutionen').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/institutionen/currentuser').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/institutionstammdaten/currentuser').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/benutzer').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/gesuchsperioden/active').respond({});
    }
});
