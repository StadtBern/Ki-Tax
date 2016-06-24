import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';


pendenzRun.$inject = ['RouterHelper'];
/* @ngInject */
export function pendenzRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/pendenzen');
}

function getStates(): IState[] {
    return [
        new EbeguPendenzenListState()
    ];
}

//STATES

export class EbeguPendenzenListState implements IState {
    name = 'pendenzen';
    template = '<pendenzen-list-view>';
    url = '/pendenzen';
}
