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

import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import {InstitutionRS} from '../../../core/service/institutionRS.rest';
import {InstitutionStammdatenRS} from '../../../core/service/institutionStammdatenRS.rest';
import BerechnungsManager from '../../../gesuch/service/berechnungsManager';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import {TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import TSPendenzBetreuung from '../../../models/TSPendenzBetreuung';
import TestDataUtil from '../../../utils/TestDataUtil';
import {EbeguWebPendenzenBetreuungen} from '../../pendenzenBetreuungen.module';
import PendenzBetreuungenRS from '../../service/PendenzBetreuungenRS.rest';
import {PendenzenBetreuungenListViewController} from './pendenzenBetreuungenListView';

describe('pendenzenBetreuungenListView', function () {

    let institutionRS: InstitutionRS;
    let gesuchsperiodeRS: GesuchsperiodeRS;
    let institutionStammdatenRS: InstitutionStammdatenRS;
    let pendenzBetreuungenRS: PendenzBetreuungenRS;
    let pendenzBetreuungenListViewController: PendenzenBetreuungenListViewController;
    let $q: angular.IQService;
    let $scope: angular.IScope;
    let $httpBackend: angular.IHttpBackendService;
    let gesuchModelManager: GesuchModelManager;
    let berechnungsManager: BerechnungsManager;
    let $state: angular.ui.IStateService;
    let CONSTANTS: any;

    beforeEach(angular.mock.module(EbeguWebPendenzenBetreuungen.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        pendenzBetreuungenRS = $injector.get('PendenzBetreuungenRS');
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
                let mockPendenz: TSPendenzBetreuung = mockGetPendenzenList();
                mockRestCalls();
                spyOn(gesuchsperiodeRS, 'getAllActiveGesuchsperioden').and.returnValue($q.when([TestDataUtil.createGesuchsperiode20162017()]));
                pendenzBetreuungenListViewController = new PendenzenBetreuungenListViewController(pendenzBetreuungenRS, undefined,
                    institutionRS, institutionStammdatenRS, gesuchsperiodeRS, gesuchModelManager, berechnungsManager, $state);
                pendenzBetreuungenListViewController.$onInit();

                $scope.$apply();
                expect(pendenzBetreuungenRS.getPendenzenBetreuungenList).toHaveBeenCalled();

                let list: Array<TSPendenzBetreuung> = pendenzBetreuungenListViewController.getPendenzenList();
                expect(list).toBeDefined();
                expect(list.length).toBe(1);
                expect(list[0]).toEqual(mockPendenz);
            });
        });
    });

    function mockGetPendenzenList(): TSPendenzBetreuung {
        let mockPendenz: TSPendenzBetreuung = new TSPendenzBetreuung('123.12.12.12', '123', '123', '123', 'Kind', 'Kilian', undefined,
            'Platzbestaetigung', undefined, undefined, undefined, TSBetreuungsangebotTyp.KITA, undefined);
        let result: Array<TSPendenzBetreuung> = [mockPendenz];
        spyOn(pendenzBetreuungenRS, 'getPendenzenBetreuungenList').and.returnValue($q.when(result));
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
