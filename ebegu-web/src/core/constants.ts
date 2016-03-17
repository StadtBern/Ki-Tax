/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.utils {
    'use strict';

    angular
        .module('ebeguWeb.core')
        .constant('REST_API', '/ebegu/api/v1/')
        .constant('MAX_LENGTH', 255)
        .constant('CONFIG', {
            name: 'EBEGU',
            REST_API: '/ebegu/api/v1/'
        })

}
