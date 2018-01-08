/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

import {IHttpService, IPromise} from 'angular';

export class DailyBatchRS {

    serviceURL: string;
    http: IHttpService;

    static $inject = ['$http', 'REST_API'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string) {
        this.serviceURL = REST_API + 'dailybatch';
        this.http = $http;
    }

    public getServiceName(): string {
        return 'DailyBatchRS';
    }

    public runBatchMahnungFristablauf(): IPromise<Boolean> {
        return this.http.get(this.serviceURL + '/mahnungFristAblauf')
            .then((response: any) => {
                return response;
            });
    }
}
