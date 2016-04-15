/*
 Workaround um den Async angular-translate loader in Unit Tests durch einen
 loader zu ersetzten, der keine REST requests macht
 https://angular-translate.github.io/docs/#/guide/22_unit-testing-with-angular-translate
 */
import IProvideService = angular.auto.IProvideService;
import {ITranslateProvider} from 'angular-translate';
import {IQService} from 'angular';

beforeEach(angular.mock.module('ebeguWeb.core', function ($provide: IProvideService, $translateProvider: ITranslateProvider) {

    $provide.factory('customLoader', function ($q: IQService) {
        return function () {
            return $q.resolve({});
        };
    });

    $translateProvider.useLoader('customLoader');
}));