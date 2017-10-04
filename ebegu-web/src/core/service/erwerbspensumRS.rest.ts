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
import TSErwerbspensumContainer from '../../models/TSErwerbspensumContainer';
import WizardStepManager from '../../gesuch/service/wizardStepManager';

export default class ErwerbspensumRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', 'WizardStepManager'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService,
                private wizardStepManager: WizardStepManager) {
        this.serviceURL = REST_API + 'erwerbspensen';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'ErwerbspensumRS';
    }

    public findErwerbspensum(erwerbspensenContainerID: string): IPromise<TSErwerbspensumContainer> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(erwerbspensenContainerID))
            .then((response: any) => {
                this.log.debug('PARSING erwerbspensenContainer REST object ', response.data);
                return this.ebeguRestUtil.parseErwerbspensumContainer(new TSErwerbspensumContainer(), response.data);
            });
    }

    public saveErwerbspensum(erwerbspensenContainer: TSErwerbspensumContainer, gesuchstellerID: string, gesuchId: string): IPromise<TSErwerbspensumContainer> {
        let restErwerbspensum = {};
        restErwerbspensum = this.ebeguRestUtil.erwerbspensumContainerToRestObject(restErwerbspensum, erwerbspensenContainer);
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(gesuchstellerID) + '/' + gesuchId, restErwerbspensum, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.log.debug('PARSING ErwerbspensumContainer REST object ', response.data);
                return this.ebeguRestUtil.parseErwerbspensumContainer(new TSErwerbspensumContainer(), response.data);
            });
        });
    }

    public removeErwerbspensum(erwerbspensumContID: string, gesuchId: string): IPromise<any> {
        return this.http.delete(this.serviceURL + '/gesuchId/' + encodeURIComponent(gesuchId) + '/erwPenId/' + encodeURIComponent(erwerbspensumContID))
            .then((response) => {
                this.wizardStepManager.findStepsFromGesuch(gesuchId);
                return response;
            });
    }

    public isErwerbspensumRequired(gesuchId: string): IPromise<boolean> {
        return this.http.get(this.serviceURL + '/required/' + encodeURIComponent(gesuchId)).then((response: any) => {
            return response.data;
        });
    }
}
