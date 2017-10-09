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
import WizardStepManager from './wizardStepManager';
import TSEinkommensverschlechterungInfoContainer from '../../models/TSEinkommensverschlechterungInfoContainer';
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;

export default class EinkommensverschlechterungInfoRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', 'WizardStepManager'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService,
                private wizardStepManager: WizardStepManager) {
        this.serviceURL = REST_API + 'einkommensverschlechterungInfo';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public saveEinkommensverschlechterungInfo(einkommensverschlechterungInfoContainer: TSEinkommensverschlechterungInfoContainer,
                                              gesuchId: string): IPromise<TSEinkommensverschlechterungInfoContainer> {
        let returnedEinkommensverschlechterungInfo = {};
        returnedEinkommensverschlechterungInfo =
            this.ebeguRestUtil.einkommensverschlechterungInfoContainerToRestObject(returnedEinkommensverschlechterungInfo, einkommensverschlechterungInfoContainer);
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(gesuchId), returnedEinkommensverschlechterungInfo, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((httpresponse: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.log.debug('PARSING EinkommensverschlechterungInfo REST object ', httpresponse.data);
                return this.ebeguRestUtil.parseEinkommensverschlechterungInfoContainer(new TSEinkommensverschlechterungInfoContainer(), httpresponse.data);
            });
        });
    }

}
