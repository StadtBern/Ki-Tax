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

import {EbeguWebAdmin} from '../admin.module';
import {TestFaelleRS} from './testFaelleRS.rest';

describe('TestFaelleRS', function () {

    let testFaelleRS: TestFaelleRS;
    let $httpBackend: angular.IHttpBackendService;

    beforeEach(angular.mock.module(EbeguWebAdmin.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        testFaelleRS = $injector.get('TestFaelleRS');
        $httpBackend = $injector.get('$httpBackend');
    }));

    describe('Public API', function () {
        it('check URI', function () {
            expect(testFaelleRS.serviceURL).toContain('testfaelle');
        });
        it('check Service name', function () {
            expect(testFaelleRS.getServiceName()).toBe('TestFaelleRS');
        });
        it('should include a createTestFall() function', function () {
            expect(testFaelleRS.createTestFall).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('createTestFall', () => {
            it('should call createTestFall', () => {
                $httpBackend.expectGET(testFaelleRS.serviceURL + '/testfall/' + encodeURIComponent('1') + '/null/false/false').respond({});
                testFaelleRS.createTestFall('1', null, false, false);
                $httpBackend.flush();
            });
        });
    });
});
