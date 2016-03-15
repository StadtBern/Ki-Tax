/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.config {
    'use strict';

    angular.module('ebeguWeb.core').config(configure);
    /* @ngInject */
    export function configure($translateProvider: angular.translate.ITranslateProvider) {

        $translateProvider.useSanitizeValueStrategy('escapeParameters');
        $translateProvider.useStaticFilesLoader({
                prefix: 'src/translations/translations_',
                suffix: '.json'
            })
            .fallbackLanguage('de')
            .preferredLanguage('de');

    }

}
