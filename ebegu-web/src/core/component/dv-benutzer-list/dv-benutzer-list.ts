/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {getTSRoleValues, getTSRoleValuesWithoutSuperAdmin, rolePrefix, TSRole} from '../../../models/enums/TSRole';
import TSInstitution from '../../../models/TSInstitution';
import {TSTraegerschaft} from '../../../models/TSTraegerschaft';
import TSUser from '../../../models/TSUser';
import TSUserSearchresultDTO from '../../../models/TSUserSearchresultDTO';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import {InstitutionRS} from '../../service/institutionRS.rest';
import {TraegerschaftRS} from '../../service/traegerschaftRS.rest';
import ITranslateService = angular.translate.ITranslateService;

let template = require('./dv-benutzer-list.html');
require('./dv-benutzer-list.less');

export class DVBenutzerListConfig implements IComponentOptions {
    transclude = false;

    bindings: any = {
        onEdit: '&',
        onFilterChange: '&',
        totalResultCount: '<',
        tableId: '@',
        tableTitle: '@',
    };
    template = template;
    controller = DVBenutzerListController;
    controllerAs = 'vm';
}

export class DVBenutzerListController {

    totalResultCount: number;
    displayedCollection: Array<TSUser> = []; //Liste die im Gui angezeigt wird
    pagination: any;

    institutionenList: Array<TSInstitution>;
    traegerschaftenList: Array<TSTraegerschaft>;

    selectedUsername: string;
    selectedVorname: string;
    selectedNachname: string;
    selectedEmail: string;
    selectedRole: string;
    selectedInstitution: TSInstitution;
    selectedTraegerschaft: TSTraegerschaft;
    selectedGesperrt: string;

    tableId: string;
    tableTitle: string;

    onFilterChange: (changedTableState: any) => IPromise<any>;
    onEdit: (user: any) => void;
    TSRoleUtil: any;

    static $inject: any[] = ['$log', 'InstitutionRS', 'TraegerschaftRS', 'AuthServiceRS', '$window', '$translate'];
    /* @ngInject */
    constructor(private $log: ILogService, private institutionRS: InstitutionRS, private traegerschaftenRS: TraegerschaftRS,
               private authServiceRS: AuthServiceRS, private $window: ng.IWindowService, private $translate: ITranslateService) {

        this.TSRoleUtil = TSRoleUtil;
    }

    $onInit() {
        //statt diese Listen zu laden koenne man sie auch von aussen setzen
        this.updateInstitutionenList();
        this.updateTraegerschaftenList();
    }

    private updateInstitutionenList(): void {
        this.institutionRS.getAllInstitutionen().then((response: any) => {
            this.institutionenList = angular.copy(response);
        });
    }

    private updateTraegerschaftenList(): void {
        this.traegerschaftenRS.getAllTraegerschaften().then((response: any) => {
            this.traegerschaftenList = angular.copy(response);
        });
    }

    editClicked(user: any, event: any) {
        this.onEdit({user: user, event: event});
    }

    private callServer = (tableFilterState: any) => {
        let pagination = tableFilterState.pagination;
        this.pagination = pagination;

        if (this.onFilterChange && angular.isFunction(this.onFilterChange)) {
            this.onFilterChange({tableState: tableFilterState}).then((result: TSUserSearchresultDTO) => {
                if (result) {
                    pagination.totalItemCount = result.totalResultSize;
                    pagination.numberOfPages = Math.ceil(result.totalResultSize / pagination.number);
                    this.displayedCollection = [].concat(result.userDTOs);
                }
            });
        } else {
            this.$log.info('no callback function spcified for filtering');
        }
    }

    public getRollen(): Array<TSRole> {
        if (this.authServiceRS.isRole(TSRole.SUPER_ADMIN)) {
            return getTSRoleValues();
        }
        return getTSRoleValuesWithoutSuperAdmin();
    }

    public getTranslatedRole(role: TSRole): string {
        return this.$translate.instant(rolePrefix() + role);
    }

    /**
     * Provided there is a row with id benutzerHeadRow it will take this row to check how many
     * columns there are. Therefore this row cannot have any colspan inside any cell and any other
     * children but td or th
     */
    public getColumnsNumber(): number {
        let element = this.$window.document.getElementById('benutzerHeadRow');
        return element.childElementCount;
    }
}



