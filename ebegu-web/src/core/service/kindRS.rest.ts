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
import {IHttpService, ILogService, IPromise} from 'angular';
import TSKindContainer from '../../models/TSKindContainer';
import WizardStepManager from '../../gesuch/service/wizardStepManager';
import TSKindDublette from '../../models/TSKindDublette';

export default class KindRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', 'WizardStepManager'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService,
                private wizardStepManager: WizardStepManager) {
        this.serviceURL = REST_API + 'kinder';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'KindRS';
    }

    public findKind(kindContainerID: string): IPromise<TSKindContainer> {
        return this.http.get(this.serviceURL + '/find/' + encodeURIComponent(kindContainerID))
            .then((response: any) => {
                this.log.debug('PARSING kindContainers REST object ', response.data);
                return this.ebeguRestUtil.parseKindContainer(new TSKindContainer(), response.data);
            });
    }

    public saveKind(kindContainer: TSKindContainer, gesuchId: string): IPromise<TSKindContainer> {
        let restKind = {};
        restKind = this.ebeguRestUtil.kindContainerToRestObject(restKind, kindContainer);
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(gesuchId), restKind, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.log.debug('PARSING KindContainer REST object ', response.data);
                return this.ebeguRestUtil.parseKindContainer(new TSKindContainer(), response.data);
            });
        });
    }

    public removeKind(kindID: string, gesuchId: string): IPromise<any> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(kindID))
            .then((response) => {
                this.wizardStepManager.findStepsFromGesuch(gesuchId);
                return response;
            });
    }

    public getKindDubletten(gesuchId: string): IPromise<TSKindDublette[]> {
        return this.http.get(this.serviceURL + '/dubletten/' + encodeURIComponent(gesuchId))
            .then((response: any) => {
                return this.ebeguRestUtil.parseKindDubletteList(response.data);
            });
    }
}
