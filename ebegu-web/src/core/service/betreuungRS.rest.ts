import {IHttpService, ILogService, IPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSBetreuung from '../../models/TSBetreuung';
import WizardStepManager from '../../gesuch/service/wizardStepManager';

export default class BetreuungRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', 'WizardStepManager'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService,
                private wizardStepManager: WizardStepManager) {
        this.serviceURL = REST_API + 'betreuungen';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'BetreuungRS';
    }

    public findBetreuung(betreuungID: string): IPromise<TSBetreuung> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(betreuungID))
            .then((response: any) => {
                this.log.debug('PARSING betreuung REST object ', response.data);
                return this.ebeguRestUtil.parseBetreuung(new TSBetreuung(), response.data);
            });
    }

    public saveBetreuung(betreuung: TSBetreuung, kindId: string, gesuchId: string, abwesenheit: boolean): IPromise<TSBetreuung> {
        let restBetreuung = {};
        restBetreuung = this.ebeguRestUtil.betreuungToRestObject(restBetreuung, betreuung);
        return this.http.put(this.serviceURL + '/' + encodeURIComponent(kindId) + '/' + abwesenheit, restBetreuung, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.log.debug('PARSING Betreuung REST object ', response.data);
                return this.ebeguRestUtil.parseBetreuung(new TSBetreuung(), response.data);
            });
        });
    }

    removeBetreuung(betreuungId: string, gesuchId: string): IPromise<any> {
        return this.http.delete(this.serviceURL + '/' + encodeURIComponent(betreuungId))
            .then((response) => {
                this.wizardStepManager.findStepsFromGesuch(gesuchId);
                return response;
            });
    }

    /**
     * Diese Methode ruft den Service um alle uebergebenen Betreuungen zu speichern.
     * Dies wird empfohlen wenn mehrere Betreuungen gleichzeitig gespeichert werden muessen,
     * damit alles in einer Transaction passiert. Z.B. fuer Abwesenheiten
     */
    public saveBetreuungen(betreuungenToUpdate: Array<TSBetreuung>, gesuchId: string, saveForAbwesenheit: boolean): IPromise<Array<TSBetreuung>> {
        let restBetreuungen: Array<any> = [];
        betreuungenToUpdate.forEach((betreuungToUpdate: TSBetreuung) => {
            restBetreuungen.push(this.ebeguRestUtil.betreuungToRestObject({}, betreuungToUpdate));
        });
        return this.http.put(this.serviceURL + '/all/' + saveForAbwesenheit, restBetreuungen, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(() => {
                this.log.debug('PARSING Betreuung REST object ', response.data);
                let convertedBetreuungen: Array<TSBetreuung> = [];
                response.data.forEach((returnedBetreuung: any) => {
                    convertedBetreuungen.push(this.ebeguRestUtil.parseBetreuung(new TSBetreuung(), returnedBetreuung));
                });
                return convertedBetreuungen;
            });
        });
    }
}