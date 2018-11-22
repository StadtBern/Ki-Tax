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

import {IHttpService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSGesuch from '../../models/TSGesuch';
import TSDokumenteDTO from '../../models/dto/TSDokumenteDTO';
import TSDokumentGrund from '../../models/TSDokumentGrund';
import {TSDokumentGrundTyp} from '../../models/enums/TSDokumentGrundTyp';
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;
import ICacheObject = angular.ICacheObject;


export default class DokumenteRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'dokumente';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getDokumente(gesuch: TSGesuch): IPromise<TSDokumenteDTO> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuch.id))
            .then((response: any) => {
                this.log.debug('PARSING dokumentDTO REST object ', response.data);
                return this.ebeguRestUtil.parseDokumenteDTO(new TSDokumenteDTO(), response.data);
            });
    }


    public getDokumenteByTypeCached(gesuch: TSGesuch, dokumentGrundTyp: TSDokumentGrundTyp, cache: ICacheObject): IPromise<TSDokumenteDTO> {
        return this.http.get(this.serviceURL + '/byTyp/' + encodeURIComponent(gesuch.id) + '/'
            + encodeURIComponent(TSDokumentGrundTyp[dokumentGrundTyp]), {cache: cache})
            .then((response: any) => {
                this.log.debug('PARSING cached dokumentDTO REST object ', response.data);
                return this.ebeguRestUtil.parseDokumenteDTO(new TSDokumenteDTO(), response.data);
            });
    }

    public updateDokumentGrund(dokumentGrund: TSDokumentGrund): IPromise<TSDokumentGrund> {
        let restDokumentGrund = {};
        restDokumentGrund = this.ebeguRestUtil.dokumentGrundToRestObject(restDokumentGrund, dokumentGrund);
        return this.http.put(this.serviceURL, restDokumentGrund, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING dokumentGrund REST object ', response.data);
            return this.ebeguRestUtil.parseDokumentGrund(new TSDokumentGrund(), response.data);
        });
    }

}
