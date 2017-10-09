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
import {FreigabeViewController} from '../component/freigabeView/freigabeView';
import IDialogService = angular.material.IDialogService;
import ITranslateService = angular.translate.ITranslateService;

export class FreigabeDialogController {

    static $inject = ['$mdDialog', '$translate', 'parentController'];

    deleteText: string;
    title: string;

    constructor(private $mdDialog: IDialogService, $translate: ITranslateService, private parentController: FreigabeViewController) {
        this.title = $translate.instant('CONFIRM_GESUCH_FREIGEBEN');
        this.deleteText = $translate.instant('CONFIRM_GESUCH_FREIGEBEN_DESCRIPTION');
    }

    public hide(): IPromise<any> {
        this.parentController.confirmationCallback();
        return this.$mdDialog.hide();
    }

    public cancel(): void {
        this.$mdDialog.cancel();
    }
}
