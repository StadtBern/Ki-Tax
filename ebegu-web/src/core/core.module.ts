/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb {
    'use strict';

    angular
        .module('ebeguWeb.core', [
            /* Angular modules */
            'ngAnimate',
            'ngSanitize',
            'ngCookies',
            /* shared DVBern modules */
            'dvbAngular.router',
            /* 3rd-party modules */
            'ui.bootstrap',
            'smart-table',
            'pascalprecht.translate'
        ]);
}
