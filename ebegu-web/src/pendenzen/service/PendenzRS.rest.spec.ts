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

import TSAntragDTO from '../../models/TSAntragDTO';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpBackendService} from 'angular';
import {EbeguWebPendenzen} from '../pendenzen.module';
import PendenzRS from './PendenzRS.rest';
import {TSAntragTyp} from '../../models/enums/TSAntragTyp';
import {TSBetreuungsangebotTyp} from '../../models/enums/TSBetreuungsangebotTyp';

describe('pendenzInstitutionRS', function () {

    let pendenzRS: PendenzRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockPendenz: TSAntragDTO;
    let mockPendenzRest: any;

    beforeEach(angular.mock.module(EbeguWebPendenzen.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        pendenzRS = $injector.get('PendenzRS');
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
            expect(pendenzRS.getServiceName()).toBe('PendenzRS');
        });
        it('should include a getPendenzenList() function', function () {
            expect(pendenzRS.getPendenzenList).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('findBetreuung', () => {
            it('should return all pending Antraege', () => {
                let arrayResult: Array<any> = [mockPendenzRest];
                $httpBackend.expectGET(pendenzRS.serviceURL + '/jugendamt/').respond(arrayResult);

                let foundPendenzen: Array<TSAntragDTO>;
                pendenzRS.getPendenzenList().then((result) => {
                    foundPendenzen = result;
                });
                $httpBackend.flush();
                expect(foundPendenzen).toBeDefined();
                expect(foundPendenzen.length).toBe(1);
                expect(foundPendenzen[0]).toEqual(mockPendenz);
            });
        });
    });

});
