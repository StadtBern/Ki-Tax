(function () {
    'use strict';

    angular.module('ebeguWeb.admin').factory('applicationPropertyRS', applicationPropertyRS);

    applicationPropertyRS.$inject = ['$http', 'REST_API'];

    function applicationPropertyRS($http, REST_API) {

        var serviceURL = REST_API + 'application-properties';
        //noinspection UnnecessaryLocalVariableJS
        var srv = {
            // Public API
            getByKey: getByKey,
            create: create

        };

        return srv;

        ////////////

        function getByKey(key) {
            return $http.get(serviceURL + '/' + encodeURIComponent(key));

        }

        function create(key, value) {
            return $http.post(serviceURL + '/' + encodeURIComponent(key), value, {
                headers: {
                    'Content-Type': 'text/plain'
                }
            });

        }

    }
})();
