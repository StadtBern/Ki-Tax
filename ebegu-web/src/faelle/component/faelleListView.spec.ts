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

import GesuchModelManager from '../../gesuch/service/gesuchModelManager';
import WizardStepManager from '../../gesuch/service/wizardStepManager';
import {TSAntragTyp} from '../../models/enums/TSAntragTyp';
import {TSBetreuungsangebotTyp} from '../../models/enums/TSBetreuungsangebotTyp';
import TSAntragDTO from '../../models/TSAntragDTO';
import TSAntragSearchresultDTO from '../../models/TSAntragSearchresultDTO';
import TSGesuch from '../../models/TSGesuch';
import TestDataUtil from '../../utils/TestDataUtil';
import {EbeguWebFaelle} from '../faelle.module';
import {FaelleListViewController} from './faelleListView';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {TSAntragStatus} from '../../models/enums/TSAntragStatus';
import SearchRS from '../../gesuch/service/searchRS.rest';
import GesuchRS from '../../gesuch/service/gesuchRS.rest';

describe('faelleListView', function () {

    let authServiceRS: AuthServiceRS;
    let gesuchRS: GesuchRS;
    let searchRS: SearchRS;
    let faelleListViewController: FaelleListViewController;
    let $q: angular.IQService;
    let $scope: angular.IScope;
    let $filter: angular.IFilterService;
    let $httpBackend: angular.IHttpBackendService;
    let gesuchModelManager: GesuchModelManager;
    let $state: angular.ui.IStateService;
    let $log: angular.ILogService;
    let wizardStepManager: WizardStepManager;
    let mockAntrag: TSAntragDTO;

    beforeEach(angular.mock.module(EbeguWebFaelle.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        authServiceRS = $injector.get('AuthServiceRS');
        gesuchRS = $injector.get('GesuchRS');
        searchRS = $injector.get('SearchRS');
        $q = $injector.get('$q');
        $scope = $injector.get('$rootScope');
        $filter = $injector.get('$filter');
        $httpBackend = $injector.get('$httpBackend');
        gesuchModelManager = $injector.get('GesuchModelManager');
        $state = $injector.get('$state');
        $log = $injector.get('$log');
        wizardStepManager = $injector.get('WizardStepManager');
        mockAntrag = mockGetPendenzenList();
    }));

    describe('API Usage', function () {
        describe('searchFaelle', function () {
            it('should return the list with found Faellen', function () {
                mockRestCalls();
                faelleListViewController = new FaelleListViewController($filter,
                    gesuchModelManager,  $state, $log,  authServiceRS, searchRS);

                faelleListViewController.passFilterToServer({});
                expect(searchRS.searchAntraege).toHaveBeenCalledTimes(1);
                $scope.$apply();

                let list: Array<TSAntragDTO> = faelleListViewController.getAntragList();
                expect(list).toBeDefined();
                expect(list.length).toBe(1);
                expect(list[0]).toEqual(mockAntrag);
            });
        });
        describe('editPendenzJA', function () {
            it('should call findGesuch and open the view gesuch.fallcreation with it for normal user', function () {
                let tsGesuch = callEditFall();

                expect($state.go).toHaveBeenCalledWith('gesuch.fallcreation', {createNew: false, gesuchId: '66345345'});

            });
            it('should call findGesuch and open the view gesuch.betreuungen with it for INS/TRAEGER user if gesuch not verfuegt', function () {
                spyOn(authServiceRS, 'isOneOfRoles').and.returnValue(true);
                let tsGesuch = callEditFall();
                expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen', {createNew: false, gesuchId: '66345345'});
            });
            it('should call findGesuch and open the view gesuch.verfuegen with it for INS/TRAEGER user if gesuch verfuegt', function () {
                spyOn(authServiceRS, 'isOneOfRoles').and.returnValue(true);
                mockAntrag.status = TSAntragStatus.VERFUEGT;
                let tsGesuch = callEditFall();
                expect($state.go).toHaveBeenCalledWith('gesuch.verfuegen', {createNew: false, gesuchId: '66345345'});
            });
        });
    });

    function mockGetPendenzenList(): TSAntragDTO {
        let mockPendenz: TSAntragDTO = new TSAntragDTO('66345345', 123, 'name', TSAntragTyp.ERSTGESUCH,
            undefined, undefined, undefined, [TSBetreuungsangebotTyp.KITA], ['Inst1, Inst2'], 'Juan Arbolado', 'Juan Arbolado',
            undefined, undefined, undefined);
        let dtoList: Array<TSAntragDTO> = [mockPendenz];
        let totalSize: number = 1;
        let searchresult: TSAntragSearchresultDTO = new TSAntragSearchresultDTO(dtoList, totalSize);
        spyOn(searchRS, 'searchAntraege').and.returnValue($q.when(searchresult));
        return mockPendenz;
    }

    function mockRestCalls(): void {
        TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
        $httpBackend.when('GET', '/ebegu/api/v1/institutionen').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/benutzer').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/gesuchsperioden/active').respond({});
    }

    function callEditFall(): TSGesuch {
        mockRestCalls();
        spyOn($state, 'go');
        spyOn(wizardStepManager, 'findStepsFromGesuch').and.returnValue(undefined);
        faelleListViewController = new FaelleListViewController($filter, gesuchModelManager,
            $state, $log, authServiceRS, searchRS);

        let tsGesuch = new TSGesuch();
        spyOn(gesuchRS, 'findGesuch').and.returnValue($q.when(tsGesuch));
        spyOn(gesuchRS, 'findGesuchForInstitution').and.returnValue($q.when(tsGesuch));

        faelleListViewController.editFall(mockAntrag, undefined);
        $scope.$apply();
        return tsGesuch;
    }
});

