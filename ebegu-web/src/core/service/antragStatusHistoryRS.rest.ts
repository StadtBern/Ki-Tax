import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, ILogService, IPromise} from 'angular';
import TSAntragStatusHistory from '../../models/TSAntragStatusHistory';
import TSGesuch from '../../models/TSGesuch';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';

export default class AntragStatusHistoryRS {

    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    private _lastChange: TSAntragStatusHistory;


    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', 'AuthServiceRS'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService,
                private authServiceRS: AuthServiceRS) {
        this.serviceURL = REST_API + 'antragStatusHistory';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
        this.log = $log;
    }

    public getServiceName(): string {
        return 'AntragStatusHistoryRS';
    }

    get lastChange(): TSAntragStatusHistory {
        return this._lastChange;
    }

    public findLastStatusChange(gesuch: TSGesuch): IPromise<TSAntragStatusHistory> {
        if (gesuch && gesuch.id) {
            return this.http.get(this.serviceURL + '/' + encodeURIComponent(gesuch.id))
                .then((response: any) => {
                    this.log.debug('PARSING AntragStatusHistory REST object ', response.data);
                    return this._lastChange = this.ebeguRestUtil.parseAntragStatusHistory(new TSAntragStatusHistory(), response.data);
                });
        } else {
            this._lastChange = undefined;
        }
        return undefined;
    }

    /**
     * Gibt den FullName des Benutzers zurueck, der den Gesuchsstatus am letzten geaendert hat. Sollte das Gesuch noch nicht
     * gespeichert sein (fallCreation), wird der FullName des eingeloggten Benutzers zurueckgegeben
     * @returns {any}
     */
    public getUserFullname(): string {
        if (this.lastChange) {
            return this.lastChange.benutzer.getFullName();
        } else {
            if (this.authServiceRS && this.authServiceRS.getPrincipal()) {
                return this.authServiceRS.getPrincipal().getFullName();
            }
        }
        return '';
    }

}
