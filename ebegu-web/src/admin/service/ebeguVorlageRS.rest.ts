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
import {IHttpService, IPromise} from 'angular';
import TSEbeguVorlage from '../../models/TSEbeguVorlage';
import IQService = angular.IQService;
import IHttpPromise = angular.IHttpPromise;


export class EbeguVorlageRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', 'Upload', '$q', 'base64'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private upload: any,
                private $q: IQService, private base64: any) {
        this.serviceURL = REST_API + 'ebeguVorlage';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public getEbeguVorlagenByGesuchsperiode(gesuchsperiodeId: string): IPromise<TSEbeguVorlage[]> {
        return this.http.get(this.serviceURL + '/gesuchsperiode/' + gesuchsperiodeId)
            .then((response: any) => {
                return this.ebeguRestUtil.parseEbeguVorlages(response.data);
            });
    }

    public uploadVorlage(file: any, ebeguVorlage: TSEbeguVorlage, gesuchsperiodeID: string, proGesuchsperiode: boolean): IPromise<TSEbeguVorlage> {

        let restEbeguVorlage = {};
        restEbeguVorlage = this.ebeguRestUtil.ebeguVorlageToRestObject(restEbeguVorlage, ebeguVorlage);
        this.upload.json(restEbeguVorlage);
        let encodedFilename = this.base64.encode(file.name);
        return this.upload.upload({
            url: this.serviceURL,
            method: 'POST',
            headers: {
                'x-filename': encodedFilename,
                'x-vorlagekey': ebeguVorlage.name,
                'x-gesuchsperiode': gesuchsperiodeID,
                'x-progesuchsperiode': proGesuchsperiode,
            },
            data: {
                file: file,
            }
        }).then((response: any) => {
            return this.ebeguRestUtil.parseEbeguVorlage(new TSEbeguVorlage(), response.data);
        }, (response: any) => {
            console.log('Upload File: NOT SUCCESS');
            return this.$q.reject();
        }, (evt: any) => {
            let loaded: number = evt.loaded;
            let total: number = evt.total;
            let progressPercentage: number = 100.0 * loaded / total;
            console.log('progress: ' + progressPercentage + '% ');
            return this.$q.defer().notify();
        });
    }

    public deleteEbeguVorlage(ebeguVorlageID: string): IHttpPromise<any> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(ebeguVorlageID));
    }

    public getEbeguVorlagenWithoutGesuchsperiode(): IPromise<TSEbeguVorlage[]> {
        return this.http.get(this.serviceURL + '/nogesuchsperiode/')
            .then((response: any) => {
                return this.ebeguRestUtil.parseEbeguVorlages(response.data);
            });
    }
}
