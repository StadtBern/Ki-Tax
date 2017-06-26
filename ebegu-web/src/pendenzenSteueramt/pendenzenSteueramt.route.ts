import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';


pendenzRun.$inject = ['RouterHelper'];
/* @ngInject */
export function pendenzRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/start');
}

function getStates(): IState[] {
    return [
        new EbeguPendenzenSteueramtListState()
    ];
}

//STATES

export class EbeguPendenzenSteueramtListState implements IState {
    name = 'pendenzenSteueramt';
    template = '<pendenzen-steueramt-list-view flex="auto" class="overflow-scroll">';
    url = '/pendenzenSteueramt';
}
