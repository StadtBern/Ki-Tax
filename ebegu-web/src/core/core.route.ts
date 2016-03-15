/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.routes {
    'use strict';

    angular.module('ebeguWeb.core').run(appRun);

    /* @ngInject */
    function appRun(routerHelper) {
        routerHelper.configureStates(getStates());
    }

    function getStates() {
        return [
            /* Add New States Above */
        ];
    }
}
