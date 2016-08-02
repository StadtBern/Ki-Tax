import {EbeguWebPendenzen} from '../../pendenzen.module';
import PendenzRS from '../../service/PendenzRS.rest';
import {PendenzenListViewController} from './pendenzenListView';
import {IScope, IQService, IFilterService, IHttpBackendService} from 'angular';
import TSPendenzJA from '../../../models/TSPendenzJA';
import {TSAntragTyp} from '../../../models/enums/TSAntragTyp';
import {TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import {InstitutionRS} from '../../../core/service/institutionRS.rest';
import GesuchRS from '../../../gesuch/service/gesuchRS.rest';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import TestDataUtil from '../../../utils/TestDataUtil';
import TSGesuch from '../../../models/TSGesuch';
import BerechnungsManager from '../../../gesuch/service/berechnungsManager';

describe('pendenzenListView', function () {

    let institutionRS: InstitutionRS;
    let gesuchsperiodeRS: GesuchsperiodeRS;
    let gesuchRS: GesuchRS;
    let pendenzRS: PendenzRS;
    let pendenzListViewController: PendenzenListViewController;
    let $q: IQService;
    let $scope: IScope;
    let $filter: IFilterService;
    let $httpBackend: IHttpBackendService;
    let gesuchModelManager: GesuchModelManager;
    let berechnungsManager: BerechnungsManager;
    let $state: IStateService;
    let CONSTANTS: any;


    beforeEach(angular.mock.module(EbeguWebPendenzen.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        pendenzRS = $injector.get('PendenzRS');
        institutionRS = $injector.get('InstitutionRS');
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
        describe('getPendenzenList', function () {
            it('should return the list with all pendenzen', function () {
                let mockPendenz: TSPendenzJA = mockGetPendenzenList();
                mockRestCalls();
                pendenzListViewController = new PendenzenListViewController(pendenzRS, undefined, $filter,
                    institutionRS, gesuchsperiodeRS, gesuchRS, gesuchModelManager, berechnungsManager, $state, CONSTANTS);

                $scope.$apply();
                expect(pendenzRS.getPendenzenList).toHaveBeenCalled();

                let list: Array<TSPendenzJA> = pendenzListViewController.getPendenzenList();
                expect(list).toBeDefined();
                expect(list.length).toBe(1);
                expect(list[0]).toEqual(mockPendenz);
            });
        });
        describe('translateBetreuungsangebotTypList', () => {
            it('returns a comma separated string with all BetreuungsangebotTypen', () => {
                let list: Array<TSBetreuungsangebotTyp> = [TSBetreuungsangebotTyp.KITA, TSBetreuungsangebotTyp.TAGESELTERN];
                expect(pendenzListViewController.translateBetreuungsangebotTypList(list))
                    .toEqual('Tagesstätte für Kleinkinder, Tageseltern');
            });
            it('returns an empty string for invalid values or empty lists', () => {
                expect(pendenzListViewController.translateBetreuungsangebotTypList([])).toEqual('');
                expect(pendenzListViewController.translateBetreuungsangebotTypList(undefined)).toEqual('');
                expect(pendenzListViewController.translateBetreuungsangebotTypList(null)).toEqual('');
            });
        });
        describe('editPendenzJA', function () {
            it('should call findGesuch and open the view gesuch.fallcreation with it', function () {
                let mockPendenz: TSPendenzJA = mockGetPendenzenList();
                mockRestCalls();
                spyOn($state, 'go');
                pendenzListViewController = new PendenzenListViewController(pendenzRS, undefined, $filter,
                    institutionRS, gesuchsperiodeRS, gesuchRS, gesuchModelManager, berechnungsManager, $state, CONSTANTS);

                let tsGesuch = new TSGesuch();
                spyOn(gesuchRS, 'findGesuch').and.returnValue($q.when(tsGesuch));

                pendenzListViewController.editPendenzJA(mockPendenz);
                $scope.$apply();

                expect(pendenzRS.getPendenzenList).toHaveBeenCalled();
                expect(gesuchRS.findGesuch).toHaveBeenCalledWith(mockPendenz.antragId);
                expect($state.go).toHaveBeenCalledWith('gesuch.fallcreation');
                expect(gesuchModelManager.gesuch).toBe(tsGesuch);
            });
        });
    });

    function mockGetPendenzenList(): TSPendenzJA {
        let mockPendenz: TSPendenzJA = new TSPendenzJA('66345345', 123, 'name', TSAntragTyp.GESUCH, undefined,
            undefined, [TSBetreuungsangebotTyp.KITA], ['Inst1, Inst2'], 'Juan Arbolado');
        let result: Array<TSPendenzJA> = [mockPendenz];
        spyOn(pendenzRS, 'getPendenzenList').and.returnValue($q.when(result));
        return mockPendenz;
    }

    function mockRestCalls(): void {
        TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
        $httpBackend.when('GET', '/ebegu/api/v1/institutionen').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/benutzer').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/gesuchsperioden/active').respond({});
    }
});
