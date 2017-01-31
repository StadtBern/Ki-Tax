import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IPromise, ILogService} from 'angular';
import WizardStepManager from '../../gesuch/service/wizardStepManager';
import TSMitteilung from '../../models/TSMitteilung';

export default class MitteilungRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', 'WizardStepManager'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService,
                private wizardStepManager: WizardStepManager) {
        this.serviceURL = REST_API + 'mitteilungen';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'MitteilungRS';
    }

    public findMitteilung(mitteilungID: string): IPromise<TSMitteilung> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(mitteilungID))
            .then((response: any) => {
                this.log.debug('PARSING Mitteilung REST object ', response.data);
                return this.ebeguRestUtil.parseMitteilung(new TSMitteilung(), response.data);
            });
    }

    public sendMitteilung(mitteilung: TSMitteilung): IPromise<TSMitteilung> {
        let restMitteilung = {};
        restMitteilung = this.ebeguRestUtil.mitteilungToRestObject(restMitteilung, mitteilung);
        return this.http.put(this.serviceURL + '/send', restMitteilung, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilung(new TSMitteilung(), response.data);
        });
    }

    public saveEntwurf(mitteilung: TSMitteilung): IPromise<TSMitteilung> {
        let restMitteilung = {};
        restMitteilung = this.ebeguRestUtil.mitteilungToRestObject(restMitteilung, mitteilung);
        return this.http.put(this.serviceURL + '/entwurf', restMitteilung, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilung(new TSMitteilung(), response.data);
        });
    }

    public setMitteilungGelesen(mitteilungId: string): IPromise<TSMitteilung> {
        return this.http.put(this.serviceURL + '/setgelesen/' + mitteilungId, null).then((response: any) => {
            this.log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilung(new TSMitteilung(), response.data);
        });
    }

    public setMitteilungErledigt(mitteilungId: string): IPromise<TSMitteilung> {
        return this.http.put(this.serviceURL + '/seterledigt/' + mitteilungId, null).then((response: any) => {
            this.log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilung(new TSMitteilung(), response.data);
        });
    }

    public getEntwurfForCurrentRolle(fallId: string): IPromise<TSMitteilung> {
        return this.http.get(this.serviceURL + '/entwurf/' + fallId).then((response: any) => {
            this.log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilung(new TSMitteilung(), response.data);
        });
    }

    public getMitteilungenForCurrentRolle(fallId: string): IPromise<Array<TSMitteilung>> {
        return this.http.get(this.serviceURL + '/forrole/' + fallId).then((response: any) => {
            this.log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilungen(response.data);
        });
    }

    public getMitteilungenForPosteingang(): IPromise<Array<TSMitteilung>> {
        return this.http.get(this.serviceURL + '/posteingang').then((response: any) => {
            this.log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilungen(response.data);
        });
    }

    public removeEntwurf(mitteilung: TSMitteilung): IPromise<any> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(mitteilung.id))
            .then((response) => {
                return response;
            });
    }

    public setAllNewMitteilungenOfFallGelesen(fallId: string): IPromise<Array<TSMitteilung>> {
        return this.http.put(this.serviceURL + '/setallgelesen/' + fallId, null).then((response: any) => {
            this.log.debug('PARSING mitteilungen REST objects ', response.data);
            return this.ebeguRestUtil.parseMitteilungen(response.data);
        });
    }
}
