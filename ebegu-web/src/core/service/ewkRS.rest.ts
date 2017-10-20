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
import TSGesuchstellerContainer from '../../models/TSGesuchstellerContainer';
import TSEWKResultat from '../../models/TSEWKResultat';
import TSGesuchsteller from '../../models/TSGesuchsteller';
import DateUtil from '../../utils/DateUtil';
import IHttpParamSerializer = angular.IHttpParamSerializer;

export default class EwkRS {
    serviceURL: string;
    http: IHttpService;
    httpParamSerializer: IHttpParamSerializer;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;
    gesuchsteller1: TSGesuchstellerContainer;
    gesuchsteller2: TSGesuchstellerContainer;

    static $inject = ['$http', '$httpParamSerializer', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, private $httpParamSerializer: IHttpParamSerializer, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'gesuchsteller';
        this.http = $http;
        this.httpParamSerializer = $httpParamSerializer;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public ewkSearchAvailable(gsNr: number): boolean {
        return this.ewkSearchAvailableGS(this.getGesuchsteller(gsNr));
    }

    private ewkSearchAvailableGS(gesuchstellerContainer: TSGesuchstellerContainer): boolean {
        if (gesuchstellerContainer && gesuchstellerContainer.gesuchstellerJA
            && gesuchstellerContainer.gesuchstellerJA.nachname
            && gesuchstellerContainer.gesuchstellerJA.vorname
            && gesuchstellerContainer.gesuchstellerJA.geburtsdatum
            && gesuchstellerContainer.gesuchstellerJA.geschlecht) {
            return true;
        }
        return false;
    }

    public suchePerson(gsNr: number): IPromise<TSEWKResultat> {
        return this.suchePersonInEwk(this.getGesuchsteller(gsNr));
    }

    public getGesuchsteller(gsNr: number): TSGesuchstellerContainer {
        if (1 === gsNr) {
            return this.gesuchsteller1;
        } else if (2 === gsNr) {
            return this.gesuchsteller2;
        } else {
            this.log.error('invalid gesuchstellernummer', gsNr);
            return null;
        }
    }

    private suchePersonInEwk(gesuchstellerContainer: TSGesuchstellerContainer): IPromise<TSEWKResultat> {
        let gs: TSGesuchsteller = gesuchstellerContainer.gesuchstellerJA;
        if (gs.ewkPersonId) {
            return this.http.get(this.serviceURL + '/ewk/search/id/' + gs.ewkPersonId)
                .then((response: any) => {
                    return this.handlePersonSucheResult(response);
                });
        } else {
            let reportParams: string = this.httpParamSerializer({
                nachname: gs.nachname,
                vorname: gs.vorname,
                geburtsdatum: DateUtil.momentToLocalDate(gs.geburtsdatum),
                geschlecht: gs.geschlecht.toLocaleString()
            });
            return this.http.get(this.serviceURL + '/ewk/search/attributes?' + reportParams)
                .then((response: any) => {
                    return this.handlePersonSucheResult(response);
                });
        }
    }

    private handlePersonSucheResult(response: any): TSEWKResultat {
        this.log.debug('PARSING ewkResultat REST object ', response.data);
        return this.ebeguRestUtil.parseEWKResultat(new TSEWKResultat(), response.data);
    }

    public selectPerson(n: number, ewkPersonID: string): void {
        let gs: TSGesuchstellerContainer = this.getGesuchsteller(n);
        gs.gesuchstellerJA.ewkPersonId = ewkPersonID;
        gs.gesuchstellerJA.ewkAbfrageDatum = DateUtil.now();
    }

    public getServiceName(): string {
        return 'EwkRS';
    }
}
