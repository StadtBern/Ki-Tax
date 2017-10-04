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

import {IEntityRS} from '../../core/service/iEntityRS.rest';
import {IHttpPromise, IHttpService, IPromise, ILogService, IHttpResponse} from 'angular';
import TSGesuch from '../../models/TSGesuch';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSMahnung from '../../models/TSMahnung';

export default class MahnungRS implements IEntityRS {

    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private $log: ILogService) {
        this.serviceURL = REST_API + 'mahnung';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public saveMahnung(mahnung: TSMahnung): IPromise<TSMahnung> {
        let sentMahnung = {};
        sentMahnung = this.ebeguRestUtil.mahnungToRestObject(sentMahnung, mahnung);
        return this.http.post(this.serviceURL, sentMahnung, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.$log.debug('PARSING gesuch REST object ', response.data);
            return this.ebeguRestUtil.parseMahnung(new TSMahnung(), response.data);
        });
    }

    public findMahnungen(gesuchId: string): IPromise<TSMahnung[]> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuchId))
            .then((response: any) => {
                return this.ebeguRestUtil.parseMahnungen(response.data);
            });
    }

    public mahnlaufBeenden(gesuch: TSGesuch): IPromise<TSGesuch> {
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(gesuch.id), {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: IHttpResponse<TSGesuch>) => {
            this.$log.debug('PARSING gesuch REST object ', response.data);
            return this.ebeguRestUtil.parseGesuch(new TSGesuch(), response.data);
        });
    }

    public getInitialeBemerkungen(gesuch: TSGesuch): IHttpPromise<string> {
        return this.http.get(this.serviceURL + '/bemerkungen/' + encodeURIComponent(gesuch.id), {
            headers: {
                'Content-Type': 'text/plain'
            }
        });
    }
}
