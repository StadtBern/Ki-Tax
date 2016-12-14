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
