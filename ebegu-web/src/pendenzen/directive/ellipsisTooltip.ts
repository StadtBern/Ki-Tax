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

import {IDirective, IDirectiveFactory, IDirectiveLinkFn, IScope, IAugmentedJQuery} from 'angular';

export default class EllipsisTooltip implements IDirective {

    link: IDirectiveLinkFn;

    constructor() {
        this.link = (scope: IScope, element: IAugmentedJQuery, attrs: any) => {
            // Die Direktive wird vor dem ng-bind ausgefuehrt, deswegen muessen wir ein watch einfuegen, sodass der Code erst
            // ausgefuehrt wird, wenn das ng-bind vom Element fertig ist und der Text deshalb stimmt.
            scope.$watch(attrs.ngBind, (interpolatedValue: any) => {
                if (element[0].offsetWidth < element[0].scrollWidth) {
                    //hiere wird vergliechen ob der Text groesser als der offsetWidth ist, in dem Fall wissen wir dass ellipsis hinzugefuegt wurden und fuegen title hinzu
                    element[0].title = interpolatedValue;
                }
            });
        };
    }

    static factory(): IDirectiveFactory {
        const directive = () => new EllipsisTooltip();
        return directive;
    }
}
