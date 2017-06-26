import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';


statistikRun.$inject = ['RouterHelper'];
/* @ngInject */
export function statistikRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/start');
}

function getStates(): IState[] {
    return [
        new EbeguStatistikState()
    ];
}

//STATES

export class EbeguStatistikState implements IState {
    name = 'statistik';
    template = '<statistik-view flex="auto" class="overflow-scroll">';
    url = '/statistik';
}
