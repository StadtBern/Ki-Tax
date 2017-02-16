import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';


zahlungRun.$inject = ['RouterHelper'];
/* @ngInject */
export function zahlungRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/start');
}

function getStates(): IState[] {
    return [
        new EbeguZahlungState()
    ];
}

//STATES
export class EbeguZahlungState implements IState {
    name = 'zahlung';
    template = '<zahlung-view flex="auto" class="overflow-scroll">';
    url = '/zahlung';
}
