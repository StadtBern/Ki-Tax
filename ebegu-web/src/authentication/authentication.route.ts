import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';
import IStateParamsService = angular.ui.IStateParamsService;


authenticationRun.$inject = ['RouterHelper'];
/* @ngInject */
export function authenticationRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/auth/login');
}

function getStates(): IState[] {
    return [
        new EbeguLoginState(),
        new EbeguLocalLoginState()
    ];
}

//STATES

export class EbeguLoginState implements IState {
    name = 'login';
    template = '<authentication-view>';
    url = '/auth/login/?type&relayPath';
}

export class EbeguLocalLoginState implements IState {
    name = 'login.local';
    template = '<dummy-authentication-view>';
    url = '/auth/locallogin';
}


export class IAuthenticationStateParams implements IStateParamsService {
    relayPath: string;
    type: string;
}

