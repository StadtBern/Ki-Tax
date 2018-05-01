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
import * as moment from 'moment';
import {Moment} from 'moment';
import {IBenutzerStateParams} from '../../../admin/admin.route';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {RemoveDialogController} from '../../../gesuch/dialog/RemoveDialogController';
import {getTSRoleValues, getTSRoleValuesWithoutSuperAdmin, rolePrefix, TSRole} from '../../../models/enums/TSRole';
import TSBerechtigung from '../../../models/TSBerechtigung';
import TSInstitution from '../../../models/TSInstitution';
import {TSTraegerschaft} from '../../../models/TSTraegerschaft';
import TSUser from '../../../models/TSUser';
import {TSDateRange} from '../../../models/types/TSDateRange';
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
    today: Moment = moment(moment.now()).add(1, 'days');

    selectedUser: TSUser;
    berechtigungenOfBenutzer: TSBerechtigung[];


    static $inject: any[] = ['$log', 'InstitutionRS', 'TraegerschaftRS', 'AuthServiceRS', '$translate', '$stateParams', 'UserRS', '$state', 'DvDialog'];
    /* @ngInject */
    constructor(private $log: ILogService, private institutionRS: InstitutionRS, private traegerschaftenRS: TraegerschaftRS,
                private authServiceRS: AuthServiceRS, private $translate: ITranslateService,
                private $stateParams: IBenutzerStateParams, private userRS: UserRS, private $state: IStateService, private dvDialog: DvDialog) {

        this.TSRoleUtil = TSRoleUtil;
    }

    $onInit() {
        this.updateInstitutionenList();
        this.updateTraegerschaftenList();
        let username: string = this.$stateParams.benutzerId;
        if (username) {
           this.userRS.findBenutzer(username).then((result) => {
               this.selectedUser = result;
               this.userRS.getBerechtigungenForBenutzer(result).then((result) => {
                   this.berechtigungenOfBenutzer = result;
                   // Wir schicken nur die aktuelle und eine eventuelle zukünftige Berechtigung, sortiert mit Datum aufsteigend, d.h. die erste ist die aktuelle
                   // Diese muss entfernt werden, da sie schon als aktuelleBerechtigung aufgefuehrt wird
                   this.berechtigungenOfBenutzer.splice(0, 1);
               });
           });
        }
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

    public showInstitutionenList(): boolean {
        if (this.selectedUser) {
            return this.selectedUser.currentBerechtigung.role === TSRole.SACHBEARBEITER_INSTITUTION;
        }
        return false;
    }

    public showTraegerschaftenList(): boolean {
        if (this.selectedUser) {
            return this.selectedUser.currentBerechtigung.role === TSRole.SACHBEARBEITER_TRAEGERSCHAFT;
        }
        return false;
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
        if (rolesToCheck.indexOf(this.selectedUser.currentBerechtigung.role) > -1) {
            return true;
        }
        for (let berechtigung of this.berechtigungenOfBenutzer) {
            if (rolesToCheck.indexOf(berechtigung.role) > -1) {
                return true;
            }
        }
        return false;
    }

    private doSaveBenutzer(): void {
        // Die (separat behandelte) aktuelle Berechtigung wieder zur Liste hinzufügen
        this.berechtigungenOfBenutzer.unshift(this.selectedUser.currentBerechtigung);
        this.clearBenutzerObject(this.selectedUser);
        this.userRS.saveBerechtigungen(this.selectedUser, this.berechtigungenOfBenutzer);
        this.navigateBackToUsersList();
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
        return this.berechtigungenOfBenutzer && this.berechtigungenOfBenutzer.length < 1;
    }

    canBeenden(): boolean {
       return this.selectedUser.currentBerechtigung.role !== TSRole.GESUCHSTELLER;
    }

    addBerechtigung() {
        let berechtigung: TSBerechtigung = new TSBerechtigung();
        berechtigung.role = TSRole.GESUCHSTELLER;
        berechtigung.gueltigkeit = new TSDateRange();
        berechtigung.gueltigkeit.gueltigAb = this.today;
        berechtigung.enabled = true;
        this.berechtigungenOfBenutzer.push(berechtigung);
    }

    enableBerechtigung(berechtigung: TSBerechtigung): void {
        berechtigung.enabled = true;
    }

    removeBerechtigung(berechtigung: TSBerechtigung): void {
        let index: number = this.berechtigungenOfBenutzer.indexOf(berechtigung, 0);
        this.berechtigungenOfBenutzer.splice(index, 1);
    }

    cancel(): void {
        this.navigateBackToUsersList();
    }

    private navigateBackToUsersList() {
        this.$state.go('benutzerlist');
    }

    private clearBenutzerObject(benutzer: TSUser): void {
        // Es darf nur eine Institution gesetzt sein, wenn die Rolle INSTITUTION ist
        if (benutzer.currentBerechtigung.role !== TSRole.SACHBEARBEITER_INSTITUTION) {
            benutzer.currentBerechtigung.institution = null;
        }
        // Es darf nur eine Trägerschaft gesetzt sein, wenn die Rolle TRAEGERSCHAFT ist
        if (benutzer.currentBerechtigung.role !== TSRole.SACHBEARBEITER_TRAEGERSCHAFT) {
            benutzer.currentBerechtigung.traegerschaft = null;
        }
        // Das Datum gueltigBis sollte bei Rolle GESUCHSTELLER nicht gesetzt werden
        if (benutzer.currentBerechtigung.role === TSRole.GESUCHSTELLER) {
            benutzer.currentBerechtigung.gueltigkeit.gueltigBis = null;
        }
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
}
