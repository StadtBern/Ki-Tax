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

import {IDirective, IDirectiveFactory} from 'angular';
import TSUser from '../../../models/TSUser';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import UserRS from '../../service/userRS.rest';
import * as moment from 'moment';
let template = require('./dv-userselect.html');


export class DVUserselect implements IDirective {
    restrict = 'E';
    require: any = {smartTable: '?^stTable'};
    scope = {
        ngModel: '=',
        inputId: '@',
        dvUsersearch: '@',
        ngDisabled: '<',
        initialAll: '=',
        showSelectionAll: '=',
        onUserChanged: '&',
        selectedUser: '=?'
        //initialAll -> tritt nur ein, wenn explizit  { initial-all="true" } geschrieben ist
    };
    controller = UserselectController;
    controllerAs = 'vm';
    bindToController = true;
    template = template;

    static factory(): IDirectiveFactory {
        const directive = () => new DVUserselect();
        directive.$inject = [];
        return directive;
    }
}
/**
 * Direktive  der initial die smart table nach dem aktuell eingeloggtem user filtert
 */
export class UserselectController {
    selectedUser: TSUser;
    smartTable: any;
    userList: Array<TSUser>;
    dvUsersearch: string;
    initialAll: boolean;
    showSelectionAll: boolean;
    valueChanged: () => void;           // Methode, die beim Klick auf die Combobox aufgerufen wird
    onUserChanged: (user: any) => void; // Callback, welche aus obiger Methode aufgerufen werden soll

    static $inject: string[] = ['UserRS', 'AuthServiceRS'];
    /* @ngInject */
    constructor(private userRS: UserRS, private authService: AuthServiceRS) {

    }

    //wird von angular aufgerufen
    $onInit() {
        this.updateUserList();
        if (!this.initialAll) { //tritt nur ein, wenn explizit  { initial-all="true" } geschrieben ist
            this.selectedUser = this.authService.getPrincipal();
        }
        //initial nach aktuell eingeloggtem filtern
        if (this.smartTable && !this.initialAll && this.selectedUser) {
            this.smartTable.search(this.selectedUser.getFullName(), this.dvUsersearch);
        }
        this.valueChanged = () => {
            this.onUserChanged({user: this.selectedUser});
        };
    }

    private updateUserList(): void {
        this.userRS.getBenutzerJAorAdmin().then((response: any) => {
            this.userList = angular.copy(response);
        });
    }
}
