import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IPromise, ILogService} from 'angular';
import TSKindContainer from '../../models/TSKindContainer';
import WizardStepManager from '../../gesuch/service/wizardStepManager';

export default class KindRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', 'WizardStepManager'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService,
                private wizardStepManager: WizardStepManager) {
        this.serviceURL = REST_API + 'kinder';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'KindRS';
    }

    public findKind(kindContainerID: string): IPromise<TSKindContainer> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(kindContainerID))
            .then((response: any) => {
                this.log.debug('PARSING kindContainers REST object ', response.data);
                return this.ebeguRestUtil.parseKindContainer(new TSKindContainer(), response.data);
            });
    }

    public saveKind(kindContainer: TSKindContainer, gesuchId: string): IPromise<TSKindContainer> {
        let restKind = {};
        restKind = this.ebeguRestUtil.kindContainerToRestObject(restKind, kindContainer);
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(gesuchId), restKind, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.log.debug('PARSING KindContainer REST object ', response.data);
                return this.ebeguRestUtil.parseKindContainer(new TSKindContainer(), response.data);
            });
        });
    }

    public removeKind(kindID: string, gesuchId: string): IPromise<any> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(kindID))
            .then((response) => {
                this.wizardStepManager.findStepsFromGesuch(gesuchId);
                return response;
            });
    }
}
