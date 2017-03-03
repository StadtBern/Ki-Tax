import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';


zahlungsauftragRun.$inject = ['RouterHelper'];
/* @ngInject */
export function zahlungsauftragRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/start');
}

function getStates(): IState[] {
    return [
        new EbeguZahlungsauftragState()
    ];
}

//STATES
export class EbeguZahlungsauftragState implements IState {
    name = 'zahlungsauftrag';
    template = '<zahlungsauftrag-view flex="auto" class="overflow-scroll">';
    url = '/zahlungsauftrag';
}
