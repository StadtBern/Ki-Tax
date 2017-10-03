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

/*
 Workaround um den Async angular-translate loader in Unit Tests durch einen
 loader zu ersetzten, der keine REST requests macht
 https://angular-translate.github.io/docs/#/guide/22_unit-testing-with-angular-translate
 */
import 'angular';
import 'angular-mocks';
import 'angular-translate';
import IProvideService = angular.auto.IProvideService;

/*
beforeEach(angular.mock.module('ebeguWeb.core', function ($provide: IProvideService, $translateProvider: ITranslateProvider) {

    $provide.factory('customLoader', function ($q: IQService) {
        return function () {
            return $q.resolve({});
        };
    });
    $translateProvider.useLoader('customLoader');
}));
*/
