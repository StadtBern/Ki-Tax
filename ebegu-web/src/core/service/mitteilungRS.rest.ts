/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import {IHttpService, ILogService, IPromise} from 'angular';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import WizardStepManager from '../../gesuch/service/wizardStepManager';
import {TSMitteilungStatus} from '../../models/enums/TSMitteilungStatus';
import {TSMitteilungTeilnehmerTyp} from '../../models/enums/TSMitteilungTeilnehmerTyp';
import TSBetreuung from '../../models/TSBetreuung';
import TSBetreuungsmitteilung from '../../models/TSBetreuungsmitteilung';
import TSBetreuungsmitteilungPensum from '../../models/TSBetreuungsmitteilungPensum';
import TSBetreuungspensum from '../../models/TSBetreuungspensum';
import TSBetreuungspensumContainer from '../../models/TSBetreuungspensumContainer';
import TSFall from '../../models/TSFall';
import TSMitteilung from '../../models/TSMitteilung';
import TSMtteilungSearchresultDTO from '../../models/TSMitteilungSearchresultDTO';
import DateUtil from '../../utils/DateUtil';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import ITranslateService = angular.translate.ITranslateService;

export default class MitteilungRS {
    serviceURL: string;
    http: IHttpService;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['$http', 'REST_API', 'EbeguRestUtil', '$log', 'WizardStepManager',
        'AuthServiceRS', '$translate'];
    /* @ngInject */
    constructor($http: IHttpService, REST_API: string, ebeguRestUtil: EbeguRestUtil, private $log: ILogService,
                private wizardStepManager: WizardStepManager, private authServiceRS: AuthServiceRS,
                private $translate: ITranslateService) {
        this.serviceURL = REST_API + 'mitteilungen';
        this.http = $http;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public getServiceName(): string {
        return 'MitteilungRS';
    }

    public findMitteilung(mitteilungID: string): IPromise<TSMitteilung> {
        return this.http.get(this.serviceURL + '/' + encodeURIComponent(mitteilungID))
            .then((response: any) => {
                this.$log.debug('PARSING Mitteilung REST object ', response.data);
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
            this.$log.debug('PARSING mitteilung REST object ', response.data);
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
            this.$log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilung(new TSMitteilung(), response.data);
        });
    }

    public setMitteilungGelesen(mitteilungId: string): IPromise<TSMitteilung> {
        return this.http.put(this.serviceURL + '/setgelesen/' + mitteilungId, null).then((response: any) => {
            this.$log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilung(new TSMitteilung(), response.data);
        });
    }

    public setMitteilungErledigt(mitteilungId: string): IPromise<TSMitteilung> {
        return this.http.put(this.serviceURL + '/seterledigt/' + mitteilungId, null).then((response: any) => {
            this.$log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilung(new TSMitteilung(), response.data);
        });
    }

    public getEntwurfForCurrentRolleForFall(fallId: string): IPromise<TSMitteilung> {
        return this.http.get(this.serviceURL + '/entwurf/fall/' + fallId).then((response: any) => {
            this.$log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilung(new TSMitteilung(), response.data);
        });
    }

    public getEntwurfForCurrentRolleForBetreuung(betreuungId: string): IPromise<TSMitteilung> {
        return this.http.get(this.serviceURL + '/entwurf/betreuung/' + betreuungId).then((response: any) => {
            this.$log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilung(new TSMitteilung(), response.data);
        });
    }

    public getMitteilungenForCurrentRolleForFall(fallId: string): IPromise<Array<TSMitteilung>> {
        return this.http.get(this.serviceURL + '/forrole/fall/' + fallId).then((response: any) => {
            this.$log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilungen(response.data.mitteilungen); // The response is a wrapper
        });
    }

    public getMitteilungenForCurrentRolleForBetreuung(betreuungId: string): IPromise<Array<TSMitteilung>> {
        return this.http.get(this.serviceURL + '/forrole/betreuung/' + betreuungId).then((response: any) => {
            this.$log.debug('PARSING mitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseMitteilungen(response.data.mitteilungen); // The response is a wrapper
        });
    }

    public getAmountMitteilungenForCurrentBenutzer(): IPromise<number> {
        return this.http.get(this.serviceURL + '/amountnewforuser/notokenrefresh').then((response: any) => {
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
            this.$log.debug('PARSING mitteilungen REST objects ', response.data);
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
            this.$log.debug('PARSING Betreuungsmitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseBetreuungsmitteilung(new TSBetreuungsmitteilung(), response.data);
        });
    }

    public applyBetreuungsmitteilung(betreuungsmitteilungId: string): IPromise<string> {
        return this.http.put(this.serviceURL + '/applybetreuungsmitteilung/' + betreuungsmitteilungId, null, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            return response.data;
        });
    }

    public getNewestBetreuungsmitteilung(betreuungId: string): IPromise<TSBetreuungsmitteilung> {
        return this.http.get(this.serviceURL + '/newestBetreuunsmitteilung/' + betreuungId).then((response: any) => {
            this.$log.debug('PARSING Betreuungsmitteilung REST object ', response.data);
            return this.ebeguRestUtil.parseBetreuungsmitteilung(new TSBetreuungsmitteilung(), response.data);
        });
    }

    public mitteilungUebergebenAnJugendamt(mitteilungId: string): IPromise<TSMitteilung> {
        return this.http.get(this.serviceURL + '/delegation/jugendamt/' + mitteilungId).then((response: any) => {
            return this.ebeguRestUtil.parseMitteilung(new TSMitteilung(), response.data);
        });
    }

    public mitteilungUebergebenAnSchulamt(mitteilungId: string): IPromise<TSMitteilung> {
        return this.http.get(this.serviceURL + '/delegation/schulamt/' + mitteilungId).then((response: any) => {
            return this.ebeguRestUtil.parseMitteilung(new TSMitteilung(), response.data);
        });
    }

    public searchMitteilungen(antragSearch: any, includeClosed: boolean): IPromise<TSMtteilungSearchresultDTO> {
        return this.http.post(this.serviceURL + '/search/' + includeClosed, antragSearch, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then((response: any) => {
            this.$log.debug('PARSING antraege REST array object', response);
            return new TSMtteilungSearchresultDTO(this.ebeguRestUtil.parseMitteilungen(response.data.mitteilungDTOs), response.data.paginationDTO.totalItemCount);
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
        let betreuungspensumContainers: Array<TSBetreuungspensumContainer> = angular.copy(betreuung.betreuungspensumContainers); // to avoid changing something
        betreuungspensumContainers
            .sort(
                (a: TSBetreuungspensumContainer, b: TSBetreuungspensumContainer) => {
                    return DateUtil.compareDateTime(a.betreuungspensumJA.gueltigkeit.gueltigAb, b.betreuungspensumJA.gueltigkeit.gueltigAb);
                }
            ).forEach(betpenContainer => {
            if (betpenContainer.betreuungspensumJA) {
                // z.B. -> Pensum 1 vom 1.8.2017 bis 31.07.2018: 80%
                if (i > 1) {
                    message += '\n';
                }
                let datumAb: string = DateUtil.momentToLocalDateFormat(betpenContainer.betreuungspensumJA.gueltigkeit.gueltigAb, 'DD.MM.YYYY');
                let datumBis: string = DateUtil.momentToLocalDateFormat(betpenContainer.betreuungspensumJA.gueltigkeit.gueltigBis, 'DD.MM.YYYY');
                datumBis = datumBis ? datumBis : DateUtil.momentToLocalDateFormat(betreuung.gesuchsperiode.gueltigkeit.gueltigBis, 'DD.MM.YYYY'); // by default Ende der Periode
                message += this.$translate.instant('MUTATIONSMELDUNG_PENSUM') + i
                    + this.$translate.instant('MUTATIONSMELDUNG_VON') + datumAb
                    + this.$translate.instant('MUTATIONSMELDUNG_BIS') + datumBis
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
    private extractPensenFromBetreuung(betreuung: TSBetreuung): Array<TSBetreuungsmitteilungPensum> {
        let pensen: Array<TSBetreuungsmitteilungPensum> = [];
        betreuung.betreuungspensumContainers.forEach(betpenContainer => {
            let pensumJA = angular.copy(betpenContainer.betreuungspensumJA);
            pensumJA.id = undefined; // the id must be set to undefined in order no to duplicate it
            pensen.push(this.convertBetreuungspensumToBetreuungsmitteilung(pensumJA));
        });
        return pensen;
    }

    private convertBetreuungspensumToBetreuungsmitteilung(betreuungspensum: TSBetreuungspensum): TSBetreuungsmitteilungPensum {
        const betreuungsmitteilung = new TSBetreuungsmitteilungPensum();
        betreuungsmitteilung.monatlicheMittagessen = betreuungspensum.monatlicheMittagessen;
        betreuungsmitteilung.pensum = betreuungspensum.pensum;
        betreuungsmitteilung.gueltigkeit = betreuungspensum.gueltigkeit;
        return undefined;
    }
}
