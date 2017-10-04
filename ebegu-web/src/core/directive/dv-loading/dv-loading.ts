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

import {IDirective, IDirectiveFactory, IHttpService} from 'angular';
import ITimeoutService = angular.ITimeoutService;
import IPromise = angular.IPromise;

export class DVLoading implements IDirective {
    restrict = 'A';
    controller = DVLoadingController;
    controllerAs = 'vm';

    link = (scope: ng.IScope, element: ng.IAugmentedJQuery, attributes: ng.IAttributes, controller: DVLoadingController) => {
        let promise: IPromise<any>;
        scope.$watch(controller.isLoading, (v) => {

            if (v) {
                controller.$timeout.cancel(promise);
                element.show();
            } else {
                promise = controller.$timeout(() => {
                    element.hide();
                }, 500);

            }
        });
    }

    static factory(): IDirectiveFactory {
        const directive = () => new DVLoading();
        directive.$inject = [];
        return directive;
    }
}

/**
 * Direktive  die ein Element ein oder ausblendet jenachdem ob ein http request pending ist
 */
export class DVLoadingController {

    static $inject: string[] = ['$http', '$timeout'];

    isLoading: () => {};

    /* @ngInject */
    constructor(private $http: IHttpService, public $timeout: ITimeoutService) {
        this.isLoading = (): boolean => {
            return this.$http.pendingRequests.length > 0;
        };
    }


}
