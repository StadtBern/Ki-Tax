module ebeguWeb.routes {
    import IApplicationPropertyRS = ebeguWeb.services.IApplicationPropertyRS;
    import ApplicationProperty = ebeguWeb.API.TSApplicationProperty;
    'use strict';


    export class EbeguStateController {
        applicationProperties: Array<ApplicationProperty>;

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
            applicationProperties : function(applicationPropertyRS) {
                return applicationPropertyRS.getAllApplicationProperties();
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

        public static instance(routerHelper) : EbeguWebAdminRun {
            return new EbeguWebAdminRun(routerHelper);
        }

    }


    angular.module('ebeguWeb.admin').run(EbeguWebAdminRun.instance);
}
