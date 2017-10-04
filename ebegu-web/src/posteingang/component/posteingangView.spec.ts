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
import {IScope, IQService, IFilterService, IHttpBackendService} from 'angular';
import GesuchModelManager from '../../gesuch/service/gesuchModelManager';
import BerechnungsManager from '../../gesuch/service/berechnungsManager';
import {IStateService} from 'angular-ui-router';
import {EbeguWebPosteingang} from '../posteingang.module';
import {TSBetreuungsangebotTyp} from '../../models/enums/TSBetreuungsangebotTyp';
import TestDataUtil from '../../utils/TestDataUtil';
import {PosteingangViewController} from './posteingangView';
import WizardStepManager from '../../gesuch/service/wizardStepManager';
import TSMitteilung from '../../models/TSMitteilung';
import {TSAntragTyp} from '../../models/enums/TSAntragTyp';
import TSGesuch from '../../models/TSGesuch';
import TSAntragSearchresultDTO from '../../models/TSAntragSearchresultDTO';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {TSAntragStatus} from '../../models/enums/TSAntragStatus';
import TSFall from '../../models/TSFall';
import {TSMitteilungTeilnehmerTyp} from '../../models/enums/TSMitteilungTeilnehmerTyp';
import TSUser from '../../models/TSUser';
import {TSRole} from '../../models/enums/TSRole';
import {TSMitteilungStatus} from '../../models/enums/TSMitteilungStatus';
import MitteilungRS from '../../core/service/mitteilungRS.rest';
import EbeguUtil from '../../utils/EbeguUtil';


describe('posteingangView', function () {

    let authServiceRS: AuthServiceRS;
    let gesuchRS: GesuchRS;
    let ebeguUtil: EbeguUtil;
    let mitteilungRS: MitteilungRS;
    let posteingangViewController: PosteingangViewController;
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
    let mockMitteilung: TSMitteilung;


    beforeEach(angular.mock.module(EbeguWebPosteingang.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        authServiceRS = $injector.get('AuthServiceRS');
        mitteilungRS = $injector.get('MitteilungRS');
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
        mockMitteilung = mockGetMitteilung();
    }));

    describe('API Usage', function () {
        describe('getMitteilungen', function () {
            it('should return the list of Mitteilungen', function () {
                mockRestCalls();
                posteingangViewController = new PosteingangViewController(mitteilungRS, ebeguUtil, CONSTANTS, undefined);
                $scope.$apply();
                expect(mitteilungRS.getMitteilungenForPosteingang).toHaveBeenCalled();
                let list: Array<TSMitteilung> = posteingangViewController.getMitteilungen();
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
        spyOn(mitteilungRS, 'getMitteilungenForPosteingang').and.returnValue($q.when(dtoList));
        return mockMitteilung;
    }

    function mockRestCalls(): void {
        TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
        $httpBackend.when('GET', '/ebegu/api/v1/institutionen').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/benutzer').respond({});
        $httpBackend.when('GET', '/ebegu/api/v1/gesuchsperioden/active').respond({});
    }
});

