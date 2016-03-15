/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.routes {
    'use strict';

    export interface IRouterHelper {
        hasOtherwise: boolean;
        stateProvider: angular.IServiceProvider;
        urlRouterProvider: any; //todo imanol set types

        configureStates: (states: Array<angular.ui.IState>, otherwisePath: string) => void; //todo imanol set types
        getStates: () => any; //todo imanol set types
    }

    export class RouterHelper implements IRouterHelper {
        hasOtherwise: boolean;
        stateProvider: angular.ui.IStateProvider;
        urlRouterProvider: any; //todo imanol set types

        $inject = ['$stateProvider', '$urlRouterProvider'];
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
        configureStates(states: Array<angular.ui.IState>, otherwisePath: string) {
            var vm = this;
            states.forEach(function (state) {
                vm.stateProvider.state(state);
            });
            if (otherwisePath && !this.hasOtherwise) {
                this.hasOtherwise = true;
                this.urlRouterProvider.otherwise(otherwisePath);
            }
        }

        getStates() {
            return this.stateProvider.$get();
        }

    }

    export interface IRouterHelperProvider extends angular.IServiceProvider {
        $get(): IRouterHelper;
    }

    class RouterHelperProvider implements IRouterHelperProvider{

        private stateProvider: angular.ui.IStateProvider;
        private urlRouterProvider: any; //todo imanol set types

        static $inject = ['$locationProvider', '$stateProvider', '$urlRouterProvider'];
        /* @ngInject */
        constructor($locationProvider, $stateProvider, $urlRouterProvider) {
            this.stateProvider = $stateProvider;
            this.urlRouterProvider = $urlRouterProvider;
            $locationProvider.html5Mode(false);
        }

        $get(): IRouterHelper {
            return new RouterHelper(this.stateProvider, this.urlRouterProvider);
        }

    }



    angular.module('dvbAngular.router').provider('routerHelper', RouterHelperProvider);



    //routerHelperProvider.$inject = ['$locationProvider', '$stateProvider', '$urlRouterProvider'];
    ///* @ngInject */
    //function routerHelperProvider($locationProvider, $stateProvider, $urlRouterProvider) {
    //    /* jshint validthis:true */
    //    this.$get = RouterHelper;
    //
    //    $locationProvider.html5Mode(false);
    //
    //    RouterHelper.$inject = ['$state'];
    //    /* @ngInject */
    //    function RouterHelper($state) {
    //        var hasOtherwise = false;
    //
    //        var service = {
    //            configureStates: configureStates,
    //            getStates: getStates
    //        };
    //
    //        return service;
    //
    //        ///////////////
    //
    //        /**
    //         * @param {Array} states
    //         * @param {string} [otherwisePath]
    //         */
    //        function configureStates(states, otherwisePath) {
    //            states.forEach(function (state) {
    //                $stateProvider.state(state.state, state.config);
    //            });
    //            if (otherwisePath && !hasOtherwise) {
    //                hasOtherwise = true;
    //                $urlRouterProvider.otherwise(otherwisePath);
    //            }
    //        }
    //
    //        function getStates() {
    //            return $state.get();
    //        }
    //    }
    //}

}
