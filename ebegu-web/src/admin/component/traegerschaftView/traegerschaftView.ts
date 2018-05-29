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
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import ErrorService from '../../../core/errors/service/ErrorService';
import {TraegerschaftRS} from '../../../core/service/traegerschaftRS.rest';
import {RemoveDialogController} from '../../../gesuch/dialog/RemoveDialogController';
import {TSTraegerschaft} from '../../../models/TSTraegerschaft';
import EbeguUtil from '../../../utils/EbeguUtil';
import AbstractAdminViewController from '../../abstractAdminView';
import './traegerschaftView.less';
import IFormController = angular.IFormController;

let template = require('./traegerschaftView.html');
let removeDialogTemplate = require('../../../gesuch/dialog/removeDialogTemplate.html');

export class TraegerschaftViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    bindings: any = {
        traegerschaften: '<'
    };
    template: string = template;
    controller: any = TraegerschaftViewController;
    controllerAs: string = 'vm';
}

export class TraegerschaftViewController extends AbstractAdminViewController {

    form: IFormController;
    traegerschaftRS: TraegerschaftRS;
    traegerschaften: TSTraegerschaft[];
    traegerschaft: TSTraegerschaft = undefined;

    static $inject = ['TraegerschaftRS', 'ErrorService', 'DvDialog', 'AuthServiceRS', 'EbeguUtil'];
    /* @ngInject */
    constructor(TraegerschaftRS: TraegerschaftRS, private errorService: ErrorService, private dvDialog: DvDialog,
                authServiceRS: AuthServiceRS, private ebeguUtil: EbeguUtil) {
        super(authServiceRS);
        this.traegerschaftRS = TraegerschaftRS;
    }

    getTraegerschaftenList(): TSTraegerschaft[] {
        return this.traegerschaften;
    }

    removeTraegerschaft(traegerschaft: any): void {
        this.dvDialog.showRemoveDialog(removeDialogTemplate, this.form, RemoveDialogController, {
            deleteText: '',
            title: 'LOESCHEN_DIALOG_TITLE',
            parentController: undefined,
            elementID: undefined
        })
            .then(() => {   //User confirmed removal
                this.traegerschaft = undefined;
                this.traegerschaftRS.removeTraegerschaft(traegerschaft.id).then((response) => {
                    let index = EbeguUtil.getIndexOfElementwithID(traegerschaft, this.traegerschaften);
                    if (index > -1) {
                        this.traegerschaften.splice(index, 1);
                    }
                });
            });
    }

    createTraegerschaft(): void {
        this.traegerschaft = new TSTraegerschaft();
        this.traegerschaft.active = true;
    }

    saveTraegerschaft(): void {
        if (this.form.$valid) {
            this.errorService.clearAll();
            let newTraegerschaft: boolean = this.traegerschaft.isNew();
            this.traegerschaftRS.createTraegerschaft(this.traegerschaft).then((traegerschaft: TSTraegerschaft) => {
                if (newTraegerschaft) {
                    this.traegerschaften.push(traegerschaft);
                } else {
                    let index = EbeguUtil.getIndexOfElementwithID(traegerschaft, this.traegerschaften);
                    if (index > -1) {
                        this.traegerschaften[index] = traegerschaft;
                        this.ebeguUtil.handleSmarttablesUpdateBug(this.traegerschaften);
                    }
                }
                this.traegerschaft = undefined;
            });
        }
    }

    cancelTraegerschaft(): void {
        this.traegerschaft = undefined;
    }

    setSelectedTraegerschaft(selected: TSTraegerschaft): void {
        this.traegerschaft = angular.copy(selected);
    }
}
