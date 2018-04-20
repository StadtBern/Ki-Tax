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

import {IAttributes, IAugmentedJQuery, IDirective, IDirectiveFactory, IDirectiveLinkFn, IScope} from 'angular';
import {TSSubmitEvent} from '../../events/TSSubmitEvent';

/**
 * this directive can be added to a form to boradcast a form submit event
 */
export default class DVSubmitevent implements IDirective {
    static $inject: string[] = [];

    restrict = 'A';
    require = 'form';
    link: IDirectiveLinkFn;

    /* @ngInject */
    constructor() {
        this.link = (scope: IScope, element: IAugmentedJQuery, attrs: IAttributes, ctrl: any) => {
            element.on('submit', function () {
                scope.$broadcast(TSSubmitEvent[TSSubmitEvent.FORM_SUBMIT]);
            });
        };
    }

    static factory(): IDirectiveFactory {
        const directive = () => new DVSubmitevent();
        return directive;
    }
}

