import {IPromise, IHttpService, ILogService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSFamiliensituation from '../../models/TSFamiliensituation';
import WizardStepManager from './wizardStepManager';


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

    public saveFamiliensituation(familiensituation: TSFamiliensituation, gesuchId: string): IPromise<TSFamiliensituation> {
        let returnedFamiliensituation = {};
        returnedFamiliensituation = this.ebeguRestUtil.familiensituationToRestObject(returnedFamiliensituation, familiensituation);
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(gesuchId), returnedFamiliensituation, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.$log.debug('PARSING Familiensituation REST object ', response.data);
                return this.ebeguRestUtil.parseFamiliensituation(new TSFamiliensituation(), response.data);
            });
        });
    }

}
