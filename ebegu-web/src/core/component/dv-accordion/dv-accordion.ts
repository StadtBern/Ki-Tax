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

let template = require('./dv-accordion.html');
require('./dv-accordion.less');

export class DvAccordionComponentConfig implements IComponentOptions {
    transclude = true;
    template = template;
    controller = DvAccordionController;
    controllerAs = 'vma';
    bindings: any = {
        allowMultipleSections: '<',
        selectedTabId: '<'
    };
}

export class DvAccordionController {
    accordion: string[] = [];
    allowMultipleSections: boolean;
    selectedTabId: string;
    static $inject: any[] = [];

    /* @ngInject */
    constructor() {
    }

    $onChanges() {
        // erlaubt dass man von Anfang an, ein Tab oeffnet, wenn man eine bestimmte Mitteilung oeffnen will
        if (this.selectedTabId) {
            this.toggleTab(this.selectedTabId);
        }
    }

    public toggleTab(i: string): void {
        if (this.isTagOpen(i)) {
            this.accordion.splice(this.accordion.indexOf(i), 1);
        } else {
            if (!this.allowMultipleSections) {
                this.accordion = [];
            }
            this.accordion.push(i);
        }
    }

    public isTagOpen(i: string ): boolean {
        return this.accordion.indexOf(i) > -1;
    }
}
