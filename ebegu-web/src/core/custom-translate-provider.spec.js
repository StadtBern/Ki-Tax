/*
 Workaround um den Async angular-translate loader in Unit Tests durch einen
 loader zu ersetzten, der keine REST requests macht
 https://angular-translate.github.io/docs/#/guide/22_unit-testing-with-angular-translate
 */
beforeEach(module('ebeguWeb.core', function ($provide, $translateProvider) {

    $provide.factory('customLoader', function ($q) {
        return function () {
            return $q.resolve({});
        };
    });

    $translateProvider.useLoader('customLoader');
}));
