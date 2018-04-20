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

import IDirective = angular.IDirective;
import IDirectiveLinkFn = angular.IDirectiveLinkFn;
import IScope = angular.IScope;
import IAttributes = angular.IAttributes;
import {DVsTPersistService} from '../../service/dVsTPersistService';
import {IAugmentedJQuery, IDirectiveFactory} from 'angular';

export default class DVSTResetSearch implements IDirective {
    static $inject: string[] = ['UserRS', 'InstitutionRS', 'DVsTPersistService'];

    restrict = 'A';
    require = '^stTable';
    link: IDirectiveLinkFn;

    /* @ngInject */
    constructor(private dVsTPersistService: DVsTPersistService) {
        this.link = (scope: IScope, element: IAugmentedJQuery, attrs: IAttributes, ctrl: any) => {
            let nameSpace: string = attrs.dvStPersistAntraege;
            return element.bind('click', function() {
                return scope.$apply(function() {
                    let tableState = ctrl.tableState();
                    tableState.search.predicateObject = {};
                    tableState.sort = {};
                    tableState.pagination.start = 0;
                    dVsTPersistService.deleteData(nameSpace);
                    return ctrl.pipe();
                });
            });
        };
    }

    static factory(): IDirectiveFactory {
        const directive = (dVsTPersistService: any) => new DVSTResetSearch(dVsTPersistService);
        directive.$inject = ['DVsTPersistService'];
        return directive;
    }
}

