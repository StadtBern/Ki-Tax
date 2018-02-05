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

import {IHttpService, ILogService, IPromise, IQService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSWorkJob from '../../models/TSWorkJob';

/**
 * liest information ueber batch jobs aus
 */
export default class BatchJobRS {
    static $inject = ['$http', 'REST_API', 'EbeguRestUtil'];

    serviceURL: string;
    http: IHttpService;

    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, private ebeguRestUtil: EbeguRestUtil, $log: ILogService, private $q: IQService) {
        this.serviceURL = REST_API + 'admin/batch';
        this.http = $http;
    }

    public getAllJobs(): IPromise<TSWorkJob[]> {
        return this.http.get(this.serviceURL + '/jobs').then((response: any) => {
            return this.ebeguRestUtil.parseWorkJobList(response.data);
        });
    }

    public getBatchJobsOfUser(): IPromise<TSWorkJob[]> {
        return this.http.get(this.serviceURL + '/userjobs').then((response: any) => {
            return this.ebeguRestUtil.parseWorkJobList(response.data);
        });
    }

    public getBatchJobInformation(executionId: string): IPromise<TSWorkJob[]> {
        return this.http.get(this.serviceURL + '/jobs/' + encodeURI(executionId)).then((response: any) => {
            return this.ebeguRestUtil.parseWorkJobList(response.data);
        });
    }

}
