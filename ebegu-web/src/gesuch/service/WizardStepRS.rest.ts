import {ILogService, IHttpService, IPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSWizardStep from '../../models/TSWizardStep';

export default class WizardStepRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private $log: ILogService) {
        this.serviceURL = REST_API + 'wizard-steps';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public updateWizardStep(wizardStep: TSWizardStep): IPromise<any> {
        let wizardStepObject = this.ebeguRestUtil.wizardStepToRestObject({}, wizardStep);

        return this.http.post(this.serviceURL, wizardStepObject, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.$log.debug('PARSING WizardStep REST object ', response.data);
            return this.ebeguRestUtil.parseWizardStep(new TSWizardStep(), response.data);
        });
    }

    public findWizardStepsFromGesuch(gesuchID: string): IPromise<any> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuchID))
            .then((response: any) => {
                this.$log.debug('PARSING wizardSteps REST objects ', response.data);
                return this.ebeguRestUtil.parseWizardStepList(response.data);
            });
    }

    public getServiceName(): string {
        return 'WizardStepRS';
    }

}
