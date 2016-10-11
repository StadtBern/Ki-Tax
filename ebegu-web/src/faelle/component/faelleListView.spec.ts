import GesuchRS from '../../gesuch/service/gesuchRS.rest';
import {IScope, IQService, IFilterService, IHttpBackendService} from 'angular';
import GesuchModelManager from '../../gesuch/service/gesuchModelManager';
import BerechnungsManager from '../../gesuch/service/berechnungsManager';
import {IStateService} from 'angular-ui-router';
import {EbeguWebFaelle} from '../faelle.module';
import {TSBetreuungsangebotTyp} from '../../models/enums/TSBetreuungsangebotTyp';
import TestDataUtil from '../../utils/TestDataUtil';
import {FaelleListViewController} from './faelleListView';
import WizardStepManager from '../../gesuch/service/wizardStepManager';
import TSAntragDTO from '../../models/TSAntragDTO';
import {TSAntragTyp} from '../../models/enums/TSAntragTyp';
import TSGesuch from '../../models/TSGesuch';
import TSAntragSearchresultDTO from '../../models/TSAntragSearchresultDTO';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';


describe('faelleListeView', function () {

    let authServiceRS: AuthServiceRS;
    let gesuchRS: GesuchRS;
    let faelleListViewController: FaelleListViewController;
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


    beforeEach(angular.mock.module(EbeguWebFaelle.name));

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
        describe('searchFaelle', function () {
            it('should return the list with found Faellen', function () {
                mockRestCalls();
                faelleListViewController = new FaelleListViewController($filter, gesuchRS,
                    gesuchModelManager, berechnungsManager, $state, $log, CONSTANTS, authServiceRS);

                faelleListViewController.passFilterToServer({});
                $scope.$apply();
                expect(gesuchRS.searchAntraege).toHaveBeenCalled();

                let list: Array<TSAntragDTO> = faelleListViewController.getAntragList();
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
            it('should call findGesuch and open the view gesuch.verfuegen with it for INST/TRAEGER user', function () {
                spyOn(authServiceRS, 'isRole').and.returnValue(true);
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
        faelleListViewController = new FaelleListViewController($filter, gesuchRS,
            gesuchModelManager, berechnungsManager, $state, $log, CONSTANTS, authServiceRS);

        let tsGesuch = new TSGesuch();
        spyOn(gesuchRS, methodName).and.returnValue($q.when(tsGesuch));

        faelleListViewController.editFall(mockAntrag);
        $scope.$apply();
        return tsGesuch;
    }
});

