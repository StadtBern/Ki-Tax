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

import {ILogService, IHttpService, IPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSWizardStep from '../../models/TSWizardStep';

export default class WizardStepRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private $log: ILogService) {
        this.serviceURL = REST_API + 'wizard-steps';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public updateWizardStep(wizardStep: TSWizardStep): IPromise<any> {
        let wizardStepObject = this.ebeguRestUtil.wizardStepToRestObject({}, wizardStep);

        return this.http.post(this.serviceURL, wizardStepObject, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.$log.debug('PARSING WizardStep REST object ', response.data);
            return this.ebeguRestUtil.parseWizardStep(new TSWizardStep(), response.data);
        });
    }

    public findWizardStepsFromGesuch(gesuchID: string): IPromise<any> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuchID))
            .then((response: any) => {
                this.$log.debug('PARSING wizardSteps REST objects ', response.data);
                return this.ebeguRestUtil.parseWizardStepList(response.data);
            });
    }

    public getServiceName(): string {
        return 'WizardStepRS';
    }

    public setWizardStepMutiert(wizardStepId: string): IPromise<TSWizardStep> {
        return this.http.post(this.serviceURL + '/setWizardStepMutiert/' + encodeURIComponent(wizardStepId), null)
            .then((response) => {
            return this.ebeguRestUtil.parseWizardStep(new TSWizardStep(), response.data);
        });
    }
}
