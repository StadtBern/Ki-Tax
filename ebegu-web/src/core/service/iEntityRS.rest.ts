/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.services {
    'use strict';

    export interface IEntityRS {
        serviceURL: string;
        http: angular.IHttpService;
        ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil;
    }

}
