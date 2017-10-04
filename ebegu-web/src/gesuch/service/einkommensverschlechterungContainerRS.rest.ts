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
import TSEinkommensverschlechterungContainer from '../../models/TSEinkommensverschlechterungContainer';
import TSGesuch from '../../models/TSGesuch';
import TSFinanzielleSituationResultateDTO from '../../models/dto/TSFinanzielleSituationResultateDTO';
import WizardStepManager from './wizardStepManager';
import TSFinanzModel from '../../models/TSFinanzModel';
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;

export default class EinkommensverschlechterungContainerRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', 'WizardStepManager'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService,
                private wizardStepManager: WizardStepManager) {
        this.serviceURL = REST_API + 'einkommensverschlechterung';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public saveEinkommensverschlechterungContainer(einkommensverschlechterungContainer: TSEinkommensverschlechterungContainer,
                                                   gesuchstellerId: string, gesuchId: string): IPromise<TSEinkommensverschlechterungContainer> {
        let returnedEinkommensverschlechterungContainer = {};
        returnedEinkommensverschlechterungContainer =
            this.ebeguRestUtil.einkommensverschlechterungContainerToRestObject(returnedEinkommensverschlechterungContainer, einkommensverschlechterungContainer);
        return this.http.put(this.serviceURL + '/' + gesuchstellerId  + '/' + encodeURIComponent(gesuchId), returnedEinkommensverschlechterungContainer, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((httpresponse: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.log.debug('PARSING Einkommensverschlechterung Container REST object ', httpresponse.data);
                return this.ebeguRestUtil.parseEinkommensverschlechterungContainer(new TSEinkommensverschlechterungContainer(), httpresponse.data);
            });
        });
    }

    public calculateEinkommensverschlechterung(gesuch: TSGesuch, basisJahrPlus: number): IPromise<TSFinanzielleSituationResultateDTO> {
        let gesuchToSend = {};
        gesuchToSend = this.ebeguRestUtil.gesuchToRestObject(gesuchToSend, gesuch);
        return this.http.post(this.serviceURL + '/calculate' + '/' + basisJahrPlus, gesuchToSend, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((httpresponse: any) => {
            this.log.debug('PARSING Einkommensverschlechterung Result  REST object ', httpresponse.data);
            return this.ebeguRestUtil.parseFinanzielleSituationResultate(new TSFinanzielleSituationResultateDTO(), httpresponse.data);
        });
    }

    public calculateEinkommensverschlechterungTemp(finanzModel: TSFinanzModel, basisJahrPlus: number): IPromise<TSFinanzielleSituationResultateDTO> {
        let finanzenToSend = {};
        finanzenToSend = this.ebeguRestUtil.finanzModelToRestObject(finanzenToSend, finanzModel);
        return this.http.post(this.serviceURL + '/calculateTemp' + '/' + basisJahrPlus, finanzenToSend, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((httpresponse: any) => {
            this.log.debug('PARSING Einkommensverschlechterung Result  REST object ', httpresponse.data);
            return this.ebeguRestUtil.parseFinanzielleSituationResultate(new TSFinanzielleSituationResultateDTO(), httpresponse.data);
        });
    }

    public findEinkommensverschlechterungContainer(einkommensverschlechterungID: string): IPromise<TSEinkommensverschlechterungContainer> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(einkommensverschlechterungID)).then((httpresponse: any) => {
            this.log.debug('PARSING EinkommensverschlechterungContainer REST object ', httpresponse.data);
            return this.ebeguRestUtil.parseEinkommensverschlechterungContainer(new TSEinkommensverschlechterungContainer(), httpresponse.data);
        });
    }

    public findEKVContainerForGesuchsteller(gesuchstellerID: string): IPromise<TSEinkommensverschlechterungContainer> {
        return this.http.get(this.serviceURL + '/forGesuchsteller/' + encodeURIComponent(gesuchstellerID)).then((httpresponse: any) => {
            this.log.debug('PARSING EinkommensverschlechterungContainer REST object ', httpresponse.data);
            return this.ebeguRestUtil.parseEinkommensverschlechterungContainer(new TSEinkommensverschlechterungContainer(), httpresponse.data);
        });
    }
}
