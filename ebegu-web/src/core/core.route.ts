/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.routes {
    'use strict';

    angular.module('ebeguWeb.core').run(appRun);

    /* @ngInject */
    export function appRun(angularMomentConfig : any ,routerHelper : IRouterHelper, listResourceRS: ebeguWeb.services.ListResourceRS) {
        routerHelper.configureStates(getStates());
        angularMomentConfig.format = 'DD.MM.YYYY';
        listResourceRS.getLaenderList();
    }

    function getStates() {
        return [
            /* Add New States Above */
        ];
    }
}
