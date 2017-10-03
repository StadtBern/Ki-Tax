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
import {IHttpService, IPromise, ILogService} from 'angular';
import TSAntragDTO from '../../models/TSAntragDTO';

export default class PendenzRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, private REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'pendenzen';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'PendenzRS';
    }

    public getPendenzenList(): IPromise<Array<TSAntragDTO>> {
        return this.http.get(this.serviceURL + '/jugendamt/')
            .then((response: any) => {
                this.log.debug('PARSING pendenz REST object ', response.data);
                return this.ebeguRestUtil.parseAntragDTOs(response.data);
            });
    }

    public getPendenzenListForUser(userId: string): IPromise<Array<TSAntragDTO>> {
        return this.http.get(this.serviceURL + '/jugendamt/' + encodeURIComponent(userId))
            .then((response: any) => {
                this.log.debug('PARSING pendenz REST object ', response.data);
                return this.ebeguRestUtil.parseAntragDTOs(response.data);
            });
    }

    public getAntraegeGesuchstellerList(): IPromise<Array<TSAntragDTO>> {
        return this.http.get(this.serviceURL + '/gesuchsteller')
            .then((response: any) => {
                this.log.debug('PARSING pendenz REST object ', response.data);
                return this.ebeguRestUtil.parseAntragDTOs(response.data);
            });
    }
}
