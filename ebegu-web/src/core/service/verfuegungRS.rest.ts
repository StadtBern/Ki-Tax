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
import TSVerfuegung from '../../models/TSVerfuegung';
import WizardStepManager from '../../gesuch/service/wizardStepManager';

export default class VerfuegungRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', 'WizardStepManager'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService,
                private wizardStepManager: WizardStepManager) {
        this.serviceURL = REST_API + 'verfuegung';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'VerfuegungRS';
    }

    public calculateVerfuegung(gesuchID: string): IPromise<TSKindContainer[]> {
        return this.http.get(this.serviceURL + '/calculate/' + encodeURIComponent(gesuchID))
            .then((response: any) => {
                this.log.debug('PARSING KindContainers REST object ', response.data);
                return this.ebeguRestUtil.parseKindContainerList(response.data);
            });
    }

    public saveVerfuegung(verfuegung: TSVerfuegung, gesuchId: string, betreuungId: string, ignorieren: boolean): IPromise<TSVerfuegung> {
        let restVerfuegung = this.ebeguRestUtil.verfuegungToRestObject({}, verfuegung);
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(gesuchId) + '/' + encodeURIComponent(betreuungId) + '/' + ignorieren, restVerfuegung, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.log.debug('PARSING Verfuegung REST object ', response.data);
                return this.ebeguRestUtil.parseVerfuegung(new TSVerfuegung(), response.data);
            });
        });
    }

    public verfuegungSchliessenOhneVerfuegen(gesuchId: string, betreuungId: string): IPromise<void> {
        return this.http.post(this.serviceURL + '/schliessenOhneVerfuegen/' + encodeURIComponent(betreuungId), {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.log.debug('PARSING Verfuegung REST object ', response.data);
                return;
            });
        });
    }

    public nichtEintreten(verfuegung: TSVerfuegung, gesuchId: string, betreuungId: string): IPromise<TSVerfuegung> {
        let restVerfuegung = this.ebeguRestUtil.verfuegungToRestObject({}, verfuegung);
        return this.http.put(this.serviceURL + '/nichtEintreten/' + encodeURIComponent(betreuungId), restVerfuegung, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.log.debug('PARSING Verfuegung REST object ', response.data);
                return this.ebeguRestUtil.parseVerfuegung(new TSVerfuegung(), response.data);
            });
        });
    }
}
