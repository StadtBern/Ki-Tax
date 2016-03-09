(function () {
    'use strict';

    angular
        .module('ebeguWeb.core')
        .constant('REST_API', '/ebegu/api/v1/')
        .constant('MAX_LENGTH', 255)
        .constant('angularMomentConfig', {timezone: ''});

})();
