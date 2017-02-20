import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpService, IPromise, ILogService} from 'angular';
import WizardStepManager from '../../gesuch/service/wizardStepManager';
import TSMitteilung from '../../models/TSMitteilung';
import TSBetreuung from '../../models/TSBetreuung';
import {TSMitteilungTeilnehmerTyp} from '../../models/enums/TSMitteilungTeilnehmerTyp';
import {TSMitteilungStatus} from '../../models/enums/TSMitteilungStatus';
import TSFall from '../../models/TSFall';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import TSBetreuungsmitteilung from '../../models/TSBetreuungsmitteilung';
import TSBetreuungspensum from '../../models/TSBetreuungspensum';
import ITranslateService = angular.translate.ITranslateService;
import DateUtil from '../../utils/DateUtil';

export default class MitteilungRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;
    log: ILogService;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', 'WizardStepManager',
        'AuthServiceRS', '$translate'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, $log: ILogService,
                private wizardStepManager: WizardStepManager, private authServiceRS: AuthServiceRS,
                private $translate: ITranslateService) {
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

    public getEntwurfForCurrentRolleForFall(fallId: string): IPromise<TSMitteilung> {
        return this.http.get(this.serviceURL + '/entwurf/fall/' + fallId).then((response: any) => {
            this.log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilung(new TSMitteilung(), response.data);
        });
    }

    public getEntwurfForCurrentRolleForBetreuung(betreuungId: string): IPromise<TSMitteilung> {
        return this.http.get(this.serviceURL + '/entwurf/betreuung/' + betreuungId).then((response: any) => {
            this.log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilung(new TSMitteilung(), response.data);
        });
    }

    public getMitteilungenForCurrentRolleForFall(fallId: string): IPromise<Array<TSMitteilung>> {
        return this.http.get(this.serviceURL + '/forrole/fall/' + fallId).then((response: any) => {
            this.log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilungen(response.data.mitteilungen); // The response is a wrapper
        });
    }

    public getMitteilungenForCurrentRolleForBetreuung(betreuungId: string): IPromise<Array<TSMitteilung>> {
        return this.http.get(this.serviceURL + '/forrole/betreuung/' + betreuungId).then((response: any) => {
            this.log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilungen(response.data.mitteilungen); // The response is a wrapper
        });
    }

    public getMitteilungenForPosteingang(): IPromise<Array<TSMitteilung>> {
        return this.http.get(this.serviceURL + '/posteingang').then((response: any) => {
            this.log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilungen(response.data.mitteilungen); // The response is a wrapper
        });
    }

    public getAmountMitteilungenForCurrentBenutzer(): IPromise<number> {
        return this.http.get(this.serviceURL + '/amountnewforuser').then((response: any) => {
            return response.data;
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
            return this.ebeguRestUtil.parseMitteilungen(response.data.mitteilungen); // The response is a wrapper
        });
    }

    public getAmountNewMitteilungenForCurrentRolle(fallId: string): IPromise<number> {
        return this.http.get(this.serviceURL + '/amountnew/' + fallId).then((response: any) => {
            return response.data;
        });
    }

    public sendbetreuungsmitteilung(fall: TSFall, betreuung: TSBetreuung): IPromise<TSBetreuungsmitteilung> {
        let mutationsmeldung: TSBetreuungsmitteilung = this.createBetreuungsmitteilung(fall, betreuung);
        let restMitteilung: any = this.ebeguRestUtil.betreuungsmitteilungToRestObject({}, mutationsmeldung);
        return this.http.put(this.serviceURL + '/sendbetreuungsmitteilung', restMitteilung, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseBetreuungsmitteilung(new TSBetreuungsmitteilung(), response.data);
        });
    }

    private createBetreuungsmitteilung(fall: TSFall, betreuung: TSBetreuung): TSBetreuungsmitteilung {
        let mutationsmeldung: TSBetreuungsmitteilung = new TSBetreuungsmitteilung();
        mutationsmeldung.fall = fall;
        mutationsmeldung.betreuung = betreuung;
        mutationsmeldung.senderTyp = TSMitteilungTeilnehmerTyp.INSTITUTION;
        mutationsmeldung.empfaengerTyp = TSMitteilungTeilnehmerTyp.JUGENDAMT;
        mutationsmeldung.sender = this.authServiceRS.getPrincipal();
        mutationsmeldung.empfaenger = fall.besitzer ? fall.besitzer : undefined;
        mutationsmeldung.subject = this.$translate.instant('MUTATIONSMELDUNG_BETREFF');
        mutationsmeldung.message = this.createNachrichtForMutationsmeldung(betreuung);
        mutationsmeldung.mitteilungStatus = TSMitteilungStatus.ENTWURF;
        mutationsmeldung.betreuungspensen = this.extractPensenFromBetreuung(betreuung);
        return mutationsmeldung;
    }

    /**
     * Erzeugt eine Nachricht mit einem Text mit allen Betreuungspensen der Betreuung.
     */
    private createNachrichtForMutationsmeldung(betreuung: TSBetreuung): string {
        let message: string = '';
        let i: number = 1;
        betreuung.betreuungspensumContainers.forEach(betpenContainer => {
            if (betpenContainer.betreuungspensumJA) {
                // Pensum 1 vom 1.8.2017: 80%
                if (i > 1) {
                    message += '\n';
                }
                message += 'Pensum ' + i + ' vom '
                    + DateUtil.momentToLocalDateFormat(betpenContainer.betreuungspensumJA.gueltigkeit.gueltigAb, 'DD.MM.YYYY')
                    + ': ' + betpenContainer.betreuungspensumJA.pensum + '%';
            }
            i++;
        });
        return message;
    }

    /**
     * Kopiert alle Betreuungspensen der gegebenen Betreuung in einer neuen Liste und
     * gibt diese zurueck. By default wird eine leere Liste zurueckgegeben
     */
    private extractPensenFromBetreuung(betreuung: TSBetreuung): Array<TSBetreuungspensum> {
        let pensen: Array<TSBetreuungspensum> = [];
        betreuung.betreuungspensumContainers.forEach(betpenContainer => {
            let pensumJA = angular.copy(betpenContainer.betreuungspensumJA);
            pensumJA.id = undefined; // the id must be set to undefined in order no to duplicate it
            pensen.push(pensumJA);
        });
        return pensen;
    }
}
