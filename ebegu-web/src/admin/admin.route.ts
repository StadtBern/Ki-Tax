import {IState} from 'angular-ui-router';
import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {ApplicationPropertyRS} from './service/applicationPropertyRS.rest';

adminRun.$inject = ['RouterHelper'];

/* @ngInject */
export function adminRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates());
}

function getStates(): IState[] {
    return [
        {
            name: 'admin',
            template: '<dv-admin-view application-properties="$resolve.applicationProperties"></dv-admin-view>',
            url: '/admin',
            resolve: {
                applicationProperties: getApplicationProperties
            }
        },
        {
            name: 'parameter',
            template: '<dv-parameter-view ebeguParameter="vm.ebeguParameter"></dv-parameter-view>',
            url: '/parameter',
        }
    ];
}

// FIXME dieses $inject wird ignoriert, d.h, der Parameter der Funktion muss exact dem Namen des Services entsprechen (Grossbuchstaben am Anfang). Warum?
getApplicationProperties.$inject = ['ApplicationPropertyRS'];
/* @ngInject */
function getApplicationProperties(ApplicationPropertyRS: ApplicationPropertyRS) {
    return ApplicationPropertyRS.getAllApplicationProperties();
}
