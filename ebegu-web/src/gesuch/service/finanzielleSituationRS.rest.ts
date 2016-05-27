import {IHttpPromise, IHttpService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSFinanzielleSituationContainer from '../../models/TSFinanzielleSituationContainer';
import TSGesuchsteller from '../../models/TSGesuchsteller';
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;
import TSGesuch from '../../models/TSGesuch';
import TSFinanzielleSituationResultateDTO from '../../models/dto/TSFinanzielleSituationResultateDTO';


export default class FinanzielleSituationRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService) {
        this.serviceURL = REST_API + 'finanzielleSituation';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public saveFinanzielleSituation(finanzielleSituationContainer: TSFinanzielleSituationContainer, gesuchsteller: TSGesuchsteller): IPromise<TSFinanzielleSituationContainer> {
        let returnedFinanzielleSituation = {};
        returnedFinanzielleSituation = this.ebeguRestUtil.finanzielleSituationContainerToRestObject(returnedFinanzielleSituation, finanzielleSituationContainer);
        return this.http.put(this.serviceURL + '/' + gesuchsteller.id, returnedFinanzielleSituation, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((httpresponse: any) => {
            this.log.debug('PARSING finanzielle Situation  REST object ', httpresponse.data);
            return this.ebeguRestUtil.parseFinanzielleSituationContainer(new TSFinanzielleSituationContainer(), httpresponse.data);
        });
    }

    public calculateFinanzielleSituation(gesuch: TSGesuch): IPromise<TSFinanzielleSituationResultateDTO> {
        let gesuchToSend = {};
        gesuchToSend = this.ebeguRestUtil.gesuchToRestObject(gesuchToSend, gesuch);
        return this.http.post(this.serviceURL + '/calculate', gesuchToSend, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((httpresponse: any) => {
            this.log.debug('PARSING finanzielle Situation  REST object ', httpresponse.data);
            return this.ebeguRestUtil.parseFinanzielleSituationResultate(new TSFinanzielleSituationResultateDTO(), httpresponse.data);
        });
    }

    public findFinanzielleSituation(finanzielleSituationID: string): IPromise<TSFinanzielleSituationContainer> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(finanzielleSituationID)).then((httpresponse: any) => {
            this.log.debug('PARSING finanzielle Situation  REST object ', httpresponse.data);
            return this.ebeguRestUtil.parseFinanzielleSituationContainer(new TSFinanzielleSituationContainer(), httpresponse.data);
        });
    }
}
