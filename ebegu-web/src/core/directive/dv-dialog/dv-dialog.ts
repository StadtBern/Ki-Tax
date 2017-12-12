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

import {IPromise} from 'angular';
import IDialogService = angular.material.IDialogService;
import IDialogOptions = angular.material.IDialogOptions;
import {RemoveDialogController} from '../../../gesuch/dialog/RemoveDialogController';
require('./dv-dialog.less');

export class DvDialog {

    static $inject: any[] = ['$mdDialog'];
    /* @ngInject */
    constructor(private $mdDialog: IDialogService) {
    }

    /**
     * Erstellt einen neuen confim Dialog mit den uebergegebenen Parametern
     * @param template Man kann ein belibiges Template eingeben in dem man das Layout des ganzen Dialogs gestaltet.
     * @param controller Hier implementiert man die verschiedenen Funktionen, die benoetigt sind
     * @param params Ein JS-Objekt {key-value}. Alle definierte Keys werden dann mit dem gegebenen Wert in Controller injected
     * @returns {angular.IPromise<any>}
     */
    public showDialog(template: string, controller?: any, params?: any): IPromise<any> {


        // form parameter is required for injection for RemoveDialogController, so set messing parameter here.
        if (controller.name === 'RemoveDialogController' && !params.form) {
            params.form = undefined;
        }

        let confirm: IDialogOptions = {
            template: template,
            controller: controller,
            controllerAs: 'vm',
            locals: params
        };

        return this.$mdDialog.show(confirm);
    }

    public showDialogFullscreen(template: string, controller?: any, params?: any): IPromise<any> {
        let confirm: IDialogOptions = {
            template: template,
            controller: controller,
            controllerAs: 'vm',
            fullscreen: true,
            locals: params
        };

        return this.$mdDialog.show(confirm);
    }

}
