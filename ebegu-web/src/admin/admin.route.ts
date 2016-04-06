import TSApplicationProperty from '../models/TSApplicationProperty';
import {IState} from 'angular-ui-router';
import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import ApplicationPropertyRS from './service/applicationPropertyRS.rest';

class EbeguStateController {
    static $inject = ['applicationProperties'];

    applicationProperties: TSApplicationProperty[];

    constructor(applicationProperties: TSApplicationProperty[]) {
        var vm = this;
        vm.applicationProperties = applicationProperties;
    }
}

class EbeguWebAdminRun {
    static $inject = ['routerHelper'];
    /* @ngInject */
    constructor(routerHelper: RouterHelper) {
        routerHelper.configureStates(this.getStates());
    }

    public static instance(routerHelper: RouterHelper): EbeguWebAdminRun {
        return new EbeguWebAdminRun(routerHelper);
    }

    public getStates(): IState[] {
        return [
            {
                name: 'admin',
                template: '<admin-view application-properties="vm.applicationProperties"></admin-view>',
                url: '/admin',
                controller: EbeguStateController,
                controllerAs: 'vm',
                resolve: {
                    applicationProperties: function (applicationPropertyRS: ApplicationPropertyRS) {
                        return applicationPropertyRS.getAllApplicationProperties();
                    }
                }
            }
        ];
    }

}

angular.module('ebeguWeb.admin').run(EbeguWebAdminRun.instance);
