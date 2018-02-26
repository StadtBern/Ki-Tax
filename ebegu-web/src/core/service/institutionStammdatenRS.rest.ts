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
import TSInstitutionStammdaten from '../../models/TSInstitutionStammdaten';
import DateUtil from '../../utils/DateUtil';
import {TSBetreuungsangebotTyp} from '../../models/enums/TSBetreuungsangebotTyp';
import * as moment from 'moment';

export class InstitutionStammdatenRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'institutionstammdaten';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public findInstitutionStammdaten(institutionStammdatenID: string): IPromise<TSInstitutionStammdaten> {
        return this.http.get(this.serviceURL + '/id/' + encodeURIComponent(institutionStammdatenID))
            .then((response: any) => {
                this.log.debug('PARSING InstitutionStammdaten REST object ', response.data);
                return this.ebeguRestUtil.parseInstitutionStammdaten(new TSInstitutionStammdaten(), response.data);
            });
    }

    public createInstitutionStammdaten(institutionStammdaten: TSInstitutionStammdaten): IPromise<TSInstitutionStammdaten> {
        return this.saveInstitutionStammdaten(institutionStammdaten);
    }

    public updateInstitutionStammdaten(institutionStammdaten: TSInstitutionStammdaten): IPromise<TSInstitutionStammdaten> {
        return this.saveInstitutionStammdaten(institutionStammdaten);
    }

    private saveInstitutionStammdaten(institutionStammdaten: TSInstitutionStammdaten): IPromise<TSInstitutionStammdaten> {
        let restInstitutionStammdaten = {};
        restInstitutionStammdaten = this.ebeguRestUtil.institutionStammdatenToRestObject(restInstitutionStammdaten, institutionStammdaten);

        return this.http.put(this.serviceURL, restInstitutionStammdaten, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
                this.log.debug('PARSING institutionStammdaten REST object', response.data);
                return this.ebeguRestUtil.parseInstitutionStammdaten(new TSInstitutionStammdaten(), response.data);
            }
        );
    }

    public removeInstitutionStammdaten(institutionStammdatenID: string): IHttpPromise<any> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(institutionStammdatenID));
    }

    public getAllInstitutionStammdaten(): IPromise<TSInstitutionStammdaten[]> {
        return this.http.get(this.serviceURL).then((response: any) => {
            this.log.debug('PARSING institutionStammdaten REST array object', response.data);
            return this.ebeguRestUtil.parseInstitutionStammdatenArray(response.data);
        });
    }

    public getAllInstitutionStammdatenByDate(dateParam: moment.Moment): IPromise<TSInstitutionStammdaten[]> {
        return this.http.get(this.serviceURL + '/date', {params: {date: DateUtil.momentToLocalDate(dateParam)}})
            .then((response: any) => {
                this.log.debug('PARSING institutionStammdaten REST array object', response.data);
                return this.ebeguRestUtil.parseInstitutionStammdatenArray(response.data);
            });
    }

    public getAllActiveInstitutionStammdatenByGesuchsperiode(gesuchsperiodeId: string): IPromise<TSInstitutionStammdaten[]> {
        return this.http.get(this.serviceURL + '/gesuchsperiode/active', {params: {gesuchsperiodeId: gesuchsperiodeId}})
            .then((response: any) => {
                this.log.debug('PARSING institutionStammdaten REST array object', response.data);
                return this.ebeguRestUtil.parseInstitutionStammdatenArray(response.data);
            });
    }

    public getAllInstitutionStammdatenByInstitution(institutionID: string): IPromise<TSInstitutionStammdaten[]> {
        return this.http.get(this.serviceURL + '/institution' + '/' + encodeURIComponent(institutionID))
            .then((response: any) => {
                this.log.debug('PARSING institutionStammdaten REST array object', response.data);
                return this.ebeguRestUtil.parseInstitutionStammdatenArray(response.data);
            });
    }

    public getBetreuungsangeboteForInstitutionenOfCurrentBenutzer(): IPromise<TSBetreuungsangebotTyp[]> {
        return this.http.get(this.serviceURL + '/currentuser')
            .then((response: any) => {
                this.log.debug('PARSING institutionStammdaten REST array object', response.data);
                return response.data;
            });
    }

    public getServiceName(): string {
        return 'InstitutionStammdatenRS';
    }

}
