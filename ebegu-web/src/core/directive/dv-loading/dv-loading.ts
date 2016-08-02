import {IDirective, IDirectiveFactory} from 'angular';
import Moment = moment.Moment;
import INgModelController = angular.INgModelController;
import IHttpService = angular.IHttpService;


interface IDVLoadingController {
    isLoading: () => {};
}

export class DVLoading implements IDirective {
    restrict = 'A';
    controller = DVLoadingController;
    controllerAs = 'vm';

    link = (scope: ng.IScope, element: ng.IAugmentedJQuery, attributes: ng.IAttributes, controller: IDVLoadingController) => {
        scope.$watch(controller.isLoading, function (v) {
            if (v) {
                element.show();
            } else {
                element.hide();
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
export class DVLoadingController implements IDVLoadingController {

    static $inject: string[] = ['$http'];

    isLoading: () => {};

    /* @ngInject */
    constructor(private $http: IHttpService) {
        this.isLoading = (): boolean => {
            return this.$http.pendingRequests.length > 0;
        };
    }


}
