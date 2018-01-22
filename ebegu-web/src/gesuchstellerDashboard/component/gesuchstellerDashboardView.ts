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

import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import GesuchsperiodeRS from '../../core/service/gesuchsperiodeRS.rest';
import MitteilungRS from '../../core/service/mitteilungRS.rest';
import FallRS from '../../gesuch/service/fallRS.rest';
import SearchRS from '../../gesuch/service/searchRS.rest';
import {IN_BEARBEITUNG_BASE_NAME, isAnyStatusOfVerfuegt, TSAntragStatus} from '../../models/enums/TSAntragStatus';
import {TSEingangsart} from '../../models/enums/TSEingangsart';
import {TSGesuchBetreuungenStatus} from '../../models/enums/TSGesuchBetreuungenStatus';
import TSAntragDTO from '../../models/TSAntragDTO';
import TSFall from '../../models/TSFall';
import TSGesuchsperiode from '../../models/TSGesuchsperiode';
import EbeguUtil from '../../utils/EbeguUtil';
import {TSRoleUtil} from '../../utils/TSRoleUtil';
import ILogService = angular.ILogService;
import IPromise = angular.IPromise;
import ITranslateService = angular.translate.ITranslateService;

let template = require('./gesuchstellerDashboardView.html');
require('./gesuchstellerDashboardView.less');

export class GesuchstellerDashboardListViewConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = GesuchstellerDashboardListViewController;
    controllerAs = 'vm';
}

export class GesuchstellerDashboardListViewController {

    private antragList: Array<TSAntragDTO> = [];
    private _activeGesuchsperiodenList: Array<TSGesuchsperiode>;
    fallId: string;
    totalResultCount: string = '-';
    amountNewMitteilungen: number;


    static $inject: string[] = ['$state', '$log', 'AuthServiceRS', 'SearchRS', 'EbeguUtil', 'GesuchsperiodeRS',
        'FallRS', '$translate', 'MitteilungRS'];

    constructor(private $state: IStateService, private $log: ILogService,
                private authServiceRS: AuthServiceRS, private searchRS: SearchRS, private ebeguUtil: EbeguUtil,
                private gesuchsperiodeRS: GesuchsperiodeRS, private fallRS: FallRS, private $translate: ITranslateService,
                private mitteilungRS: MitteilungRS) {
    }

    $onInit() {
        this.initViewModel();
    }

    private initViewModel() {
        this.updateAntragList().then(() => {
            this.getAmountNewMitteilungen();
        });
        this.updateActiveGesuchsperiodenList();
    }

    private updateAntragList(): IPromise<any> {
        return this.fallRS.findFallByCurrentBenutzerAsBesitzer().then((existingFall: TSFall) => {
            if (existingFall) {
                this.fallId = existingFall.id;
                return this.searchRS.getAntraegeGesuchstellerList().then((response: any) => {
                    this.antragList = angular.copy(response);
                    return this.antragList;
                });
            } else { //fall es fuer den GS noch keine Fall gibt, erstellen wir einen
                return this.fallRS.createFallForCurrentBenutzerAsBesitzer().then((createdFall: TSFall) => {
                    if (createdFall) {
                        this.fallId = createdFall.id;
                    }
                    return this.antragList;
                });
            }
        });
    }

    private getAmountNewMitteilungen(): void {
        this.mitteilungRS.getAmountNewMitteilungenForCurrentRolle(this.fallId).then((response: number) => {
            this.amountNewMitteilungen = response;
        });
    }

    private updateActiveGesuchsperiodenList(): void {
        this.gesuchsperiodeRS.getAllActiveGesuchsperioden().then((response: TSGesuchsperiode[]) => {
            this._activeGesuchsperiodenList = angular.copy(response);
        });
    }

    public getActiveGesuchsperiodenList(): Array<TSGesuchsperiode> {
        return this._activeGesuchsperiodenList;
    }

    public goToMitteilungenOeffen() {
        this.$state.go('mitteilungen', {
            fallId: this.fallId
        });
    }

    public getAntragList(): Array<TSAntragDTO> {
        return this.antragList;
    }

    public displayAnsehenButton(periode: TSGesuchsperiode): boolean {
        let antrag: TSAntragDTO = this.getAntragForGesuchsperiode(periode);
        if (antrag) {
            if (TSAntragStatus.IN_BEARBEITUNG_GS === antrag.status) {
                return false;
            }
            return true;
        }
        return false;
    }

    public getNumberMitteilungen(): number {
        return this.amountNewMitteilungen;
    }

