/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.routes {
    'use strict';


    export class EbeguStateController {
        applicationProperties: any;

        static $inject = ['applicationProperties'];
        constructor(applicationProperties) {
            var vm = this;
            vm.applicationProperties = applicationProperties;
        }
    }

    export class EbeguState implements angular.ui.IState {
        name = 'admin';
        template = '<admin-view application-properties="vm.applicationProperties"></admin-view>';
        url = '/admin';
        controller = EbeguStateController;
        controllerAs = 'vm';
        resolve = {
            //applicationProperties();
            applicationProperties : function(applicationPropertyRS) {
                return applicationPropertyRS.getAllApplicationProperties()
                    .then(function (response) {
                        return response.data;
                    });
            }
        };

        constructor() {
        }

    }


    export class EbeguWebAdminRun {
        static $inject = ['routerHelper'];
        /* @ngInject */
        constructor(routerHelper: IRouterHelper) {
            routerHelper.configureStates(this.getStates());
        }

        /**
         * @returns {angular.ui.IState[]}
         */
        public getStates(): Array<angular.ui.IState> {
            return [new EbeguState()];
        }

        public static Factory(routerHelper) : EbeguWebAdminRun {
            return new EbeguWebAdminRun(routerHelper);
        }

    }


    angular.module('ebeguWeb.admin').run(EbeguWebAdminRun.Factory);
}
