import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';


pendenzenRun.$inject = ['RouterHelper'];
/* @ngInject */
export function pendenzenRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/gesuch/familiensituation');
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
