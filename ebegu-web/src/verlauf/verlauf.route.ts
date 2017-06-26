import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';
import {IStateParamsService} from 'angular-ui-router';


verlaufRun.$inject = ['RouterHelper'];
/* @ngInject */
export function verlaufRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/start');
}

function getStates(): IState[] {
    return [
        new EbeguVerlaufState()
    ];
}

//STATES

export class EbeguVerlaufState implements IState {
    name = 'verlauf';
    template = '<verlauf-view flex="auto" class="overflow-hidden" layout="column">';
    url = '/verlauf/:gesuchId';
}

// PARAMS

export class IVerlaufStateParams implements IStateParamsService {
    gesuchId: string;
}
