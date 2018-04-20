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

import {IHttpInterceptor, ILogService, IQService, IRootScopeService} from 'angular';
import {TSVersionCheckEvent} from '../../events/TSVersionCheckEvent';

/**
 * this interceptor boradcasts a  VERSION_MATCH or VERSION_MISMATCH event whenever a rest service responds
 */
export default class HttpVersionInterceptor implements IHttpInterceptor {

    private backendVersion: string;

    static $inject = ['$rootScope', '$q', 'CONSTANTS', '$log'];

    /* @ngInject */
    constructor(private $rootScope: IRootScopeService, private $q: IQService, private CONSTANTS: any, private $log: ILogService) {
    }

    //interceptor methode
    public response = (response: any) => {
        if (response.headers && response.config && response.config.url.indexOf(this.CONSTANTS.REST_API) === 0 && !response.config.cache) {
            this.updateBackendVersion(response.headers('x-ebegu-version'));
        }

        return response;
    }

    /**
     * @param {*} newVersion
     */
    private updateBackendVersion(newVersion: string) {
        if (newVersion !== this.backendVersion) {
            this.backendVersion = newVersion;
            if (!this.hasVersionCompatibility(this.frontendVersion(), this.backendVersion)) {
                this.$log.warn('Versions of Frontend and Backend do not match');
                this.$rootScope.$broadcast(TSVersionCheckEvent[TSVersionCheckEvent.VERSION_MISMATCH]);
            } else {
                //could throw match event here but currently there is no action we want to perform when it matches
            }
        }
    }

    private hasVersionCompatibility(frontendVersion: string, backendVersion: string): boolean {
        // Wir erwarten, dass die Versionsnummern im Frontend und Backend immer synchronisiert werden
        return frontendVersion === backendVersion;
    }

    /**
     * @return {string}
     */
    public frontendVersion(): string {
        return VERSION;
    }

    public getBackendVersion(): string {
        return this.backendVersion;
    }

    public getBuildTime(): string {
        return BUILDTSTAMP;
    }

}
