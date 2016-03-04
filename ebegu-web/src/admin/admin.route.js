(function () {
    'use strict';

    angular
        .module('ebeguWeb.admin').run(ebeguWebAdminRun);

    /* @ngInject */
    function ebeguWebAdminRun(routerHelper) {
        routerHelper.configureStates(getStates());
    }

    function getStates() {
        return [
            /* Add New States Above */
        ];
    }
})();
