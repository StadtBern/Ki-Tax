import {IDirective, IDirectiveFactory, IHttpService} from 'angular';
import * as moment from 'moment';
import Moment = moment.Moment;
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
