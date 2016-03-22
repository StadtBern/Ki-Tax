/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.routes {
    'use strict';

    export interface IRouterHelper {
        hasOtherwise: boolean;
        stateProvider: angular.IServiceProvider;
        urlRouterProvider: angular.ui.IUrlRouterProvider;

        configureStates: (states: Array<angular.ui.IState>, otherwisePath?: string) => void;
        getStates: () => Array<angular.ui.IState>;
    }

    export class RouterHelper implements IRouterHelper {
        hasOtherwise: boolean;
        stateProvider: angular.ui.IStateProvider;
        urlRouterProvider: angular.ui.IUrlRouterProvider;

        static $inject = ['$stateProvider', '$urlRouterProvider'];
        /* @ngInject */
        constructor($stateProvider, $urlRouterProvider) {
            this.hasOtherwise = false;
            this.stateProvider = $stateProvider;
            this.urlRouterProvider = $urlRouterProvider;
        }

        /**
         * @param {Array} states
         * @param {string} [otherwisePath]
         */
        public configureStates(states, otherwisePath) {
            states.forEach((state) => {
                this.stateProvider.state(state);
            });
            if (otherwisePath && !this.hasOtherwise) {
                this.hasOtherwise = true;
                this.urlRouterProvider.otherwise(otherwisePath);
            }
        }

        public getStates() {
            return this.stateProvider.$get();
        }

    }

    export class RouterHelperProvider implements angular.IServiceProvider {

        private routerHelper: IRouterHelper;

        static $inject = ['$locationProvider', '$stateProvider', '$urlRouterProvider'];
        /* @ngInject */
        constructor($locationProvider: angular.ILocationProvider, $stateProvider, $urlRouterProvider) {
            $locationProvider.html5Mode(false);
            this.routerHelper = new RouterHelper($stateProvider, $urlRouterProvider);
        }

        $get(): IRouterHelper {
            return this.routerHelper;
        }

    }

    angular.module('dvbAngular.router').provider('routerHelper', RouterHelperProvider);

}
