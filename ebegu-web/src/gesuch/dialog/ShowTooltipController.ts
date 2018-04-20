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
import {IDVFocusableController} from '../../core/component/IDVFocusableController';
import IDialogService = angular.material.IDialogService;
import ITranslateService = angular.translate.ITranslateService;

export class ShowTooltipController {

    static $inject = ['$mdDialog', '$translate', 'title', 'text', 'parentController'];

    title: string;
    text: string;

    constructor(private $mdDialog: IDialogService, $translate: ITranslateService, title: string, text: string,
                private parentController: IDVFocusableController) {
        if (text !== undefined && text !== null) {
            this.text = $translate.instant(text);
        } else {
            this.text = 'test';
        }
    }

    public hide(): IPromise<any> {
        return this.$mdDialog.hide();
    }

    public cancel(): void {
        if (this.parentController) {
            this.parentController.setFocusBack(undefined); // no need to pass the element
        }
        this.$mdDialog.cancel();
    }
}
