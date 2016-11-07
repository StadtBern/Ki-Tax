import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';


faelleRun.$inject = ['RouterHelper'];
/* @ngInject */
export function faelleRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/faelle');
}

function getStates(): IState[] {
    return [
        new EbeguPendenzenListState()
    ];
}

//STATES

export class EbeguPendenzenListState implements IState {
    name = 'faelle';
    template = '<faelle-list-view flex="auto" class="overflow-auto">';
    url = '/faelle';
}
