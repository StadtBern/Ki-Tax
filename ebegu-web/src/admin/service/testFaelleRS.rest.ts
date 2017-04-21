import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IHttpPromise} from 'angular';
import DateUtil from '../../utils/DateUtil';
import * as moment from 'moment';
import IPromise = angular.IPromise;


export class TestFaelleRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil) {
        this.serviceURL = REST_API + 'testfaelle';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public getServiceName(): string {
        return 'TestFaelleRS';
    }

    public createTestFallGS(testFall: string, gesuchsperiodeId: string, bestaetigt: boolean, verfuegen: boolean, username: string): IHttpPromise<String> { //TODO
        return this.http.get(this.serviceURL + '/testfallgs/' + encodeURIComponent(testFall) + '/' + gesuchsperiodeId
            + '/' + bestaetigt + '/' + verfuegen + '/' + encodeURIComponent(username));
    }


    public removeFaelleOfGS(username: string): IHttpPromise<String> {
        return this.http.delete(this.serviceURL + '/testfallgs/' + encodeURIComponent(username));
    }



    public createTestFall(testFall: string, gesuchsperiodeId: string, bestaetigt: boolean, verfuegen: boolean): IHttpPromise<String> { //TODO
        return this.http.get(this.serviceURL + '/testfall/' + encodeURIComponent(testFall) + '/' + gesuchsperiodeId + '/' + bestaetigt + '/' + verfuegen);
    }

    public mutiereFallHeirat(fallNummer: Number, gesuchsperiodeid: string, mutationsdatum: moment.Moment, aenderungper: moment.Moment): IHttpPromise<String> {
        return this.http.get(this.serviceURL + '/mutationHeirat/' + fallNummer + '/' +
            encodeURIComponent(gesuchsperiodeid), {
            params: {
                mutationsdatum: DateUtil.momentToLocalDate(mutationsdatum),
                aenderungper: DateUtil.momentToLocalDate(aenderungper)
            }
        });
    }

    public mutiereFallScheidung(fallNummer: Number, gesuchsperiodeid: string, mutationsdatum: moment.Moment, aenderungper: moment.Moment): IHttpPromise<String> {
        return this.http.get(this.serviceURL + '/mutationScheidung/' + fallNummer + '/' +
            encodeURIComponent(gesuchsperiodeid), {
            params: {
                mutationsdatum: DateUtil.momentToLocalDate(mutationsdatum),
                aenderungper: DateUtil.momentToLocalDate(aenderungper)
            }
        });
    }

    public resetSchulungsdaten(): IHttpPromise<String> {
        return this.http.get(this.serviceURL + '/schulung/reset');
    }

    public createSchulungsdaten(): IHttpPromise<String> {
        return this.http.get(this.serviceURL + '/schulung/create');
    }

    public deleteSchulungsdaten(): IHttpPromise<String> {
        return this.http.delete(this.serviceURL + '/schulung/delete');
    }

    public getSchulungBenutzer(): IPromise<String[]> {
        return this.http.get(this.serviceURL + '/schulung/public/user').then((response: any) => {
            return response.data;
        });
    }
}
