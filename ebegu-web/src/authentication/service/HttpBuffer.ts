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
            for (var i = 0; i < this.buffer.length; ++i) {
                this.buffer[i].deferred.reject(reason);
            }
        }
        this.buffer = [];
    }

    /**
     * Retries all the buffered requests clears the buffer.
     */
    public retryAll(updater: any) {
        for (var i = 0; i < this.buffer.length; ++i) {
            this.retryHttpRequest(updater(this.buffer[i].config), this.buffer[i].deferred);
        }
        this.buffer = [];
    }
}
