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

import {IHttpPromise, IHttpService, ILogService, IPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {TSTraegerschaft} from '../../models/TSTraegerschaft';

export class TraegerschaftRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'traegerschaften';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public findTraegerschaft(traegerschaftID: string): IPromise<TSTraegerschaft> {
        return this.http.get(this.serviceURL + '/id/' + encodeURIComponent(traegerschaftID))
            .then((response: any) => {
                this.log.debug('PARSING traegerschaft REST object ', response.data);
                return this.ebeguRestUtil.parseTraegerschaft(new TSTraegerschaft(), response.data);
            });
    }

    public createTraegerschaft(traegerschaft: TSTraegerschaft): IPromise<TSTraegerschaft> {
        return this.saveTraegerschaft(traegerschaft);
    }

    public updateTraegerschaft(traegerschaft: TSTraegerschaft): IPromise<TSTraegerschaft> {
        return this.saveTraegerschaft(traegerschaft);
    }

    private saveTraegerschaft(traegerschaft: TSTraegerschaft) {
        let restTraegerschaft = {};
        restTraegerschaft = this.ebeguRestUtil.traegerschaftToRestObject(restTraegerschaft, traegerschaft);
        return this.http.put(this.serviceURL, restTraegerschaft, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING traegerschaft REST object ', response.data);
            return this.ebeguRestUtil.parseTraegerschaft(new TSTraegerschaft(), response.data);
        });
    }

    synchronizeTraegerschaften(): IPromise<any> {
        return this.http.post(this.serviceURL + '/' + 'synchronizeWithOpenIdm', null, {
            headers: {
                'Content-Type': 'text/plain'
            }
        }).then((response: any) => {
            this.log.debug('synchronizeWithOpenIdm returns: ', response.data);
            return response;
        });
    }

    public removeTraegerschaft(traegerschaftID: string): IHttpPromise<TSTraegerschaft> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(traegerschaftID));
    }

    public getAllTraegerschaften(): IPromise<TSTraegerschaft[]> {
        return this.http.get(this.serviceURL).then((response: any) => {
            this.log.debug('PARSING traegerschaften REST array object', response.data);
            return this.ebeguRestUtil.parseTraegerschaften(response.data);
        });
    }

    public getAllActiveTraegerschaften(): IPromise<TSTraegerschaft[]> {
        return this.http.get(this.serviceURL + '/' + 'active').then((response: any) => {
            this.log.debug('PARSING traegerschaften REST array object', response.data);
            return this.ebeguRestUtil.parseTraegerschaften(response.data);
        });
    }


    public getServiceName(): string {
        return 'TraegerschaftRS';
    }

}
