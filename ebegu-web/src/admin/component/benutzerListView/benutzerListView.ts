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
import UserRS from '../../../core/service/userRS.rest';
import TSUser from '../../../models/TSUser';
import TSUserSearchresultDTO from '../../../models/TSUserSearchresultDTO';
import AbstractAdminViewController from '../../abstractAdminView';
import './benutzerListView.less';
import IStateService = angular.ui.IStateService;

let template = require('./benutzerListView.html');
let style = require('./benutzerListView.less');

export class BenutzerListViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    bindings: any = {
        benutzer: '<',
    };
    template: string = template;
    controller: any = BenutzerListViewController;
    controllerAs: string = 'vm';
}

export class BenutzerListViewController extends AbstractAdminViewController {

    totalResultCount: string = '0';


    static $inject: string[] = ['$state', '$log', 'AuthServiceRS', 'UserRS'];

    constructor(private $state: IStateService, private $log: ILogService, authServiceRS: AuthServiceRS, private userRS: UserRS) {
        super(authServiceRS);
    }

    public passFilterToServer = (tableFilterState: any): IPromise<TSUserSearchresultDTO> => {
        this.$log.debug('Triggering ServerFiltering with Filter Object', tableFilterState);

        return this.userRS.searchUsers(tableFilterState).then((response: TSUserSearchresultDTO) => {
            this.totalResultCount = response.totalResultSize ? response.totalResultSize.toString() : '0';
            return response;
        });
    }

    /**
     * Fuer Benutzer mit der Rolle SACHBEARBEITER_INSTITUTION oder SACHBEARBEITER_TRAEGERSCHAFT oeffnet es das Gesuch mit beschraenkten Daten
     * Fuer anderen Benutzer wird das Gesuch mit allen Daten geoeffnet
     * @param user
     * @param event optinally this function can check if ctrl was clicked when opeing
     */
    public editBenutzer(user: TSUser, event: any): void {
        if (user) {
            this.$state.go('benutzer', {benutzerId: user.username});
        }
    }
}
