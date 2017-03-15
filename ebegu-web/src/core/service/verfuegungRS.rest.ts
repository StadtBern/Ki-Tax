import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IPromise, ILogService} from 'angular';
import TSKindContainer from '../../models/TSKindContainer';
import TSVerfuegung from '../../models/TSVerfuegung';
import WizardStepManager from '../../gesuch/service/wizardStepManager';

export default class VerfuegungRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', 'WizardStepManager'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService,
                private wizardStepManager: WizardStepManager) {
        this.serviceURL = REST_API + 'verfuegung';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'VerfuegungRS';
    }

    public calculateVerfuegung(gesuchID: string): IPromise<TSKindContainer[]> {
        return this.http.get(this.serviceURL + '/calculate/' + encodeURIComponent(gesuchID))
            .then((response: any) => {
                this.log.debug('PARSING KindContainers REST object ', response.data);
                return this.ebeguRestUtil.parseKindContainerList(response.data);
            });
    }

    public saveVerfuegung(verfuegung: TSVerfuegung, gesuchId: string, betreuungId: string, ignorieren: boolean): IPromise<TSVerfuegung> {
        let restVerfuegung = this.ebeguRestUtil.verfuegungToRestObject({}, verfuegung);
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(gesuchId) + '/' + encodeURIComponent(betreuungId) + '/' + ignorieren, restVerfuegung, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.log.debug('PARSING Verfuegung REST object ', response.data);
                return this.ebeguRestUtil.parseVerfuegung(new TSVerfuegung(), response.data);
            });
        });
    }

    public verfuegungSchliessenOhneVerfuegen(gesuchId: string, betreuungId: string): IPromise<void> {
        return this.http.post(this.serviceURL + '/schliessenOhneVerfuegen/' + encodeURIComponent(betreuungId), {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.log.debug('PARSING Verfuegung REST object ', response.data);
                return;
            });
        });
    }

    public nichtEintreten(verfuegung: TSVerfuegung, gesuchId: string, betreuungId: string): IPromise<TSVerfuegung> {
        let restVerfuegung = this.ebeguRestUtil.verfuegungToRestObject({}, verfuegung);
        return this.http.put(this.serviceURL + '/nichtEintreten/' + encodeURIComponent(betreuungId), restVerfuegung, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.log.debug('PARSING Verfuegung REST object ', response.data);
                return this.ebeguRestUtil.parseVerfuegung(new TSVerfuegung(), response.data);
            });
        });
    }
}
