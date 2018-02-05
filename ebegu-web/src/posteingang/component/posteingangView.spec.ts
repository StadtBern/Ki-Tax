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
                posteingangViewController = new PosteingangViewController(mitteilungRS, ebeguUtil, CONSTANTS, undefined, undefined, $log);
                $rootScope.$apply();
                expect(mitteilungRS.searchMitteilungen).toHaveBeenCalled();
                let list: Array<TSMitteilung> = posteingangViewController.displayedCollection;
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
        spyOn(mitteilungRS, 'searchMitteilungen').and.returnValue($q.when(dtoList));
        return mockMitteilung;
    }

    function mockRestCalls(): void {
        TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
        $httpBackend.when('GET', '/ebegu/api/v1/institutionen').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/benutzer').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/gesuchsperioden/active').respond({});
    }
});

