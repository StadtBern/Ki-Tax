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

import {IHttpPromise, IHttpService, ILogService, IPromise, IQService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSGesuchsperiode from '../../models/TSGesuchsperiode';

export default class GesuchsperiodeRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    private activeGesuchsperiodenList: Array<TSGesuchsperiode>;
    private nichtAbgeschlosseneGesuchsperiodenList: Array<TSGesuchsperiode>;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', '$q'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService, private $q: IQService) {
        this.serviceURL = REST_API + 'gesuchsperioden';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'GesuchsperiodeRS';
    }

    public findGesuchsperiode(gesuchsperiodeID: string): IPromise<TSGesuchsperiode> {
        return this.http.get(this.serviceURL + '/gesuchsperiode/' + encodeURIComponent(gesuchsperiodeID))
            .then((response: any) => {
                this.log.debug('PARSING Gesuchsperiode REST object ', response.data);
                return this.ebeguRestUtil.parseGesuchsperiode(new TSGesuchsperiode(), response.data);
            });
    }

    public createGesuchsperiode(gesuchsperiode: TSGesuchsperiode): IPromise<TSGesuchsperiode> {
        return this.saveGesuchsperiode(gesuchsperiode);
    }

    public updateGesuchsperiode(gesuchsperiode: TSGesuchsperiode): IPromise<TSGesuchsperiode> {
        return this.saveGesuchsperiode(gesuchsperiode);
    }

    private saveGesuchsperiode(gesuchsperiode: TSGesuchsperiode): IPromise<TSGesuchsperiode> {
        let restGesuchsperiode = {};
        restGesuchsperiode = this.ebeguRestUtil.gesuchsperiodeToRestObject(restGesuchsperiode, gesuchsperiode);
        return this.http.put(this.serviceURL, restGesuchsperiode, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING Gesuchsperiode REST object ', response.data);
            return this.ebeguRestUtil.parseGesuchsperiode(new TSGesuchsperiode(), response.data);
        });
    }

    public removeGesuchsperiode(gesuchsperiodeId: string): IHttpPromise<TSGesuchsperiode> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(gesuchsperiodeId));
    }

    public updateActiveGesuchsperiodenList(): IPromise<TSGesuchsperiode[]> {
        return this.http.get(this.serviceURL + '/active').then((response: any) => {
            let gesuchsperioden: TSGesuchsperiode[] = this.ebeguRestUtil.parseGesuchsperioden(response.data);
            this.activeGesuchsperiodenList = angular.copy(gesuchsperioden);
            return this.activeGesuchsperiodenList;
        });
    }

    public getAllActiveGesuchsperioden(): IPromise<TSGesuchsperiode[]> {
        if (!this.activeGesuchsperiodenList || this.activeGesuchsperiodenList.length <= 0) { // if the list is empty, reload it
            return this.updateActiveGesuchsperiodenList().then(() => {
                return this.activeGesuchsperiodenList;
            });
        }
        return this.$q.when(this.activeGesuchsperiodenList); // we need to return a promise
    }

    public getAllGesuchsperioden(): IPromise<TSGesuchsperiode[]> {
        return this.http.get(this.serviceURL + '/').then((response: any) => {
            return this.ebeguRestUtil.parseGesuchsperioden(response.data);
        });
    }

    public updateNichtAbgeschlosseneGesuchsperiodenList(): IPromise<TSGesuchsperiode[]> {
        return this.http.get(this.serviceURL + '/unclosed').then((response: any) => {
            let gesuchsperioden: TSGesuchsperiode[] = this.ebeguRestUtil.parseGesuchsperioden(response.data);
            this.nichtAbgeschlosseneGesuchsperiodenList = angular.copy(gesuchsperioden);
            return this.nichtAbgeschlosseneGesuchsperiodenList;
        });
    }

    public getAllNichtAbgeschlosseneGesuchsperioden(): IPromise<TSGesuchsperiode[]> {
        if (!this.nichtAbgeschlosseneGesuchsperiodenList || this.nichtAbgeschlosseneGesuchsperiodenList.length <= 0) { // if the list is empty, reload it
            return this.updateNichtAbgeschlosseneGesuchsperiodenList().then(() => {
                return this.nichtAbgeschlosseneGesuchsperiodenList;
            });
        }
        return this.$q.when(this.nichtAbgeschlosseneGesuchsperiodenList); // we need to return a promise
    }

    public getAllNichtAbgeschlosseneNichtVerwendeteGesuchsperioden(fallId: string): IPromise<TSGesuchsperiode[]> {
        return this.http.get(this.serviceURL + '/unclosed/' + fallId).then((response: any) => {
            return this.ebeguRestUtil.parseGesuchsperioden(response.data);
        });
    }

    public getNewestGesuchsperiode(): IPromise<TSGesuchsperiode> {
        return this.http.get(this.serviceURL + '/newestGesuchsperiode/')
            .then((response: any) => {
                this.log.debug('PARSING Gesuchsperiode REST object ', response.data);
                return this.ebeguRestUtil.parseGesuchsperiode(new TSGesuchsperiode(), response.data);
            });
    }
}
