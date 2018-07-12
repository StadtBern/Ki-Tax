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

import {IComponentOptions, IFormController, ILogService} from 'angular';
import {IStateService} from 'angular-ui-router';
import {Moment} from 'moment';
import {IBenutzerStateParams} from '../../../admin/admin.route';
import {ApplicationPropertyRS} from '../../../admin/service/applicationPropertyRS.rest';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {RemoveDialogController} from '../../../gesuch/dialog/RemoveDialogController';
import {getTSRoleValues, getTSRoleValuesWithoutSuperAdmin, rolePrefix, TSRole} from '../../../models/enums/TSRole';
import TSBerechtigung from '../../../models/TSBerechtigung';
import TSBerechtigungHistory from '../../../models/TSBerechtigungHistory';
import TSInstitution from '../../../models/TSInstitution';
import {TSTraegerschaft} from '../../../models/TSTraegerschaft';
import TSUser from '../../../models/TSUser';
import {TSDateRange} from '../../../models/types/TSDateRange';
import DateUtil from '../../../utils/DateUtil';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import {DvDialog} from '../../directive/dv-dialog/dv-dialog';
import {InstitutionRS} from '../../service/institutionRS.rest';
import {TraegerschaftRS} from '../../service/traegerschaftRS.rest';
import UserRS from '../../service/userRS.rest';
import ITranslateService = angular.translate.ITranslateService;

let removeDialogTemplate = require('../../../gesuch/dialog/removeDialogTemplate.html');
let template = require('./dv-benutzer.html');
require('./dv-benutzer.less');

export class DVBenutzerConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = DVBenutzerController;
    controllerAs = 'vm';
}

export class DVBenutzerController {

    form: IFormController;
    TSRoleUtil: any;

    institutionenList: TSInstitution[];
    traegerschaftenList: TSTraegerschaft[];
    tomorrow: Moment = DateUtil.today().add(1, 'days');

    selectedUser: TSUser;
    private _currentBerechtigung: TSBerechtigung;
    private _futureBerechtigungen: TSBerechtigung[];
    berechtigungHistoryList: TSBerechtigungHistory[];

    private _isDefaultVerantwortlicher: boolean = false;


    static $inject: any[] = ['$log', 'InstitutionRS', 'TraegerschaftRS', 'AuthServiceRS', '$translate', '$stateParams', 'UserRS', '$state',
        'DvDialog', 'ApplicationPropertyRS'];
    /* @ngInject */
    constructor(private $log: ILogService, private institutionRS: InstitutionRS, private traegerschaftenRS: TraegerschaftRS,
                private authServiceRS: AuthServiceRS, private $translate: ITranslateService,
                private $stateParams: IBenutzerStateParams, private userRS: UserRS, private $state: IStateService,
                private dvDialog: DvDialog, private applicationPropertyRS: ApplicationPropertyRS) {

        this.TSRoleUtil = TSRoleUtil;
    }

    $onInit() {
        this.updateInstitutionenList();
        this.updateTraegerschaftenList();
        let username: string = this.$stateParams.benutzerId;
        if (username) {
           this.userRS.findBenutzer(username).then((result) => {
               this.selectedUser = result;
               this.initSelectedUser();
               // Falls der Benutzer JA oder SCH Benutzer ist, muss geprüft werden, ob es sich um den "Default-Verantwortlichen" des
               // entsprechenden Amtes handelt
               if (TSRoleUtil.getAdministratorJugendamtRole().indexOf(this.currentBerechtigung.role) > -1) {
                   this.applicationPropertyRS.getByName('DEFAULT_VERANTWORTLICHER').then(defaultBenutzerJA => {
                       if (result.username.toLowerCase() === defaultBenutzerJA.value.toLowerCase()) {
                           this._isDefaultVerantwortlicher = true;
                       }
                   });
               }
               if (TSRoleUtil.getSchulamtRoles().indexOf(this.currentBerechtigung.role) > -1) {
                   this.applicationPropertyRS.getByName('DEFAULT_VERANTWORTLICHER_SCH').then(defaultBenutzerSCH => {
                       if (result.username.toLowerCase() === defaultBenutzerSCH.value.toLowerCase()) {
                           this._isDefaultVerantwortlicher = true;
                       }
                   });
               }
           });
        }
    }

