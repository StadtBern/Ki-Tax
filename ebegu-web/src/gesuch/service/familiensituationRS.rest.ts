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

import {IPromise, IHttpService, ILogService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import WizardStepManager from './wizardStepManager';
import TSFamiliensituationContainer from '../../models/TSFamiliensituationContainer';


export default class FamiliensituationRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', 'WizardStepManager'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private $log: ILogService,
                private wizardStepManager: WizardStepManager) {
        this.serviceURL = REST_API + 'familiensituation';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public saveFamiliensituation(familiensituation: TSFamiliensituationContainer, gesuchId: string): IPromise<TSFamiliensituationContainer> {
        let returnedFamiliensituation = {};
        returnedFamiliensituation = this.ebeguRestUtil.familiensituationContainerToRestObject(returnedFamiliensituation, familiensituation);
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(gesuchId), returnedFamiliensituation, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.$log.debug('PARSING Familiensituation REST object ', response.data);
                return this.ebeguRestUtil.parseFamiliensituationContainer(new TSFamiliensituationContainer(), response.data);
            });
        });
    }

}
