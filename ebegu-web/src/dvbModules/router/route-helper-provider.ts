import {IState, IUrlRouterProvider, IStateProvider} from 'angular-ui-router';
import {IServiceProvider, ILocationProvider} from 'angular';

export class RouterHelper {
    static $inject = ['$stateProvider', '$urlRouterProvider'];

    hasOtherwise: boolean;
    stateProvider: IStateProvider;
    urlRouterProvider: IUrlRouterProvider;

    /* @ngInject */
    constructor($stateProvider: IStateProvider, $urlRouterProvider: IUrlRouterProvider) {
        this.hasOtherwise = false;
        this.stateProvider = $stateProvider;
        this.urlRouterProvider = $urlRouterProvider;
    }

    public configureStates(states: IState[], otherwisePath?: string): void {
        states.forEach((state) => {
            this.stateProvider.state(state);
        });
        if (otherwisePath && !this.hasOtherwise) {
            this.hasOtherwise = true;
            this.urlRouterProvider.otherwise(otherwisePath);
        }
    }

    public getStates(): IState[] {
        return this.stateProvider.$get();
    }
}

export default class RouterHelperProvider implements IServiceProvider {
    static $inject = ['$locationProvider', '$stateProvider', '$urlRouterProvider'];

    private routerHelper: RouterHelper;

    /* @ngInject */
    constructor($locationProvider: ILocationProvider, $stateProvider: IStateProvider, $urlRouterProvider: IUrlRouterProvider) {
        $locationProvider.html5Mode(false);
        this.routerHelper = new RouterHelper($stateProvider, $urlRouterProvider);
    }

    $get(): RouterHelper {
        return this.routerHelper;
    }
}

angular.module('dvbAngular.router').provider('routerHelper', RouterHelperProvider);
