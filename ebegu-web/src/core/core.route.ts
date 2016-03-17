/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.routes {
    'use strict';

    angular.module('ebeguWeb.core').run(appRun);

    /* @ngInject */
    export function appRun(routerHelper : IRouterHelper) {
        routerHelper.configureStates(getStates());
    }

    function getStates() {
        return [
            /* Add New States Above */
        ];
    }
}
