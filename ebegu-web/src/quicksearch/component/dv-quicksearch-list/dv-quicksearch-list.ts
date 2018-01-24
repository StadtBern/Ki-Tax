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

import {IComponentOptions, IFilterService} from 'angular';
import {getNormalizedTSAntragTypValues, TSAntragTyp} from '../../../models/enums/TSAntragTyp';
import {getTSAntragStatusValuesByRole, TSAntragStatus} from '../../../models/enums/TSAntragStatus';
import {getTSBetreuungsangebotTypValues, TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import TSInstitution from '../../../models/TSInstitution';
import TSAntragDTO from '../../../models/TSAntragDTO';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import EbeguUtil from '../../../utils/EbeguUtil';
import {InstitutionRS} from '../../../core/service/institutionRS.rest';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import {IStateService} from 'angular-ui-router';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import TSUser from '../../../models/TSUser';
import TSAbstractAntragDTO from '../../../models/TSAbstractAntragDTO';
import TSFallAntragDTO from '../../../models/TSFallAntragDTO';

let template = require('./dv-quicksearch-list.html');
require('./dv-quicksearch-list.less');

export class DVQuicksearchListConfig implements IComponentOptions {
    transclude = false;

    bindings: any = {
        antraege: '<',
        itemsByPage: '<',
        initialAll: '=',
        showSelectionAll: '=',
        totalResultCount: '<',
        onUserChanged: '&',
        tableId: '@',
        tableTitle: '<'
    };

    template = template;
    controller = DVQuicksearchListController;
    controllerAs = 'vm';
}

export class DVQuicksearchListController {

    antraege: Array<TSAntragDTO> = []; //muss hier gesuch haben damit Felder die wir anzeigen muessen da sind

    itemsByPage: number;
    initialAll: boolean;
    showSelectionAll: boolean;
    tableId: string;
    tableTitle: string;

    selectedVerantwortlicher: TSUser;
    selectedVerantwortlicherSCH: TSUser;
    selectedEingangsdatum: string;
    selectedKinder: string;
    selectedFallNummer: string;
    selectedFamilienName: string;
    selectedBetreuungsangebotTyp: string;
    selectedAntragTyp: string;
    selectedAntragStatus: string;
    selectedInstitution: TSInstitution;
    selectedGesuchsperiode: string;
    selectedDokumenteHochgeladen: string;

    institutionenList: Array<TSInstitution>;
    gesuchsperiodenList: Array<string>;
    onUserChanged: (user: any) => void;


    static $inject: string[] = ['EbeguUtil', '$filter', 'InstitutionRS', 'GesuchsperiodeRS',
        '$state', 'CONSTANTS', 'AuthServiceRS'];

    constructor(private ebeguUtil: EbeguUtil, private $filter: IFilterService,
                private institutionRS: InstitutionRS, private gesuchsperiodeRS: GesuchsperiodeRS,
                private $state: IStateService, private CONSTANTS: any, private authServiceRS: AuthServiceRS) {
    }
    $onInit() {
        this.initViewModel();
    }

    public userChanged(selectedUser: TSUser): void {
        this.onUserChanged({user: selectedUser});
    }

    private initViewModel() {
        this.updateInstitutionenList();
        this.updateGesuchsperiodenList();
    }

    public getAntragTypen(): Array<TSAntragTyp> {
        return getNormalizedTSAntragTypValues();
    }

    public getAntragStatus(): Array<TSAntragStatus> {
        return getTSAntragStatusValuesByRole(this.authServiceRS.getPrincipalRole());
    }

    public getBetreuungsangebotTypen(): Array<TSBetreuungsangebotTyp> {
        return getTSBetreuungsangebotTypValues();
    }

    public updateGesuchsperiodenList(): void {
        this.gesuchsperiodeRS.getAllNichtAbgeschlosseneGesuchsperioden().then((response: any) => {
            this.gesuchsperiodenList = [];
            response.forEach((gesuchsperiode: TSGesuchsperiode) => {
                this.gesuchsperiodenList.push(gesuchsperiode.gesuchsperiodeString);
            });
        });
    }

    public updateInstitutionenList(): void {
        this.institutionRS.getAllInstitutionen().then((response: any) => {
            this.institutionenList = angular.copy(response);
        });
    }

    public getQuicksearchList(): Array<TSAntragDTO> {
        return this.antraege;
    }

    /**
     * Fallnummer muss 6-stellig dargestellt werden. Deshalb muessen so viele 0s am Anfang hinzugefuegt werden
     * bis die Fallnummer ein 6-stelliges String ist
     * @param fallnummer
     */
    public addZerosToFallnummer(fallnummer: number): string {
        return this.ebeguUtil.addZerosToNumber(fallnummer, this.CONSTANTS.FALLNUMMER_LENGTH);
    }

    public translateBetreuungsangebotTypList(betreuungsangebotTypList: Array<TSBetreuungsangebotTyp>): string {
        let result: string = '';
        if (betreuungsangebotTypList) {
            let prefix: string = '';
            if (betreuungsangebotTypList && Array.isArray(betreuungsangebotTypList)) {
                for (let i = 0; i < betreuungsangebotTypList.length; i++) {
                    let tsBetreuungsangebotTyp = TSBetreuungsangebotTyp[betreuungsangebotTypList[i]];
                    result = result + prefix + this.$filter('translate')(tsBetreuungsangebotTyp).toString();
                    prefix = ', ';
                }
            }
        }
        return result;
    }

    public editAntrag(abstractAntrag: TSAbstractAntragDTO, event: any): void {
        if (abstractAntrag) {
            let isCtrlKeyPressed: boolean = (event && event.ctrlKey);
            if (abstractAntrag instanceof TSAntragDTO) {
                this.navigateToGesuch(abstractAntrag, isCtrlKeyPressed);
            } else if (abstractAntrag instanceof TSFallAntragDTO) {
                this.navigateToMitteilungen(isCtrlKeyPressed, abstractAntrag);
            }
        }
    }

    private navigateToMitteilungen(isCtrlKeyPressed: boolean, fallAntrag: TSFallAntragDTO) {
        if (isCtrlKeyPressed) {
            let url = this.$state.href('mitteilungen', {fallId: fallAntrag.fallID});
            window.open(url, '_blank');
        } else {
            this.$state.go('mitteilungen', {fallId: fallAntrag.fallID});
        }
    }

    private navigateToGesuch(antragDTO: TSAntragDTO, isCtrlKeyPressed: boolean) {
        if (antragDTO.antragId) {
            let navObj: any = {
                createNew: false,
                gesuchId: antragDTO.antragId
            };
            if (isCtrlKeyPressed) {
                let url = this.$state.href('gesuch.fallcreation', navObj);
                window.open(url, '_blank');
            } else {
                this.$state.go('gesuch.fallcreation', navObj);
            }
        }
    }

    private showOnlineGesuchIcon(row: TSAbstractAntragDTO): boolean {
        return row instanceof TSAntragDTO && row.hasBesitzer();
    }

    private showPapierGesuchIcon(row: TSAbstractAntragDTO): boolean {
        return row instanceof TSAntragDTO && !row.hasBesitzer();
    }
}



