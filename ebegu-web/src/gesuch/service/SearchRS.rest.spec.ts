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

import {TSAntragTyp} from '../../models/enums/TSAntragTyp';
import {TSBetreuungsangebotTyp} from '../../models/enums/TSBetreuungsangebotTyp';
import TSAntragDTO from '../../models/TSAntragDTO';
import TSAntragSearchresultDTO from '../../models/TSAntragSearchresultDTO';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import SearchRS from './searchRS.rest';
import {EbeguWebCore} from '../../core/core.module';

describe('searchRS', function () {

    let searchRS: SearchRS;
    let $httpBackend: angular.IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockPendenz: TSAntragDTO;
    let mockPendenzRest: any;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        searchRS = $injector.get('SearchRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));

    beforeEach(() => {
        mockPendenz = new TSAntragDTO('id1', 123, 'name', TSAntragTyp.ERSTGESUCH, undefined, undefined, undefined,
            [TSBetreuungsangebotTyp.KITA], ['Inst1, Inst2'], 'Juan Arbolado', undefined, undefined, undefined, undefined, undefined);
        mockPendenzRest = ebeguRestUtil.antragDTOToRestObject({}, mockPendenz);
    });

    describe('Public API', function () {
        it('check Service name', function () {
            expect(searchRS.getServiceName()).toBe('SearchRS');
        });
        it('should include a getPendenzenList() function', function () {
            expect(searchRS.getPendenzenList).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('findBetreuung', () => {
            it('should return all pending Antraege', () => {
                let tsAntragDTO: TSAntragDTO = new TSAntragDTO();
                tsAntragDTO.fallNummer = 1234;
                let searchResult: any = {
                    antragDTOs: [tsAntragDTO],
                    paginationDTO: {totalItemCount: 1}
                };

                let filter: any = {};
                $httpBackend.expectPOST(searchRS.serviceURL + '/jugendamt/', filter).respond(searchResult);

                let foundPendenzen: TSAntragSearchresultDTO;
                searchRS.getPendenzenList(filter).then((result) => {
                    foundPendenzen = result;
                });
                $httpBackend.flush();
                expect(foundPendenzen).toBeDefined();
                expect(foundPendenzen.totalResultSize).toBe(1);
                expect(foundPendenzen.antragDTOs[0]).toEqual(tsAntragDTO);
            });
        });
    });

});
