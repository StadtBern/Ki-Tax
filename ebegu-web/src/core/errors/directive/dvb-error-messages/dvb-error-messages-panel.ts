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
import IScope = angular.IScope;
import {TSMessageEvent} from '../../../../models/enums/TSErrorEvent';
import ErrorService from '../../service/ErrorService';
import TSExceptionReport from '../../../../models/TSExceptionReport';
import {TSErrorLevel} from '../../../../models/enums/TSErrorLevel';
import {TSRoleUtil} from '../../../../utils/TSRoleUtil';
import {TSErrorAction} from '../../../../models/enums/TSErrorAction';
import {DvDialog} from '../../../directive/dv-dialog/dv-dialog';
import {RemoveDialogController} from '../../../../gesuch/dialog/RemoveDialogController';
import GesuchRS from '../../../../gesuch/service/gesuchRS.rest';

let templ = require('./dvb-error-messages-panel.html');
let style = require('./dvb-error-messages-panel.less');
let removeDialogTemplate = require('../../../../gesuch/dialog/removeDialogTemplate.html');

export class DvErrorMessagesPanelComponentConfig implements IComponentOptions {

    scope = {};
    template = templ;
    controller = DvErrorMessagesPanelComponent;
    controllerAs = 'vm';
}

/**
 * component that can display error messages
 */
export class DvErrorMessagesPanelComponent {

    errors: Array<TSExceptionReport> = [];
    TSRoleUtil: any;


    static $inject: string[] = ['$scope', 'ErrorService', 'DvDialog', 'GesuchRS'];
    /* @ngInject */
    constructor(private $scope: IScope, private errorService: ErrorService, private dvDialog: DvDialog,
                private gesuchRS: GesuchRS) {
    }

    $onInit() {
        this.TSRoleUtil = TSRoleUtil;
        this.$scope.$on(TSMessageEvent[TSMessageEvent.ERROR_UPDATE], this.displayMessages);
        this.$scope.$on(TSMessageEvent[TSMessageEvent.INFO_UPDATE], this.displayMessages);
        this.$scope.$on(TSMessageEvent[TSMessageEvent.CLEAR], () => {
            this.errors = [];
        });
    }

    displayMessages = (event: any, errors: Array<TSExceptionReport>) => {
        this.errors = errors;
        this.show();
    }

    private executeAction(error: TSExceptionReport): void {
        if (error.action) {
            if (error.action === TSErrorAction.REMOVE_ONLINE_MUTATION && error.argumentList.length > 0) {
                this.removeOnlineMutation(error.objectId, error.argumentList[0]);

            } else if (error.action === TSErrorAction.REMOVE_ONLINE_ERNEUERUNGSGESUCH && error.argumentList.length > 0) {
                this.removeOnlineErneuerungsgesuch(error.objectId, error.argumentList[0]);
            }
        }
        this.clear();
    }

    private removeOnlineMutation(objectId: string, gesuchsperiodeId: string): void {
        this.dvDialog.showRemoveDialog(removeDialogTemplate, RemoveDialogController, {
            title: 'REMOVE_ONLINE_MUTATION_CONFIRMATION',
            deleteText: 'REMOVE_ONLINE_MUTATION_BESCHREIBUNG',
            parentController: undefined,
            elementID: undefined,
            form: undefined
        }).then(() => {   //User confirmed removal
            this.gesuchRS.removeOnlineMutation(objectId, gesuchsperiodeId).then((response) => {});
        });
    }

    private removeOnlineErneuerungsgesuch(objectId: string, gesuchsperiodeId: string): void {
        this.dvDialog.showRemoveDialog(removeDialogTemplate, RemoveDialogController, {
            title: 'REMOVE_ONLINE_ERNEUERUNGSGESUCH_CONFIRMATION',
            deleteText: 'REMOVE_ONLINE_ERNEUERUNGSGESUCH_BESCHREIBUNG',
            parentController: undefined,
            elementID: undefined,
            form: undefined
        }).then(() => {   //User confirmed removal
            this.gesuchRS.removeOnlineFolgegesuch(objectId, gesuchsperiodeId).then((response) => {});
        });
    }

    private isActionDefined(error: TSExceptionReport): boolean {
        return error.action !== undefined && error.action !== null;
    }

    show() {
        // this.element.show();
        angular.element('dvb-error-messages-panel').show();     //besser als $element injection fuer tests
    }

    clear() {
        this.errorService.clearAll();
    }

    messageStyle(): string {
        for (let error of this.errors) {
            if (error.severity !== TSErrorLevel.INFO) {
                return '';
            }
        }
        return 'info';
    }

}


