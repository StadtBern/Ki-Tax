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
import TSLand from '../../models/types/TSLand';
import {IHttpService} from 'angular';

export default class ListResourceRS {
    static $inject = ['$http', 'REST_API', 'EbeguRestUtil'];

    static laenderList: TSLand[];

    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil) {
        this.serviceURL = REST_API + 'lists';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        ListResourceRS.laenderList = [];
    }

    getLaenderList() {
        return this.http.get(this.serviceURL + '/laender', { cache: true }).then((response: any) => {
            if (ListResourceRS.laenderList.length <= 0) { // wenn die Laenderliste schon ausgefuellt wurde, nichts machen
                for (let i = 0; i < response.data.length; i++) {
                    ListResourceRS.laenderList.push(this.ebeguRestUtil.landCodeToTSLand(response.data[i]));
                }
            }
            return ListResourceRS.laenderList;
        });
    }

    }

