/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.routes {
    import IApplicationPropertyRS = ebeguWeb.services.IApplicationPropertyRS;
    import ApplicationProperty = ebeguWeb.API.TSApplicationProperty;
    'use strict';

/*
    export class EbeguStammdatenStateController {
        applicationProperties: Array<ApplicationProperty>;

        static $inject = ['applicationProperties'];
        constructor(applicationProperties) {
            var vm = this;
            // vm.applicationProperties = applicationProperties;
        }
    }*/

    export class EbeguStammdatenState implements angular.ui.IState {
        name = 'stammdaten';
        template = '<stammdaten-view>';
        url = '/stammdaten';
        // controller = EbeguStammdatenStateController;
        // controllerAs = 'vm';
      /*  resolve = {
            applicationProperties : function(applicationPropertyRS) {
                return applicationPropertyRS.getAllApplicationProperties();
            }
        };*/

        constructor() {
        }

    }

    export class EbeguWebStammdatenRun {
        static $inject = ['routerHelper'];
        /* @ngInject */
        constructor(routerHelper: IRouterHelper) {
            routerHelper.configureStates(this.getStates());
        }

        /**
         * @returns {angular.ui.IState[]}
         */
        public getStates(): Array<angular.ui.IState> {
            return [new EbeguStammdatenState()];
        }

        public static instance(routerHelper) : EbeguWebStammdatenRun {
            return new EbeguWebStammdatenRun(routerHelper);
        }

    }


    angular.module('ebeguWeb.stammdaten').run(EbeguWebStammdatenRun.instance);
}
