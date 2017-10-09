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
import {EbeguWebCore} from '../../core/core.module';
import GesuchRS from './gesuchRS.rest';
import IHttpBackendService = angular.IHttpBackendService;

describe('gesuch', function () {

    let gesuchRS: GesuchRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let REST_API: string;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchRS = $injector.get('GesuchRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
        REST_API = $injector.get('REST_API');
    }));

    describe('Public API', function () {

        it('should include a createGesuch() function', function () {
            expect(gesuchRS.createGesuch).toBeDefined();
        });

        it('should include a findGesuch() function', function () {
            expect(gesuchRS.findGesuch).toBeDefined();
        });

        it('should include a updateGesuch() function', function () {
            expect(gesuchRS.updateGesuch).toBeDefined();
        });


    });

    describe('API Usage', function () {

    });
});
