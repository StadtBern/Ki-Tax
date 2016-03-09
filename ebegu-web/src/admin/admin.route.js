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
                    template: '<admin-view application-properties="vm.applicationProperties"></admin-view>',
                    url: '/admin',
                    controller: function (applicationProperties) {
                        var vm = this;
                        vm.applicationProperties = applicationProperties;
                    },
                    controllerAs: 'vm',
                    resolve: {
                        applicationProperties: applicationProperties
                    }
                }

            }
            /* Add New States Above */
        ];
    }

    applicationProperties.$inject = ['applicationPropertyRS'];

    function applicationProperties(applicationPropertyRS) {
        return applicationPropertyRS.getAllApplicationProperties()
            .then(function (response) {
                return response.data;
            });
    }
})();
