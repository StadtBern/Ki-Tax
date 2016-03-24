/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.routes {
    import IApplicationPropertyRS = ebeguWeb.services.IApplicationPropertyRS;
    import ApplicationProperty = ebeguWeb.API.TSApplicationProperty;
    'use strict';

/*
    export class EbeguGesuchStateController {
        applicationProperties: Array<ApplicationProperty>;

        static $inject = ['applicationProperties'];
        constructor(applicationProperties) {
            var vm = this;
            // vm.applicationProperties = applicationProperties;
        }
    }*/

    export class EbeguGesuchState implements angular.ui.IState {
        name = 'gesuch';
        template = '<familiensituation-view>';
        //template = '<stammdaten-view>';
        url = '/gesuch';
        // controller = EbeguGesuchStateController;
        // controllerAs = 'vm';
      /*  resolve = {
            applicationProperties : function(applicationPropertyRS) {
                return applicationPropertyRS.getAllApplicationProperties();
            }
        };*/

        constructor() {
        }

    }

    export class EbeguWebGesuchRun {
        static $inject = ['routerHelper'];
        /* @ngInject */
        constructor(routerHelper: IRouterHelper) {
            routerHelper.configureStates(this.getStates());
        }

        /**
         * @returns {angular.ui.IState[]}
         */
        public getStates(): Array<angular.ui.IState> {
            return [new EbeguGesuchState()];
        }

        public static instance(routerHelper) : EbeguWebGesuchRun {
            return new EbeguWebGesuchRun(routerHelper);
        }

    }


    angular.module('ebeguWeb.gesuch').run(EbeguWebGesuchRun.instance);
}
