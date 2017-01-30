import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';


posteingangRun.$inject = ['RouterHelper'];
/* @ngInject */
export function posteingangRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/start');
}

function getStates(): IState[] {
    return [
        new EbeguPosteingangState()
    ];
}

//STATES
export class EbeguPosteingangState implements IState {
    name = 'posteingang';
    template = '<posteingang-view flex="auto" class="overflow-scroll">';
    url = '/posteingang';
}
