import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IHttpPromise} from 'angular';
import DateUtil from '../../utils/DateUtil';

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

    public createTestFallGS(testFall: string, bestaetigt: boolean, verfuegen: boolean, username: string): IHttpPromise<String> {
        return this.http.get(this.serviceURL + '/testfallgs/' + encodeURIComponent(testFall)
            + '/' + bestaetigt + '/' + verfuegen + '/' + encodeURIComponent(username));
    }


    public removeFaelleOfGS(username: string): IHttpPromise<String> {
        return this.http.delete(this.serviceURL + '/testfallgs/' + encodeURIComponent(username));
    }



    public createTestFall(testFall: string, bestaetigt: boolean, verfuegen: boolean): IHttpPromise<String> {
        return this.http.get(this.serviceURL + '/testfall/' + encodeURIComponent(testFall) + '/' + bestaetigt + '/' + verfuegen);
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
}
