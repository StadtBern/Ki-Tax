import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';
import {IStateParamsService} from 'angular-ui-router';
import TSZahlungsauftrag from '../models/TSZahlungsauftrag';


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
    url = '/zahlung/:zahlungsauftragId';
}


export class IZahlungsauftragStateParams implements IStateParamsService {
    zahlungsauftrag: TSZahlungsauftrag;
    zahlungsauftragId: string;
}
