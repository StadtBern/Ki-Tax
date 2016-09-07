import {IHttpService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSFinanzielleSituationContainer from '../../models/TSFinanzielleSituationContainer';
import TSGesuch from '../../models/TSGesuch';
import TSFinanzielleSituationResultateDTO from '../../models/dto/TSFinanzielleSituationResultateDTO';
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;
import WizardStepManager from './wizardStepManager';


export default class FinanzielleSituationRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', 'WizardStepManager'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService,
                private wizardStepManager: WizardStepManager) {
        this.serviceURL = REST_API + 'finanzielleSituation';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public saveFinanzielleSituation(finanzielleSituationContainer: TSFinanzielleSituationContainer, gesuchstellerId: string, gesuchId: string): IPromise<TSFinanzielleSituationContainer> {
        let returnedFinanzielleSituation = {};
        returnedFinanzielleSituation = this.ebeguRestUtil.finanzielleSituationContainerToRestObject(returnedFinanzielleSituation, finanzielleSituationContainer);
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(gesuchstellerId) + '/' + encodeURIComponent(gesuchId), returnedFinanzielleSituation, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((httpresponse: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.log.debug('PARSING finanzielle Situation  REST object ', httpresponse.data);
                return this.ebeguRestUtil.parseFinanzielleSituationContainer(new TSFinanzielleSituationContainer(), httpresponse.data);
            });
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
