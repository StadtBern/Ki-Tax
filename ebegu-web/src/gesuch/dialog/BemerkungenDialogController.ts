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
import ITranslateService = angular.translate.ITranslateService;
require('./dialogs.less');

export class BemerkungenDialogController {

    title: string;
    bemerkungen: string;

    static $inject = ['$mdDialog', '$translate', 'title', 'bemerkungen'];

    constructor(private $mdDialog: IDialogService, $translate: ITranslateService, title: string, bemerkungen: string) {
        this.title = $translate.instant(title);
        this.bemerkungen = bemerkungen;
    }

    public hide(): IPromise<any> {
        return this.$mdDialog.hide(this.bemerkungen);
    }

    public cancel(): void {
        this.$mdDialog.cancel();
    }
}
