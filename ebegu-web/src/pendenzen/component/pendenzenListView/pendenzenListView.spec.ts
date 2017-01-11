import {EbeguWebPendenzen} from '../../pendenzen.module';
import PendenzRS from '../../service/PendenzRS.rest';
import {PendenzenListViewController} from './pendenzenListView';
import {IScope, IQService, IFilterService, IHttpBackendService} from 'angular';
import TSAntragDTO from '../../../models/TSAntragDTO';
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
import WizardStepManager from '../../../gesuch/service/wizardStepManager';

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
    let wizardStepManager: WizardStepManager;


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
        wizardStepManager = $injector.get('WizardStepManager');
    }));

    describe('API Usage', function () {
        describe('getPendenzenList', function () {
            it('should return the list with all pendenzen', function () {
                let mockPendenz: TSAntragDTO = mockGetPendenzenList();
                mockRestCalls();
                pendenzListViewController = new PendenzenListViewController(pendenzRS, undefined, $filter,
                    institutionRS, gesuchsperiodeRS, gesuchRS, gesuchModelManager, berechnungsManager, $state, CONSTANTS);

                $scope.$apply();
                expect(pendenzRS.getPendenzenList).toHaveBeenCalled();

                let list: Array<TSAntragDTO> = pendenzListViewController.getPendenzenList();
                expect(list).toBeDefined();
                expect(list.length).toBe(1);
                expect(list[0]).toEqual(mockPendenz);
            });
        });
        describe('translateBetreuungsangebotTypList', () => {
            it('returns a comma separated string with all BetreuungsangebotTypen', () => {
                let list: Array<TSBetreuungsangebotTyp> = [TSBetreuungsangebotTyp.KITA, TSBetreuungsangebotTyp.TAGESELTERN_KLEINKIND];
                expect(pendenzListViewController.translateBetreuungsangebotTypList(list))
                    .toEqual('Kita – Tagesstätte für Kleinkinder, Tageseltern für Kleinkinder');
            });
            it('returns an empty string for invalid values or empty lists', () => {
                expect(pendenzListViewController.translateBetreuungsangebotTypList([])).toEqual('');
                expect(pendenzListViewController.translateBetreuungsangebotTypList(undefined)).toEqual('');
                expect(pendenzListViewController.translateBetreuungsangebotTypList(null)).toEqual('');
            });
        });
        describe('editPendenzJA', function () {
            it('should call findGesuch and open the view gesuch.fallcreation with it', function () {
                let mockPendenz: TSAntragDTO = mockGetPendenzenList();
                mockRestCalls();
                spyOn($state, 'go');
                spyOn(wizardStepManager, 'findStepsFromGesuch').and.returnValue(undefined);
                pendenzListViewController = new PendenzenListViewController(pendenzRS, undefined, $filter,
                    institutionRS, gesuchsperiodeRS, gesuchRS, gesuchModelManager, berechnungsManager, $state, CONSTANTS);

                let tsGesuch = new TSGesuch();
                spyOn(gesuchRS, 'findGesuch').and.returnValue($q.when(tsGesuch));

                pendenzListViewController.editPendenzJA(mockPendenz); //pendenz wird eidtiert
                $scope.$apply();

                expect(pendenzRS.getPendenzenList).toHaveBeenCalled();
                expect($state.go).toHaveBeenCalledWith('gesuch.fallcreation', {createNew: false, gesuchId: '66345345'});

            });
        });
    });

    function mockGetPendenzenList(): TSAntragDTO {
        let mockPendenz: TSAntragDTO = new TSAntragDTO('66345345', 123, 'name', TSAntragTyp.GESUCH,
            undefined, undefined, [TSBetreuungsangebotTyp.KITA], ['Inst1, Inst2'], 'Juan Arbolado', undefined, undefined, undefined);
        let result: Array<TSAntragDTO> = [mockPendenz];
        spyOn(pendenzRS, 'getPendenzenList').and.returnValue($q.when(result));
        return mockPendenz;
    }

    function mockRestCalls(): void {
        TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
        $httpBackend.when('GET', '/ebegu/api/v1/institutionen').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/benutzer').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/gesuchsperioden/active').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/gesuchsperioden/').respond({});
    }
});