    public openAntrag(periode: TSGesuchsperiode, ansehen: boolean): void {
        let antrag: TSAntragDTO = this.getAntragForGesuchsperiode(periode);
        if (antrag) {
            if (TSAntragStatus.IN_BEARBEITUNG_GS === antrag.status || ansehen) {
                // Noch nicht freigegeben
                this.$state.go('gesuch.fallcreation', {createNew: false, gesuchId: antrag.antragId});
            } else if (!isAnyStatusOfVerfuegt(antrag.status) || antrag.beschwerdeHaengig) {
                // Alles ausser verfuegt und InBearbeitung
                this.$state.go('gesuch.dokumente', {createNew: false, gesuchId: antrag.antragId});
            } else {
                // Im Else-Fall ist das Gesuch nicht mehr ueber den Button verfuegbar
                // Es kann nur noch eine Mutation gemacht werden
                this.$state.go('gesuch.mutation', {
                    createMutation: true,
                    eingangsart: TSEingangsart.ONLINE,
                    gesuchId: antrag.antragId,
                    gesuchsperiodeId: periode.id,
                    fallId: this.fallId
                });
            }
        } else {
            // Noch kein Antrag für die Gesuchsperiode vorhanden
            if (this.antragList && this.antragList.length > 0) {
                // Aber schon mindestens einer für eine frühere Periode
                this.$state.go('gesuch.erneuerung', {
                    createErneuerung: true,
                    gesuchId: this.antragList[0].antragId,
                    eingangsart: TSEingangsart.ONLINE,
                    gesuchsperiodeId: periode.id,
                    fallId: this.fallId
                });
            } else {
                // Dies ist das erste Gesuch
                this.$state.go('gesuch.fallcreation', {
                    createNew: true,
                    eingangsart: TSEingangsart.ONLINE,
                    gesuchId: null,
                    gesuchsperiodeId: periode.id,
                    fallId: this.fallId
                });
            }
        }
    }

    public getButtonText(periode: TSGesuchsperiode): string {
        let antrag: TSAntragDTO = this.getAntragForGesuchsperiode(periode);
        if (antrag) {
            if (TSAntragStatus.IN_BEARBEITUNG_GS === antrag.status) {
                // Noch nicht freigegeben -> Text BEARBEITEN
                return this.$translate.instant('GS_BEARBEITEN');
            } else if (!isAnyStatusOfVerfuegt(antrag.status) || antrag.beschwerdeHaengig) {
                // Alles ausser verfuegt und InBearbeitung -> Text DOKUMENTE HOCHLADEN
                return this.$translate.instant('GS_DOKUMENTE_HOCHLADEN');
            } else {
                // Im Else-Fall ist das Gesuch nicht mehr ueber den Button verfuegbar
                // Es kann nur noch eine Mutation gemacht werden -> Text MUTIEREN
                return this.$translate.instant('GS_MUTIEREN');
            }
        } else {
            // Noch kein Antrag vorhanden -> Text GESUCH BEANTRAGEN
            // this.$state.go('gesuch.fallcreation', {createNew: true, gesuchId: null});
            return this.$translate.instant('GS_BEANTRAGEN');
        }
    }

    public editAntrag(antrag: TSAntragDTO): void {
        if (antrag) {
            if (isAnyStatusOfVerfuegt(antrag.status)) {
                this.$state.go('gesuch.verfuegen', {createNew: false, gesuchId: antrag.antragId});
            } else {
                this.$state.go('gesuch.fallcreation', {createNew: false, gesuchId: antrag.antragId});
            }
        }
    }

    private getAntragForGesuchsperiode(periode: TSGesuchsperiode): TSAntragDTO {
        // Die Antraege sind nach Laufnummer sortiert, d.h. der erste einer Periode ist immer der aktuellste
        if (this.antragList) {
            for (let antrag of this.antragList) {
                if (antrag.gesuchsperiodeGueltigAb.year() === periode.gueltigkeit.gueltigAb.year()) {
                    return antrag;
                }
            }
        }
        return undefined;
    }

    /**
     * Status muss speziell uebersetzt werden damit Gesuchsteller nur "In Bearbeitung" sieht und nicht in "Bearbeitung Gesuchsteller"
     */
    public translateStatus(antrag: TSAntragDTO) {
        let status: TSAntragStatus = antrag.status;
        let isUserGesuchsteller: boolean = this.authServiceRS.isOneOfRoles(TSRoleUtil.getGesuchstellerOnlyRoles());
        if (status === TSAntragStatus.IN_BEARBEITUNG_GS && isUserGesuchsteller) {
            if (TSGesuchBetreuungenStatus.ABGEWIESEN === antrag.gesuchBetreuungenStatus) {
                return this.ebeguUtil.translateString(TSAntragStatus[TSAntragStatus.PLATZBESTAETIGUNG_ABGEWIESEN]);
            } else if (TSGesuchBetreuungenStatus.WARTEN === antrag.gesuchBetreuungenStatus) {
                return this.ebeguUtil.translateString(TSAntragStatus[TSAntragStatus.PLATZBESTAETIGUNG_WARTEN]);
            }
            return this.ebeguUtil.translateString(IN_BEARBEITUNG_BASE_NAME);
        }
        if ((status === TSAntragStatus.NUR_SCHULAMT)
            && isUserGesuchsteller) {
            return this.ebeguUtil.translateString('ABGESCHLOSSEN');
        }
        return this.ebeguUtil.translateString(TSAntragStatus[status]);
    }

    /**
     * JA und Mischgesuche -> verantwortlicher
     * SCHGesuche -> verantwortlicherSCH (oder "Schulamt" wenn kein Verantwortlicher vorhanden
     */
    public getHauptVerantwortlicherFullName(antrag: TSAntragDTO): string {
        if (antrag) {
            if (antrag.verantwortlicher) {
                return antrag.verantwortlicher;
            }
            if (antrag.verantwortlicherSCH) {
                return antrag.verantwortlicherSCH;
            }
            if (antrag.status === TSAntragStatus.NUR_SCHULAMT) { //legacy for old Faelle where verantwortlicherSCH didn't exist
                return this.ebeguUtil.translateString('NUR_SCHULAMT');
            }
        }
        return '';
    }
}
