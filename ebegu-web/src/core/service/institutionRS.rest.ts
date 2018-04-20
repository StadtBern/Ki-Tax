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
import TSInstitution from '../../models/TSInstitution';

export class InstitutionRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'institutionen';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public findInstitution(institutionID: string): IPromise<TSInstitution> {
        return this.http.get(this.serviceURL + '/id/' + encodeURIComponent(institutionID))
            .then((response: any) => {
                this.log.debug('PARSING institution REST object ', response.data);
                return this.ebeguRestUtil.parseInstitution(new TSInstitution(), response.data);
            });
    }

    public updateInstitution(institution: TSInstitution): IPromise<TSInstitution> {
        let restInstitution = {};
        restInstitution = this.ebeguRestUtil.institutionToRestObject(restInstitution, institution);
        return this.http.put(this.serviceURL, restInstitution, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING institution REST object ', response.data);
            return this.ebeguRestUtil.parseInstitution(new TSInstitution(), response.data);
        });
    }

    public createInstitution(institution: TSInstitution): IPromise<TSInstitution> {
        let _institution = {};
        _institution = this.ebeguRestUtil.institutionToRestObject(_institution, institution);
        return this.http.post(this.serviceURL, _institution, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING institution REST object ', response.data);
            return this.ebeguRestUtil.parseInstitution(new TSInstitution(), response.data);
        });

    }

    public removeInstitution(institutionID: string): IHttpPromise<any> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(institutionID));
    }

    public getAllInstitutionen(): IPromise<TSInstitution[]> {
        return this.http.get(this.serviceURL).then((response: any) => {
            this.log.debug('PARSING institutionen REST array object', response.data);
            return this.ebeguRestUtil.parseInstitutionen(response.data);
        });
    }

    public getAllActiveInstitutionen(): IPromise<TSInstitution[]> {
        return this.http.get(this.serviceURL + '/' + 'active').then((response: any) => {
            this.log.debug('PARSING institutionen REST array object', response.data);
            return this.ebeguRestUtil.parseInstitutionen(response.data);
        });
    }

    public getInstitutionenForCurrentBenutzer(): IPromise<TSInstitution[]> {
        return this.http.get(this.serviceURL + '/' + 'currentuser').then((response: any) => {
            this.log.debug('PARSING institutionen REST array object', response.data);
            return this.ebeguRestUtil.parseInstitutionen(response.data);
        });
    }

    public getServiceName(): string {
        return 'InstitutionRS';
    }
}
