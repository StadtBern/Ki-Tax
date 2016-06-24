import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';


authenticationRun.$inject = ['RouterHelper'];
/* @ngInject */
export function authenticationRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/auth/login');
}

function getStates(): IState[] {
    return [
        new EbeguLoginState()
    ];
}

//STATES

export class EbeguLoginState implements IState {
    name = 'login';
    template = '<dummy-authentication-view>';
    url = '/auth/login';
}
