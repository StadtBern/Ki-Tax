import {IDirective, IDirectiveFactory, IHttpService} from 'angular';
import Moment = moment.Moment;
import ITimeoutService = angular.ITimeoutService;

export class DVLoading implements IDirective {
    restrict = 'A';
    controller = DVLoadingController;
    controllerAs = 'vm';

    link = (scope: ng.IScope, element: ng.IAugmentedJQuery, attributes: ng.IAttributes, controller: DVLoadingController) => {
        scope.$watch(controller.isLoading, function (v) {
            if (v) {
                element.show();
            } else {
                controller.$timeout(function () {
                    element.hide()
                }, 500);

            }
        });
    };

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
