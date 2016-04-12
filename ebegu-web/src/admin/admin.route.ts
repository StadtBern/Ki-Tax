import TSApplicationProperty from "../models/TSApplicationProperty";
import {IState} from "angular-ui-router";
import {RouterHelper} from "../dvbModules/router/route-helper-provider";
import ApplicationPropertyRS from "./service/applicationPropertyRS.rest";

adminRun.$inject = ['RouterHelper'];

/* @ngInject */
export function adminRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates());
}

function getStates(): IState[] {
    return [
        {
            name: 'admin',
            template: '<dv-admin-view application-properties="vm.applicationProperties"></dv-admin-view>',
            url: '/admin',
            controller: EbeguStateController,
            controllerAs: 'vm',
            resolve: {
                applicationProperties: getApplicationProperties
            }
        }
    ];
}

class EbeguStateController {
    static $inject = ['applicationProperties'];

    applicationProperties: TSApplicationProperty[];

    constructor(applicationProperties: TSApplicationProperty[]) {
        var vm = this;
        vm.applicationProperties = applicationProperties;
    }
}

// FIXME dieses $inject wird ignoriert, d.h, der Parameter der Funktion muss exact dem Namen des Services entsprechen (Grossbuchstaben am Anfang). Warum?
getApplicationProperties.$inject = ['ApplicationPropertyRS'];
/* @ngInject */
function getApplicationProperties(ApplicationPropertyRS: ApplicationPropertyRS) {
    return ApplicationPropertyRS.getAllApplicationProperties();
}
