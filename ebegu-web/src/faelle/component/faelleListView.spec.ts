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

import GesuchRS from '../../gesuch/service/gesuchRS.rest';
import {IFilterService, IHttpBackendService, IQService, IScope} from 'angular';
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
import {TSAntragStatus} from '../../models/enums/TSAntragStatus';

describe('faelleListView', function () {

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
                    gesuchModelManager, berechnungsManager, $state, $log, CONSTANTS, authServiceRS, $q);


                faelleListViewController.passFilterToServer({});
                expect(gesuchRS.searchAntraege).toHaveBeenCalledTimes(1);
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

                expect($state.go).toHaveBeenCalledWith('gesuch.fallcreation', { createNew: false, gesuchId: '66345345' });

            });
            it('should call findGesuch and open the view gesuch.betreuungen with it for INS/TRAEGER user if gesuch not verfuegt', function () {
                spyOn(authServiceRS, 'isOneOfRoles').and.returnValue(true);
                let tsGesuch = callEditFall();
                expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen', { createNew: false, gesuchId: '66345345' });
            });
            it('should call findGesuch and open the view gesuch.verfuegen with it for INS/TRAEGER user if gesuch verfuegt', function () {
               spyOn(authServiceRS, 'isOneOfRoles').and.returnValue(true);
                mockAntrag.status = TSAntragStatus.VERFUEGT;
               let tsGesuch = callEditFall();
               expect($state.go).toHaveBeenCalledWith('gesuch.verfuegen', { createNew: false, gesuchId: '66345345' });
           });
        });
    });

    function mockGetPendenzenList(): TSAntragDTO {
        let mockPendenz: TSAntragDTO = new TSAntragDTO('66345345', 123, 'name', TSAntragTyp.ERSTGESUCH,
            undefined, undefined, undefined, [TSBetreuungsangebotTyp.KITA], ['Inst1, Inst2'], 'Juan Arbolado', undefined, undefined, undefined);
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

    function callEditFall(): TSGesuch {
        mockRestCalls();
        spyOn($state, 'go');
        spyOn(wizardStepManager, 'findStepsFromGesuch').and.returnValue(undefined);
        faelleListViewController = new FaelleListViewController($filter, gesuchRS,
            gesuchModelManager, berechnungsManager, $state, $log, CONSTANTS, authServiceRS, $q);

        let tsGesuch = new TSGesuch();
        spyOn(gesuchRS, 'findGesuch').and.returnValue($q.when(tsGesuch));
        spyOn(gesuchRS, 'findGesuchForInstitution').and.returnValue($q.when(tsGesuch));

        faelleListViewController.editFall(mockAntrag, undefined);
        $scope.$apply();
        return tsGesuch;
    }
});

