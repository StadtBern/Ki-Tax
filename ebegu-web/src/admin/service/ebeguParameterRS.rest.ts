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

import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSEbeguParameter from '../../models/TSEbeguParameter';
import {IHttpResponse, IHttpService, IPromise} from 'angular';
import {TSEbeguParameterKey} from '../../models/enums/TSEbeguParameterKey';
import DateUtil from '../../utils/DateUtil';
import * as moment from 'moment';
import ICacheObject = angular.ICacheObject;


export class EbeguParameterRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil) {
        this.serviceURL = REST_API + 'parameter';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public saveEbeguParameter(tsEbeguParameter: TSEbeguParameter): IPromise<TSEbeguParameter> {
        let restEbeguParameter = {};
        restEbeguParameter = this.ebeguRestUtil.ebeguParameterToRestObject(restEbeguParameter, tsEbeguParameter);
        return this.http.put(this.serviceURL, restEbeguParameter, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
                return this.ebeguRestUtil.parseEbeguParameter(new TSEbeguParameter(), response.data);
            }
        );
    }

    public getAllEbeguParameter(): IPromise<TSEbeguParameter[]> {
        return this.http.get(this.serviceURL + '/all').then(
            (response: any) => {
                return this.ebeguRestUtil.parseEbeguParameters(response.data);
            }
        );
    }

    public getAllEbeguParameterByDate(dateParam: moment.Moment): IPromise<TSEbeguParameter[]> {
        return this.http.get(this.serviceURL + '/date', {params: {date: DateUtil.momentToLocalDate(dateParam)}})
            .then((response: any) => {
                return this.ebeguRestUtil.parseEbeguParameters(response.data);
            });
    }

    public getEbeguParameterByGesuchsperiode(gesuchsperiodeId: string): IPromise<TSEbeguParameter[]> {
        return this.http.get(this.serviceURL + '/gesuchsperiode/' + gesuchsperiodeId)
            .then((response: any) => {
                return this.ebeguRestUtil.parseEbeguParameters(response.data);
            });
    }

    public getEbeguParameterByGesuchsperiodeCached(gesuchsperiodeId: string, cache: ICacheObject): IPromise<TSEbeguParameter[]> {
        return this.http.get(this.serviceURL + '/gesuchsperiode/' + gesuchsperiodeId, {cache: cache})
            .then((response: any) => {
                return this.ebeguRestUtil.parseEbeguParameters(response.data);
            });
    }

    public getEbeguParameterByJahr(year: number): IPromise<TSEbeguParameter[]> {
        return this.http.get(this.serviceURL + '/year/' + year)
            .then((response: any) => {
                return this.ebeguRestUtil.parseEbeguParameters(response.data);
            });
    }

    public getJahresabhParameter(): IPromise<TSEbeguParameter[]> {
        return this.http.get(this.serviceURL + '/yeardependent')
            .then((response: any) => {
                return this.ebeguRestUtil.parseEbeguParameters(response.data);
            });
    }

    public getEbeguParameterByKeyAndDate(dateParam: moment.Moment, keyParam: TSEbeguParameterKey): IPromise<TSEbeguParameter> {
        return this.http.get(this.serviceURL + '/name/' + keyParam)
            .then((param: IHttpResponse<TSEbeguParameter>) => {
                return param.data;
            });
    }
}
