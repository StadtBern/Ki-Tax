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

import {EbeguWebPendenzenInstitution} from '../../pendenzenInstitution.module';
import PendenzInstitutionRS from '../../service/PendenzInstitutionRS.rest';
import {PendenzenInstitutionListViewController} from './pendenzenInstitutionListView';
import {IScope, IQService, IHttpBackendService} from 'angular';
import TSPendenzInstitution from '../../../models/TSPendenzInstitution';
import {TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import {InstitutionRS} from '../../../core/service/institutionRS.rest';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import TestDataUtil from '../../../utils/TestDataUtil';
import BerechnungsManager from '../../../gesuch/service/berechnungsManager';
import {InstitutionStammdatenRS} from '../../../core/service/institutionStammdatenRS.rest';

describe('pendenzenInstitutionListView', function () {

    let institutionRS: InstitutionRS;
    let gesuchsperiodeRS: GesuchsperiodeRS;
    let institutionStammdatenRS: InstitutionStammdatenRS;
    let pendenzInstitutionRS: PendenzInstitutionRS;
    let pendenzInstitutionListViewController: PendenzenInstitutionListViewController;
    let $q: IQService;
    let $scope: IScope;
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
        $q = $injector.get('$q');
        $scope = $injector.get('$rootScope');
        $httpBackend = $injector.get('$httpBackend');
        gesuchModelManager = $injector.get('GesuchModelManager');
        berechnungsManager = $injector.get('BerechnungsManager');
        $state = $injector.get('$state');
        CONSTANTS = $injector.get('CONSTANTS');
    }));

    describe('API Usage', function () {
        describe('initFinSit Pendenzenliste', function () {
            it('should return the list with all pendenzen', function () {
                let mockPendenz: TSPendenzInstitution = mockGetPendenzenList();
                mockRestCalls();
                spyOn(gesuchsperiodeRS, 'getAllActiveGesuchsperioden').and.returnValue($q.when([TestDataUtil.createGesuchsperiode20162017()]));
                pendenzInstitutionListViewController = new PendenzenInstitutionListViewController(pendenzInstitutionRS, undefined,
                    institutionRS, institutionStammdatenRS, gesuchsperiodeRS, gesuchModelManager, berechnungsManager, $state);
                pendenzInstitutionListViewController.$onInit();

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
        let mockPendenz: TSPendenzInstitution = new TSPendenzInstitution('123.12.12.12', '123', '123', '123', 'Kind', 'Kilian', undefined,
            'Platzbestaetigung', undefined, undefined, undefined, TSBetreuungsangebotTyp.KITA, undefined);
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
