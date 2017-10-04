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

import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpBackendService} from 'angular';
import {EbeguWebPendenzenInstitution} from '../pendenzenInstitution.module';
import PendenzInstitutionRS from './PendenzInstitutionRS.rest';
import {TSBetreuungsangebotTyp} from '../../models/enums/TSBetreuungsangebotTyp';
import TSPendenzInstitution from '../../models/TSPendenzInstitution';

describe('pendenzInstitutionRS', function () {

    let pendenzInstitutionRS: PendenzInstitutionRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockPendenzInstitution: TSPendenzInstitution;
    let mockPendenzInstitutionRest: any;

    beforeEach(angular.mock.module(EbeguWebPendenzenInstitution.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        pendenzInstitutionRS = $injector.get('PendenzInstitutionRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));

    beforeEach(() => {
        mockPendenzInstitution = new TSPendenzInstitution('123.12.12', '123',  '123',  '123', 'Kind', 'Kilian', undefined, 'Platzbestaetigung', undefined,
            undefined, undefined, TSBetreuungsangebotTyp.KITA, undefined);
        mockPendenzInstitutionRest = ebeguRestUtil.pendenzInstitutionToRestObject({}, mockPendenzInstitution);
    });

    describe('Public API', function () {
        it('check Service name', function () {
            expect(pendenzInstitutionRS.getServiceName()).toBe('PendenzInstitutionRS');
        });
        it('should include a getPendenzenList() function', function () {
            expect(pendenzInstitutionRS.getPendenzenList).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('findBetreuung', () => {
            it('should return all pending Antraege', () => {
                let arrayResult: Array<any> = [mockPendenzInstitutionRest];
                $httpBackend.expectGET(pendenzInstitutionRS.serviceURL).respond(arrayResult);

                let foundPendenzen: Array<TSPendenzInstitution>;
                pendenzInstitutionRS.getPendenzenList().then((result) => {
                    foundPendenzen = result;
                });
                $httpBackend.flush();
                expect(foundPendenzen).toBeDefined();
                expect(foundPendenzen.length).toBe(1);
                expect(foundPendenzen[0]).toEqual(mockPendenzInstitution);
            });
        });
    });
});
