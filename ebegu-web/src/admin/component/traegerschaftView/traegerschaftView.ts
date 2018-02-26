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
import './traegerschaftView.less';
import {TSTraegerschaft} from '../../../models/TSTraegerschaft';
import ErrorService from '../../../core/errors/service/ErrorService';
import {TraegerschaftRS} from '../../../core/service/traegerschaftRS.rest';
import {OkDialogController} from '../../../gesuch/dialog/OkDialogController';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {OkHtmlDialogController} from '../../../gesuch/dialog/OkHtmlDialogController';
import {RemoveDialogController} from '../../../gesuch/dialog/RemoveDialogController';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import AbstractAdminViewController from '../../abstractAdminView';
import EbeguUtil from '../../../utils/EbeguUtil';
import IFormController = angular.IFormController;

let template = require('./traegerschaftView.html');
let style = require('./traegerschaftView.less');
let okDialogTempl = require('../../../gesuch/dialog/okDialogTemplate.html');
let okHtmlDialogTempl = require('../../../gesuch/dialog/okHtmlDialogTemplate.html');
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
            elementID: undefined,
            form: this.form
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
                if (!traegerschaft.synchronizedWithOpenIdm) {
                    this.dvDialog.showDialog(okDialogTempl, OkDialogController, {
                        title: 'TRAEGERSCHAFT_CREATE_SYNCHRONIZE'
                    });
                }
            });
        }
    }

    cancelTraegerschaft(): void {
        this.traegerschaft = undefined;
    }

    setSelectedTraegerschaft(selected: TSTraegerschaft): void {
        this.traegerschaft = angular.copy(selected);
    }

    private syncWithOpenIdm(): void {
        this.traegerschaftRS.synchronizeTraegerschaften().then((respone) => {
            let returnString = respone.data.replace(/(?:\r\n|\r|\n)/g, '<br />');
            return this.dvDialog.showDialog(okHtmlDialogTempl, OkHtmlDialogController, {
                title: returnString
            });
        });
    }
}
