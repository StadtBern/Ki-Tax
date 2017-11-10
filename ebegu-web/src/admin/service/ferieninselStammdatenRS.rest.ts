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

import {IHttpService, IPromise} from 'angular';
import {TSFerienname} from '../../models/enums/TSFerienname';
import TSFerieninselStammdaten from '../../models/TSFerieninselStammdaten';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import IQService = angular.IQService;

export class FerieninselStammdatenRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', 'Upload', '$q', 'base64'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private upload: any,
                private $q: IQService, private base64: any) {
        this.serviceURL = REST_API + 'ferieninselStammdaten';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public saveFerieninselStammdaten(stammdaten: TSFerieninselStammdaten): IPromise<TSFerieninselStammdaten> {
        let stammdatenObj = {};
        stammdatenObj = this.ebeguRestUtil.ferieninselStammdatenToRestObject(stammdatenObj, stammdaten);

        return this.http.put(this.serviceURL, stammdatenObj, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            return this.ebeguRestUtil.parseFerieninselStammdaten(new TSFerieninselStammdaten(), response.data);
        });
    }

    public findFerieninselStammdaten(fachstelleID: string): IPromise<TSFerieninselStammdaten> {
        return this.http.get(this.serviceURL + '/id/' + encodeURIComponent(fachstelleID))
            .then((response: any) => {
                return this.ebeguRestUtil.parseFerieninselStammdaten(new TSFerieninselStammdaten(), response.data);
            });
    }

    public findFerieninselStammdatenByGesuchsperiode(gesuchsperiodeId: string): IPromise<TSFerieninselStammdaten[]> {
        return this.http.get(this.serviceURL + '/gesuchsperiode/' + encodeURIComponent(gesuchsperiodeId))
            .then((response: any) => {
                return this.ebeguRestUtil.parseFerieninselStammdatenList(response.data);
            });
    }

    public findFerieninselStammdatenByGesuchsperiodeAndFerien(gesuchsperiodeId: string, ferienname: TSFerienname): IPromise<TSFerieninselStammdaten> {
        return this.http.get(this.serviceURL + '/gesuchsperiode/' + encodeURIComponent(gesuchsperiodeId) + '/' + ferienname)
            .then((response: any) => {
                return this.ebeguRestUtil.parseFerieninselStammdaten(new TSFerieninselStammdaten(), response.data);
            });
    }

    public getServiceName(): string {
        return 'FerieninselStammdatenRS';
    }
}
