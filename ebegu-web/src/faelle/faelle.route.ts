import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';


faelleRun.$inject = ['RouterHelper'];
/* @ngInject */
export function faelleRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/start');
}

function getStates(): IState[] {
    return [
        new EbeguFaelleListState()
    ];
}

//STATES

export class EbeguFaelleListState implements IState {
    name = 'faelle';
    template = '<faelle-list-view flex="auto" class="overflow-scroll">';
    url = '/faelle';
}
