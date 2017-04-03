import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IPromise, ILogService} from 'angular';
import WizardStepManager from '../../gesuch/service/wizardStepManager';
import TSGesuchstellerContainer from '../../models/TSGesuchstellerContainer';
import TSEWKResultat from '../../models/TSEWKResultat';

export default class GesuchstellerRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', 'WizardStepManager'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService,
                private wizardStepManager: WizardStepManager) {
        this.serviceURL = REST_API + 'gesuchsteller';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;

    }

    public saveGesuchsteller(gesuchsteller: TSGesuchstellerContainer, gesuchId: string, gsNumber: number, umzug: boolean): IPromise<TSGesuchstellerContainer> {
        let gessteller = this.ebeguRestUtil.gesuchstellerContainerToRestObject({}, gesuchsteller);
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(gesuchId) + '/gsNumber/' + gsNumber + '/' + umzug, gessteller, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.log.debug('PARSING gesuchsteller REST object ', response.data);
                return this.ebeguRestUtil.parseGesuchstellerContainer(new TSGesuchstellerContainer(), response.data);
            });
        });
    }

    public findGesuchsteller(gesuchstellerID: string): IPromise<TSGesuchstellerContainer> {
        return this.http.get(this.serviceURL + '/find/' + encodeURIComponent(gesuchstellerID))
            .then((response: any) => {
                this.log.debug('PARSING gesuchsteller REST object ', response.data);
                return this.ebeguRestUtil.parseGesuchstellerContainer(new TSGesuchstellerContainer(), response.data);
            });
    }

    public suchePerson(gesuchstellerID: string): IPromise<TSEWKResultat> {
        return this.http.get(this.serviceURL + '/ewk/' + encodeURIComponent(gesuchstellerID))
            .then((response: any) => {
                this.log.debug('PARSING ewkResultat REST object ', response.data);
                return this.ebeguRestUtil.parseEWKResultat(new TSEWKResultat(), response.data);
            });
    }

    public getServiceName(): string {
        return 'GesuchstellerRS';
    }

}

