/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.config {
    'use strict';

    angular.module('ebeguWeb.core').config(configure);
    /* @ngInject */
    function configure($translateProvider) {

        $translateProvider.useSanitizeValueStrategy('escapeParameters');
        $translateProvider.useStaticFilesLoader({
                prefix: 'src/translations/translations_',
                suffix: '.json'
            })
            .fallbackLanguage('de')
            .preferredLanguage('de');

    }

}
