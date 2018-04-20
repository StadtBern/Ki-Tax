/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import IInjectorService = angular.auto.IInjectorService;
import {IDeferred, IHttpService, IRequestConfig} from 'angular';

/**
 * Code from Kita-Projekt, den wir in Typescript geschrieben haben
 * Adapted from https://github.com/witoldsz/angular-http-auth
 */
export default class HttpBuffer {

    /** Holds all the requests, so they can be re-requested in future. */
    buffer: Array<any> = [];

    /** Service initialized later because of circular dependency problem. */
    $http: IHttpService;

    static $inject = ['$injector'];
    constructor(private $injector: IInjectorService) {
    }

    private retryHttpRequest(config: IRequestConfig, deferred: IDeferred<any>) {
        function successCallback(response: any) {
            deferred.resolve(response);
        }

        function errorCallback(response: any) {
            deferred.reject(response);
        }

        this.$http = this.$http || this.$injector.get('$http');
        this.$http(config).then(successCallback, errorCallback);
    }


    /**
     * Appends HTTP request configuration object with deferred response attached to buffer.
     */
    public append(config: IRequestConfig, deferred: IDeferred<any>) {
        this.buffer.push({
            config: config,
            deferred: deferred
        });
    }

    /**
     * Abandon or reject (if reason provided) all the buffered requests.
     */
    public rejectAll(reason: any) {
        if (reason) {
            for (let i = 0; i < this.buffer.length; ++i) {
                this.buffer[i].deferred.reject(reason);
            }
        }
        this.buffer = [];
    }

    /**
     * Retries all the buffered requests clears the buffer.
     */
    public retryAll(updater: any) {
        for (let i = 0; i < this.buffer.length; ++i) {
            this.retryHttpRequest(updater(this.buffer[i].config), this.buffer[i].deferred);
        }
        this.buffer = [];
    }
}
