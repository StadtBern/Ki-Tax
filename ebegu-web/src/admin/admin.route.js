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
            {
                state: 'admin',
                config: {
                    template: '<admin-view></admin-view>',
                    url: '/admin'
                }
            }
            /* Add New States Above */
        ];
    }
})();