    private initSelectedUser(): void {
        this._currentBerechtigung = this.selectedUser.berechtigungen[0];
        this._futureBerechtigungen = this.selectedUser.berechtigungen;
        this._futureBerechtigungen.splice(0, 1);
        this.userRS.getBerechtigungHistoriesForBenutzer(this.selectedUser.username).then((result) => {
            this.berechtigungHistoryList = result;
        });
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

    public getRollen(): Array<TSRole> {
        if (this.authServiceRS.isRole(TSRole.SUPER_ADMIN)) {
            return getTSRoleValues();
        }
        return getTSRoleValuesWithoutSuperAdmin();
    }

    public getTranslatedRole(role: TSRole): string {
        if (role === TSRole.GESUCHSTELLER) {
            return this.$translate.instant(rolePrefix() + 'NONE');
        }
        return this.$translate.instant(rolePrefix() + role);
    }

    public getGeaendertDurch(role: TSBerechtigungHistory): string {
        if (role.userErstellt === 'anonymous') {
            return 'system';
        }
        return role.userErstellt;
    }

    saveBenutzer(): void {
        if (this.form.$valid) {
            if (this.isMoreThanGesuchstellerRole()) {
                this.dvDialog.showRemoveDialog(removeDialogTemplate, this.form, RemoveDialogController, {
                    title: 'BENUTZER_ROLLENZUWEISUNG_CONFIRMATION_TITLE',
                    deleteText: 'BENUTZER_ROLLENZUWEISUNG_CONFIRMATION_TEXT',
                    parentController: undefined,
                    elementID: undefined
                }).then(() => {
                    if (this.isAdminRole()) {
                        this.dvDialog.showRemoveDialog(removeDialogTemplate, this.form, RemoveDialogController, {
                            title: 'BENUTZER_ROLLENZUWEISUNG_CONFIRMATION_ADMIN_TITLE',
                            deleteText: 'BENUTZER_ROLLENZUWEISUNG_CONFIRMATION_ADMIN_TEXT',
                            parentController: undefined,
                            elementID: undefined
                        }).then(() => {
                            this.doSaveBenutzer();
                        });
                    } else {
                        this.doSaveBenutzer();
                    }
                });
            } else {
                this.doSaveBenutzer();
            }
        }
    }

    private isAdminRole() {
        return this.isAtLeastOneRoleInList(TSRoleUtil.getAdministratorRoles());
    }

    private isMoreThanGesuchstellerRole() {
        return this.isAtLeastOneRoleInList(TSRoleUtil.getAllRolesButGesuchsteller());
    }

    private isAtLeastOneRoleInList(rolesToCheck: Array<TSRole>): boolean {
        // Es muessen alle vorhandenen Rollen geprueft werden
        if (rolesToCheck.indexOf(this.currentBerechtigung.role) > -1) {
            return true;
        }
        for (let berechtigung of this.futureBerechtigungen) {
            if (rolesToCheck.indexOf(berechtigung.role) > -1) {
                return true;
            }
        }
        return false;
    }

    private doSaveBenutzer(): void {
        // Die (separat behandelte) aktuelle Berechtigung wieder zur Liste hinzufügen
        this.selectedUser.berechtigungen = this._futureBerechtigungen;
        this.selectedUser.berechtigungen.unshift(this._currentBerechtigung);
        this.userRS.saveBenutzer(this.selectedUser).then((changedUser: TSUser) => {
            this.navigateBackToUsersList();
        }).catch(reason => {
            this.initSelectedUser();
        });
    }

    inactivateBenutzer(): void {
        if (this.form.$valid) {
            this.userRS.inactivateBenutzer(this.selectedUser).then((changedUser: TSUser) => {
                this.selectedUser = changedUser;
            });
        }
    }

    reactivateBenutzer(): void {
        if (this.form.$valid) {
            this.userRS.reactivateBenutzer(this.selectedUser).then((changedUser: TSUser) => {
                this.selectedUser = changedUser;
            });
        }
    }

    canAddBerechtigung(): boolean {
        return this.futureBerechtigungen && this.futureBerechtigungen.length < 1;
    }

    addBerechtigung() {
        let berechtigung: TSBerechtigung = new TSBerechtigung();
        berechtigung.role = TSRole.GESUCHSTELLER;
        berechtigung.gueltigkeit = new TSDateRange();
        berechtigung.gueltigkeit.gueltigAb = this.tomorrow;
        berechtigung.enabled = true;
        this.futureBerechtigungen.push(berechtigung);
    }

    enableBerechtigung(berechtigung: TSBerechtigung): void {
        berechtigung.enabled = true;
    }

    removeBerechtigung(berechtigung: TSBerechtigung): void {
        let index: number = this.futureBerechtigungen.indexOf(berechtigung, 0);
        this.futureBerechtigungen.splice(index, 1);
    }

    cancel(): void {
        this.navigateBackToUsersList();
    }

    private navigateBackToUsersList() {
        this.$state.go('benutzerlist');
    }

    public isInstitutionBerechtigung(berechtigung: TSBerechtigung): boolean {
        return berechtigung && berechtigung.role === TSRole.SACHBEARBEITER_INSTITUTION;
    }

    public isTraegerschaftBerechtigung(berechtigung: TSBerechtigung): boolean {
        return berechtigung && berechtigung.role === TSRole.SACHBEARBEITER_TRAEGERSCHAFT;
    }

    public isBerechtigungEnabled(berechtigung: TSBerechtigung): boolean {
        return berechtigung && berechtigung.enabled;
    }

    public get currentBerechtigung(): TSBerechtigung {
        return this._currentBerechtigung;
    }

    public get futureBerechtigungen(): TSBerechtigung[] {
        return this._futureBerechtigungen;
    }

    public get isDefaultVerantwortlicher(): boolean {
        return this._isDefaultVerantwortlicher;
    }

    public set isDefaultVerantwortlicher(value: boolean) {
        this._isDefaultVerantwortlicher = value;
    }
}
