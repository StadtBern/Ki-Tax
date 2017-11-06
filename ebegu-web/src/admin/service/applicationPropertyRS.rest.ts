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

import {IHttpPromise, IHttpService, IPromise} from 'angular';
import TSApplicationProperty from '../../models/TSApplicationProperty';
import EbeguRestUtil from '../../utils/EbeguRestUtil';

export class ApplicationPropertyRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil'];

    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil) {
        this.serviceURL = REST_API + 'application-properties';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    getByName(name: string): IPromise<TSApplicationProperty> {
        return this.http.get(this.serviceURL + '/key/' + encodeURIComponent(name)).then(
            (response: any) => this.ebeguRestUtil.parseApplicationProperty(new TSApplicationProperty(), response.data)
        );
    }

    isDevMode(): IPromise<boolean> {
        return this.http.get(this.serviceURL + '/public/devmode', {cache: true}).then((response) => {
            return response.data as boolean;
        });
    }

    isDummyMode(): IPromise<boolean> {
        return this.http.get(this.serviceURL + '/public/dummy').then((response) => {
            return response.data as boolean;
        });
    }

    getBackgroundColor(): IPromise<TSApplicationProperty> {
        return this.http.get(this.serviceURL + '/public/background').then((response) => {
            return this.ebeguRestUtil.parseApplicationProperty(new TSApplicationProperty, response.data);
        });
    }

    create(name: string, value: string): IHttpPromise<any> {
        return this.http.post(this.serviceURL + '/' + encodeURIComponent(name), value, {
            headers: {
                'Content-Type': 'text/plain'
            }
        });
    }

    update(name: string, value: string): IHttpPromise<any> {
        return this.http.post(this.serviceURL + '/' + encodeURIComponent(name), value, {
            headers: {
                'Content-Type': 'text/plain'
            }
        });
    }

    remove(name: string): IHttpPromise<any> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(name));
    }

    getAllApplicationProperties(): IPromise<TSApplicationProperty[]> {
        return this.http.get(this.serviceURL + '/').then(
            (response: any) => this.ebeguRestUtil.parseApplicationProperties(response.data)
        );
    }

    isZahlungenTestMode(): IPromise<boolean> {
        return this.http.get(this.serviceURL + '/public/zahlungentestmode', {cache: true}).then((response) => {
            return response.data as boolean;
        });
    }
}

