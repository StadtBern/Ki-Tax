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

import IComponentOptions = angular.IComponentOptions;
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import TSAntragDTO from '../../../models/TSAntragDTO';
import TSAntragSearchresultDTO from '../../../models/TSAntragSearchresultDTO';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import SearchRS from '../../../gesuch/service/searchRS.rest';

let template = require('./pendenzenSteueramtListView.html');

export class PendenzenSteueramtListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = PendenzenSteueramtListViewController;
    controllerAs = 'vm';
}

export class PendenzenSteueramtListViewController {

    totalResultCount: string = '0';
    TSRoleUtil: any;


    static $inject: string[] = ['GesuchModelManager', '$state', '$log', 'SearchRS'];

    constructor(private gesuchModelManager: GesuchModelManager, private $state: IStateService, private $log: ILogService,
                private searchRS: SearchRS) {
        this.TSRoleUtil = TSRoleUtil;
    }

    $onInit() {
    }


    public editpendenzSteueramt(pendenz: TSAntragDTO, event: any): void {
        if (pendenz) {
            let isCtrlKeyPressed: boolean = (event && event.ctrlKey);
            this.openPendenz(pendenz, isCtrlKeyPressed);
        }
    }

    public passFilterToServer = (tableFilterState: any): IPromise<TSAntragSearchresultDTO> => {
        this.$log.debug('Triggering ServerFiltering with Filter Object', tableFilterState);
        return this.searchRS.searchAntraege(tableFilterState).then((response: TSAntragSearchresultDTO) => {
            this.totalResultCount = response.totalResultSize ? response.totalResultSize.toString() : '0';
            return response;
        });

    }

    private openPendenz(pendenz: TSAntragDTO, isCtrlKeyPressed: boolean) {
        this.gesuchModelManager.clearGesuch();
        let navObj: any = {
            gesuchId: pendenz.antragId
        };
        if (isCtrlKeyPressed) {
            let url = this.$state.href('gesuch.familiensituation', navObj);
            window.open(url, '_blank');
        } else {
            this.$state.go('gesuch.familiensituation', navObj);
        }
    }

}
