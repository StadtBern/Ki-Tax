import {IEntityRS} from '../../core/service/iEntityRS.rest';
import {IHttpPromise, IHttpService, IPromise, ILogService} from 'angular';
import TSGesuch from '../../models/TSGesuch';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import WizardStepManager from './wizardStepManager';
import {TSAntragStatus} from '../../models/enums/TSAntragStatus';
import TSAntragSearchresultDTO from '../../models/TSAntragSearchresultDTO';
import TSAntragDTO from '../../models/TSAntragDTO';
import DateUtil from '../../utils/DateUtil';
import * as moment from 'moment';

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

    public findGesuchForFreigabe(gesuchID: string): IPromise<TSAntragDTO> {
        return this.http.get(this.serviceURL + '/freigabe/' + encodeURIComponent(gesuchID))
            .then((response: any) => {
                this.$log.debug('PARSING antragDTO REST object ', response.data);
                return this.ebeguRestUtil.parseAntragDTO(new TSAntragDTO(), response.data);
            });
    }


    public findGesuchForInstitution(gesuchID: string): IPromise<TSGesuch> {
        return this.http.get(this.serviceURL + '/institution/' + encodeURIComponent(gesuchID))
            .then((response: any) => {
                this.$log.debug('PARSING gesuch (fuer Institutionen) REST object ', response.data);
                return this.ebeguRestUtil.parseGesuch(new TSGesuch(), response.data);
            });
    }

    public searchAntraege(antragSearch: any): IPromise<TSAntragSearchresultDTO> {
        return this.http.post(this.serviceURL + '/search/', antragSearch, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.$log.debug('PARSING antraege REST array object', response.data);
            return new TSAntragSearchresultDTO(this.ebeguRestUtil.parseAntragDTOs(response.data.antragDTOs), response.data.paginationDTO.totalItemCount);
        });
    }

    public updateBemerkung(gesuchID: string, bemerkung: string): IHttpPromise<any> {
        return this.http.put(this.serviceURL + '/bemerkung/' + encodeURIComponent(gesuchID), bemerkung, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public updateBemerkungPruefungSTV(gesuchID: string, bemerkungPruefungSTV: string): IHttpPromise<any> {
        return this.http.put(this.serviceURL + '/bemerkungPruefungSTV/' + encodeURIComponent(gesuchID), bemerkungPruefungSTV, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    public updateGesuchStatus(gesuchID: string, status: TSAntragStatus): IHttpPromise<any> {
        return this.http.put(this.serviceURL + '/status/' + encodeURIComponent(gesuchID) + '/' + status, null);
    }

    public getAllAntragDTOForFall(fallId: string): IPromise<TSAntragDTO[]> {
        return this.http.get(this.serviceURL + '/fall/' + encodeURIComponent(fallId)).then((response: any) => {
            return this.ebeguRestUtil.parseAntragDTOs(response.data);
        });
    }

    public antragMutieren(antragId: string, dateParam: moment.Moment): IPromise<TSGesuch> {
        return this.http.post(this.serviceURL + '/mutieren/' + encodeURIComponent(antragId), null,
            {params: {date: DateUtil.momentToLocalDate(dateParam)}}).then((response) => {
            return this.ebeguRestUtil.parseGesuch(new TSGesuch(), response.data);
        });
    }

    public antragErneuern(gesuchsperiodeId: string, antragId: string, dateParam: moment.Moment): IPromise<TSGesuch> {
        return this.http.post(this.serviceURL + '/erneuern/' + encodeURIComponent(gesuchsperiodeId) + '/' + encodeURIComponent(antragId), null,
            {params: {date: DateUtil.momentToLocalDate(dateParam)}}).then((response) => {
            return this.ebeguRestUtil.parseGesuch(new TSGesuch(), response.data);
        });
    }

    public antragFreigeben(antragId: string, username: string): IPromise<TSGesuch> {
        return this.http.post(this.serviceURL + '/freigeben/' + encodeURIComponent(antragId), username, {
            headers: {
                'Content-Type': 'text/plain'
            }
        }).then((response) => {
            return this.ebeguRestUtil.parseGesuch(new TSGesuch(), response.data);
        });
    }

    public setBeschwerdeHaengig(antragId: string): IPromise<TSGesuch> {
        return this.http.post(this.serviceURL + '/setBeschwerde/' + encodeURIComponent(antragId), null).then((response) => {
            return this.ebeguRestUtil.parseGesuch(new TSGesuch(), response.data);
        });
    }

    public sendGesuchToSTV(antragId: string, bemerkungen: string): IPromise<TSGesuch> {
        return this.http.post(this.serviceURL + '/sendToSTV/' + encodeURIComponent(antragId), bemerkungen, null).then((response) => {
            return this.ebeguRestUtil.parseGesuch(new TSGesuch(), response.data);
        });
    }

    public gesuchBySTVFreigeben(antragId: string): IPromise<TSGesuch> {
        return this.http.post(this.serviceURL + '/freigebenSTV/' + encodeURIComponent(antragId), null).then((response) => {
            return this.ebeguRestUtil.parseGesuch(new TSGesuch(), response.data);
        });
    }

    public stvPruefungAbschliessen(antragId: string): IPromise<TSGesuch> {
        return this.http.post(this.serviceURL + '/stvPruefungAbschliessen/' + encodeURIComponent(antragId), null).then((response) => {
            return this.ebeguRestUtil.parseGesuch(new TSGesuch(), response.data);
        });
    }

    public removeBeschwerdeHaengig(antragId: string): IPromise<TSGesuch> {
        return this.http.post(this.serviceURL + '/removeBeschwerde/' + encodeURIComponent(antragId), null).then((response) => {
            return this.ebeguRestUtil.parseGesuch(new TSGesuch(), response.data);
        });
    }

    public getNeuestesGesuchFromGesuch(gesuchID: string):  IPromise<boolean> {
        return this.http.get(this.serviceURL + '/neuestesgesuch/' + encodeURIComponent(gesuchID))
            .then((response: any) => {
                return response.data;
            });
    }

    public removeOnlineMutation(gesuchID: string):  IPromise<boolean> {
        return this.http.delete(this.serviceURL + '/removeOnlineMutation/' + encodeURIComponent(gesuchID))
            .then((response: any) => {
                return response.data;
            });
    }

    public removeOnlineFolgegesuch(gesuchID: string, gesuchsperiodId: string): IPromise<boolean> {
        return this.http.delete(this.serviceURL + '/removeOnlineFolgegesuch/' + encodeURIComponent(gesuchID)
            + '/' + encodeURIComponent(gesuchsperiodId))
            .then((response: any) => {
                return response.data;
            });
    }
}
