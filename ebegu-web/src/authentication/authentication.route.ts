import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState, IStateService} from 'angular-ui-router';
import {ApplicationPropertyRS} from '../admin/service/applicationPropertyRS.rest';
import IStateParamsService = angular.ui.IStateParamsService;
import IQService = angular.IQService;
import ILogService = angular.ILogService;
import IPromise = angular.IPromise;


authenticationRun.$inject = ['RouterHelper'];
/* @ngInject */
export function authenticationRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/start');
}

function getStates(): IState[] {
    return [
        new EbeguLoginState(),
        new EbeguLocalLoginState(),
        new EbeguStartState()
    ];
}

//STATES

export class EbeguLoginState implements IState {
    name = 'login';
    template = '<authentication-view>';
    //HINWEIS: Soweit ich sehen kann koennen url navigationen mit mehr als einem einzigen slash am Anfang nicht manuell in der Adressbar aufgerufen werden?
    url = '/login?type&relayPath';
}

export class EbeguLocalLoginState implements IState {
    name = 'locallogin';
    template = '<dummy-authentication-view flex="auto" class="overflow-scroll">';
    url = '/locallogin';
    resolve = {
        dummyLoginEnabled: readDummyLoginEnabled
    };
}

export class EbeguStartState implements IState {
    name = 'start';
    template = '<start-view>';
    url = '/start';
}



export class IAuthenticationStateParams implements IStateParamsService {
    relayPath: string;
    type: string;
}

readDummyLoginEnabled.$inject = ['ApplicationPropertyRS',  '$state', '$q', '$log'];
/* @ngInject */
export function readDummyLoginEnabled(applicationPropertyRS: ApplicationPropertyRS,  $state: IStateService, $q: IQService, $log: ILogService): IPromise<boolean> {
    return applicationPropertyRS.isDummyMode()
        .then((response: boolean) => {
            if (response === false) {
                $log.debug('page is disabled');
                $state.go('start');
            }
            return response;
        }).catch(() => {
            let deferred = $q.defer();
            deferred.resolve(undefined);
            $state.go('login');
            return deferred.promise;
        });


}

