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
import {DvDialog} from '../../directive/dv-dialog/dv-dialog';
import {ShowTooltipController} from '../../../gesuch/dialog/ShowTooltipController';
import ITranslateService = angular.translate.ITranslateService;
import {IDVFocusableController} from '../IDVFocusableController';
let template = require('./dv-tooltip.html');
require('./dv-tooltip.less');
let showTooltipTemplate = require('../../../gesuch/dialog/showTooltipTemplate.html');

export class DvTooltipComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = DvTooltipController;
    controllerAs = 'vm';
    bindings: any = {
        text: '<',
        inputId: '@'
    };
}

export class DvTooltipController implements IDVFocusableController {

    private inputId: string;

    static $inject: any[] = ['$translate', 'DvDialog'];
    /* @ngInject */
    constructor(private $translate: ITranslateService, private DvDialog: DvDialog) {
    }

    showTooltip(info: any): void {
        this.DvDialog.showDialogFullscreen(showTooltipTemplate, ShowTooltipController, {
            title: '',
            text: info,
            parentController: this
        });
    }

    /**
     * Sets the focus back to the tooltip icon.
     */
    public setFocusBack(elementID: string): void {
        angular.element('#' + this.inputId + '.fa.fa-info-circle').first().focus();
    }
}

