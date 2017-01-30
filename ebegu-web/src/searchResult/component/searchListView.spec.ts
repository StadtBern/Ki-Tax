import GesuchRS from '../../gesuch/service/gesuchRS.rest';
import {IScope, IQService, IFilterService, IHttpBackendService} from 'angular';
import GesuchModelManager from '../../gesuch/service/gesuchModelManager';
import BerechnungsManager from '../../gesuch/service/berechnungsManager';
import {IStateService} from 'angular-ui-router';
import {EbeguWebSearch} from '../search.module';
import {TSBetreuungsangebotTyp} from '../../models/enums/TSBetreuungsangebotTyp';
import TestDataUtil from '../../utils/TestDataUtil';
import {SearchListViewController} from './searchListView';
import WizardStepManager from '../../gesuch/service/wizardStepManager';
import TSAntragDTO from '../../models/TSAntragDTO';
import {TSAntragTyp} from '../../models/enums/TSAntragTyp';
import TSGesuch from '../../models/TSGesuch';
import TSAntragSearchresultDTO from '../../models/TSAntragSearchresultDTO';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {TSAntragStatus} from '../../models/enums/TSAntragStatus';


describe('searchListView', function () {

    let authServiceRS: AuthServiceRS;
    let gesuchRS: GesuchRS;
    let searchListViewController: SearchListViewController;
    let $q: IQService;
    let $scope: IScope;
    let $filter: IFilterService;
    let $httpBackend: IHttpBackendService;
    let gesuchModelManager: GesuchModelManager;
    let berechnungsManager: BerechnungsManager;
    let $state: IStateService;
    let $log: any;
    let CONSTANTS: any;
    let wizardStepManager: WizardStepManager;
    let mockAntrag: TSAntragDTO;


    beforeEach(angular.mock.module(EbeguWebSearch.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        authServiceRS = $injector.get('AuthServiceRS');
        gesuchRS = $injector.get('GesuchRS');
        $q = $injector.get('$q');
        $scope = $injector.get('$rootScope');
        $filter = $injector.get('$filter');
        $httpBackend = $injector.get('$httpBackend');
        gesuchModelManager = $injector.get('GesuchModelManager');
        berechnungsManager = $injector.get('BerechnungsManager');
        $state = $injector.get('$state');
        $log = $injector.get('$log');
        CONSTANTS = $injector.get('CONSTANTS');
        wizardStepManager = $injector.get('WizardStepManager');
        mockAntrag = mockGetPendenzenList();
    }));

    describe('API Usage', function () {
        describe('searchSearch', function () {
            it('should return the list with found Searchn', function () {
                mockRestCalls();
                searchListViewController = new SearchListViewController($filter, gesuchRS,
                    gesuchModelManager, berechnungsManager, $state, $log, CONSTANTS, authServiceRS, $q);

                searchListViewController.passFilterToServer({});
                $scope.$apply();
                expect(gesuchRS.searchAntraege).toHaveBeenCalledTimes(0); //erster request wird ignoriert
                searchListViewController.passFilterToServer({});
                expect(gesuchRS.searchAntraege).toHaveBeenCalledTimes(1);
                $scope.$apply();

                let list: Array<TSAntragDTO> = searchListViewController.getAntragList();
                expect(list).toBeDefined();
                expect(list.length).toBe(1);
                expect(list[0]).toEqual(mockAntrag);
            });
        });
        describe('editPendenzJA', function () {
            it('should call findGesuch and open the view gesuch.fallcreation with it for normal user', function () {
                let tsGesuch = callEditFall('findGesuch');

                expect($state.go).toHaveBeenCalledWith('gesuch.fallcreation', { createNew: false, gesuchId: '66345345' });

            });
            it('should call findGesuch and open the view gesuch.betreuungen with it for INS/TRAEGER user if gesuch not verfuegt', function () {
                spyOn(authServiceRS, 'isOneOfRoles').and.returnValue(true);
                let tsGesuch = callEditFall('findGesuchForInstitution');
                expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen', { createNew: false, gesuchId: '66345345' });
            });
            it('should call findGesuch and open the view gesuch.verfuegen with it for INS/TRAEGER user if gesuch verfuegt', function () {
               spyOn(authServiceRS, 'isOneOfRoles').and.returnValue(true);
                mockAntrag.status = TSAntragStatus.VERFUEGT;
               let tsGesuch = callEditFall('findGesuchForInstitution');
               expect($state.go).toHaveBeenCalledWith('gesuch.verfuegen', { createNew: false, gesuchId: '66345345' });
           });
        });
    });

    function mockGetPendenzenList(): TSAntragDTO {
        let mockPendenz: TSAntragDTO = new TSAntragDTO('66345345', 123, 'name', TSAntragTyp.GESUCH,
            undefined, undefined, [TSBetreuungsangebotTyp.KITA], ['Inst1, Inst2'], 'Juan Arbolado', undefined, undefined, undefined);
        let dtoList: Array<TSAntragDTO> = [mockPendenz];
        let totalSize: number = 1;
        let searchresult: TSAntragSearchresultDTO = new TSAntragSearchresultDTO(dtoList, totalSize);
        spyOn(gesuchRS, 'searchAntraege').and.returnValue($q.when(searchresult));
        return mockPendenz;
    }

    function mockRestCalls(): void {
        TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
        $httpBackend.when('GET', '/ebegu/api/v1/institutionen').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/benutzer').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/gesuchsperioden/active').respond({});
    }

    function callEditFall(methodName: string): TSGesuch {
        mockRestCalls();
        spyOn($state, 'go');
        spyOn(wizardStepManager, 'findStepsFromGesuch').and.returnValue(undefined);
        searchListViewController = new SearchListViewController($filter, gesuchRS,
            gesuchModelManager, berechnungsManager, $state, $log, CONSTANTS, authServiceRS, $q);

        let tsGesuch = new TSGesuch();
        spyOn(gesuchRS, methodName).and.returnValue($q.when(tsGesuch));

        searchListViewController.editFall(mockAntrag, undefined);
        $scope.$apply();
        return tsGesuch;
    }
});

