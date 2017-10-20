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

import WizardStepManager from '../../gesuch/service/wizardStepManager';
import TSBetreuung from '../../models/TSBetreuung';
import TSBetreuungsmitteilung from '../../models/TSBetreuungsmitteilung';
import TSFall from '../../models/TSFall';
import TSKindContainer from '../../models/TSKindContainer';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {EbeguWebCore} from '../core.module';
import MitteilungRS from './mitteilungRS.rest';

describe('MitteilungRS', function () {

    let mitteilungRS: MitteilungRS;
    let $httpBackend: angular.IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockKind: TSKindContainer;
    let mockKindRest: any;
    let gesuchId: string;
    let $q: angular.IQService;
    let wizardStepManager: WizardStepManager;
    let $rootScope: angular.IRootScopeService;
    let fall: TSFall;
    let betreuung: TSBetreuung;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        mitteilungRS = $injector.get('MitteilungRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
        wizardStepManager = $injector.get('WizardStepManager');
        $q = $injector.get('$q');
        $rootScope = $injector.get('$rootScope');
        fall = new TSFall();
        betreuung = new TSBetreuung();
        betreuung.betreuungNummer = 123;
    }));

    describe('Public API', function () {
        it('check URI', function () {
            expect(mitteilungRS.serviceURL).toContain('mitteilungen');
        });
        it('check Service name', function () {
            expect(mitteilungRS.getServiceName()).toBe('MitteilungRS');
        });
    });
    describe('sendbetreuungsmitteilung', function () {
        it('should create the betreuungsmitteilung and send it', function () {
            let restMitteilung: any = {};
            let bm: TSBetreuungsmitteilung = new TSBetreuungsmitteilung();
            bm.betreuung = betreuung;
            spyOn(ebeguRestUtil, 'betreuungsmitteilungToRestObject').and.returnValue(restMitteilung);
            spyOn(ebeguRestUtil, 'parseBetreuungsmitteilung').and.returnValue(bm);
            $httpBackend.expectPUT(mitteilungRS.serviceURL + '/sendbetreuungsmitteilung', restMitteilung).respond($q.when(restMitteilung));

            let result: angular.IPromise<TSBetreuungsmitteilung> = mitteilungRS.sendbetreuungsmitteilung(fall, betreuung);
            $httpBackend.flush();
            $rootScope.$apply();

            expect(result).toBeDefined();
            result.then(response => {
                expect(response.betreuung).toBe(betreuung);
            });
            $rootScope.$apply();

        });
    });
    describe('applybetreuungsmitteilung', function () {
        it('should call the services to apply the betreuungsmitteilung', function () {
            let mitteilung: TSBetreuungsmitteilung = new TSBetreuungsmitteilung();
            mitteilung.id = '987654321';

            spyOn(ebeguRestUtil, 'parseBetreuungsmitteilung').and.returnValue(betreuung);
            $httpBackend.expectPUT(mitteilungRS.serviceURL + '/applybetreuungsmitteilung/' + mitteilung.id, null).respond($q.when({id: '123456'}));

            let result: angular.IPromise<any> = mitteilungRS.applyBetreuungsmitteilung(mitteilung.id);
            $httpBackend.flush();
            $rootScope.$apply();

            expect(result).toBeDefined();
            result.then(response => {
                expect(response).toEqual({id: '123456'});
            });
            $rootScope.$apply();

        });
    });
});
