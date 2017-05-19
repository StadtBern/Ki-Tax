import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IPromise, ILogService} from 'angular';
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
        if (gesuchstellerContainer && gesuchstellerContainer.gesuchstellerJA) {
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
