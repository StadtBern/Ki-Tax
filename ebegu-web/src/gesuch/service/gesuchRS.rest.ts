import {IEntityRS} from '../../core/service/iEntityRS.rest';
import {IHttpPromise, IHttpService, IPromise, ILogService} from 'angular';
import TSGesuch from '../../models/TSGesuch';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import WizardStepManager from './wizardStepManager';

export default class GesuchRS implements IEntityRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', 'WizardStepManager'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private $log: ILogService,
                private wizardStepManager: WizardStepManager) {
        this.serviceURL = REST_API + 'gesuche';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public createGesuch(gesuch: TSGesuch): IPromise<TSGesuch> {
        let sentGesuch = {};
        sentGesuch = this.ebeguRestUtil.gesuchToRestObject(sentGesuch, gesuch);
        return this.http.post(this.serviceURL, sentGesuch, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.$log.debug('PARSING gesuch REST object ', response.data);
            let convertedGesuch: TSGesuch = this.ebeguRestUtil.parseGesuch(new TSGesuch(), response.data);
            return this.wizardStepManager.updateFirstWizardStep(convertedGesuch.id).then(() => {
                return convertedGesuch;
            });
        });
    }

    public updateGesuch(gesuch: TSGesuch): IPromise<TSGesuch> {
        let sentGesuch = {};
        sentGesuch = this.ebeguRestUtil.gesuchToRestObject(sentGesuch, gesuch);
        return this.http.put(this.serviceURL, sentGesuch, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuch.id).then(() => {
                this.$log.debug('PARSING gesuch REST object ', response.data);
                return this.ebeguRestUtil.parseGesuch(new TSGesuch(), response.data);
            });
        });
    }

    public findGesuch(gesuchID: string): IPromise<TSGesuch> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuchID))
            .then((response: any) => {
                this.$log.debug('PARSING gesuch REST object ', response.data);
                return this.ebeguRestUtil.parseGesuch(new TSGesuch(), response.data);
            });
    }

    public findGesuchForInstitution(gesuchID: string): IPromise<TSGesuch> {
        return this.http.get(this.serviceURL + '/institution/' + encodeURIComponent(gesuchID))
            .then((response: any) => {
                this.$log.debug('PARSING gesuch (fuer Institutionen) REST object ', response.data);
                return this.ebeguRestUtil.parseGesuch(new TSGesuch(), response.data);
            });
    }

    public updateBemerkung(gesuchID: string, bemerkung: string): IHttpPromise<any> {
        return this.http.put(this.serviceURL + '/bemerkung/' + encodeURIComponent(gesuchID), bemerkung, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

}
