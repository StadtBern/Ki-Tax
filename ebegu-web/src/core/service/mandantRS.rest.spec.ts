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

import {TSMandant} from '../../models/TSMandant';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {EbeguWebCore} from '../core.module';
import {MandantRS} from './mandantRS.rest';

describe('mandantRS', function () {

    let mandantRS: MandantRS;
    let $httpBackend: angular.IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockMandant: TSMandant;
    let mockMandantRest: any;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        mandantRS = $injector.get('MandantRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));

    beforeEach(() => {
        mockMandant = new TSMandant('MandantTest');
        mockMandant.id = '2afc9d9a-957e-4550-9a22-97624a1d8fa1';
        mockMandantRest = ebeguRestUtil.mandantToRestObject({}, mockMandant);
    });

    describe('Public API', function () {
        it('check Service name', function () {
            expect(mandantRS.getServiceName()).toBe('MandantRS');

        });
        it('should include a findMandant() function', function () {
            expect(mandantRS.findMandant).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('findMandant', () => {
            it('should return the mandant by id', () => {
                $httpBackend.expectGET(mandantRS.serviceURL + '/id/' + encodeURIComponent(mockMandant.id)).respond(mockMandantRest);

                let foundMandant: TSMandant;
                mandantRS.findMandant(mockMandant.id).then((result) => {
                    foundMandant = result;
                });
                $httpBackend.flush();
                expect(foundMandant).toBeDefined();
                expect(foundMandant.name).toEqual(mockMandant.name);
            });
        });
    });
});
