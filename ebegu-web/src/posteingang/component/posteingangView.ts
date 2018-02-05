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

import {IComponentOptions, ILogService, IPromise} from 'angular';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import MitteilungRS from '../../core/service/mitteilungRS.rest';
import {getAemterForFilter, TSAmt} from '../../models/enums/TSAmt';
import {getTSMitteilungsStatusForFilter, TSMitteilungStatus} from '../../models/enums/TSMitteilungStatus';
import TSMitteilung from '../../models/TSMitteilung';
import TSMtteilungSearchresultDTO from '../../models/TSMitteilungSearchresultDTO';
import EbeguUtil from '../../utils/EbeguUtil';
import {TSRoleUtil} from '../../utils/TSRoleUtil';
import IStateService = angular.ui.IStateService;
let template = require('./posteingangView.html');
require('./posteingangView.less');

export class PosteingangViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = PosteingangViewController;
    controllerAs = 'vm';
}

export class PosteingangViewController {

    displayedCollection: Array<TSMitteilung> = []; //Liste die im Gui angezeigt wird
    pagination: any = {};
    totalResultCount: string = '0';
    myTableFilterState: any; // Muss hier gespeichert werden, damit es fuer den Aufruf ab "Inkl.Erledigt"-Checkbox vorhanden ist

    itemsByPage: number = 20;
    numberOfPages: number = 1;
    selectedAmt: string;
    selectedMitteilungsstatus: TSMitteilungStatus;
    includeClosed: boolean;



    static $inject: string[] = ['MitteilungRS', 'EbeguUtil', 'CONSTANTS', '$state', 'AuthServiceRS', '$log'];

    constructor(private mitteilungRS: MitteilungRS, private ebeguUtil: EbeguUtil, private CONSTANTS: any, private $state: IStateService,
                private authServiceRS: AuthServiceRS, private $log: ILogService) {
    }

    public addZerosToFallNummer(fallnummer: number): string {
        return this.ebeguUtil.addZerosToNumber(fallnummer, this.CONSTANTS.FALLNUMMER_LENGTH);
    }

    private gotoMitteilung(mitteilung: TSMitteilung) {
        this.$state.go('mitteilungen', {
            fallId: mitteilung.fall.id
        });
    }

    isCurrentUserSchulamt(): boolean {
        let isUserSchulamt: boolean = this.authServiceRS.isOneOfRoles(TSRoleUtil.getSchulamtOnlyRoles());
        return isUserSchulamt;
    }

    getAemter(): Array<TSAmt> {
        return getAemterForFilter();
    }

    getMitteilungsStatus(): Array<TSMitteilungStatus> {
        return getTSMitteilungsStatusForFilter();
    }

    public clickedIncludeClosed(): void {
        this.passFilterToServer(this.myTableFilterState);
    }

    public passFilterToServer = (tableFilterState: any): IPromise<void> => {
        this.pagination = tableFilterState.pagination;
        this.myTableFilterState = tableFilterState;

        return this.mitteilungRS.searchMitteilungen(tableFilterState, this.includeClosed).then((result: TSMtteilungSearchresultDTO) => {
            this.setResult(result);
        });
    }

    private setResult(result: TSMtteilungSearchresultDTO): void {
        if (result) {
            this.pagination.totalItemCount = result.totalResultSize;
            this.pagination.numberOfPages = Math.ceil(result.totalResultSize / this.pagination.number);
            this.displayedCollection = [].concat(result.mitteilungen);
            this.totalResultCount = result.totalResultSize ? result.totalResultSize.toString() : '0';
        }
    }
}
