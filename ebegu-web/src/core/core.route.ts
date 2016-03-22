/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.routes {
    'use strict';

    angular.module('ebeguWeb.core').run(appRun);

    /* @ngInject */
    export function appRun(angularMomentConfig : any ,routerHelper : IRouterHelper) {
        routerHelper.configureStates(getStates());
        angularMomentConfig.format = 'DD.MM.YYYY'

    }

    function getStates() {
        return [
            /* Add New States Above */
        ];
    }
}
