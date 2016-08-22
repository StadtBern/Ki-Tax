import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';


pendenzRun.$inject = ['RouterHelper'];
/* @ngInject */
export function pendenzRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/pendenzenInstitution');
}

function getStates(): IState[] {
    return [
        new EbeguPendenzenListState()
    ];
}

//STATES

export class EbeguPendenzenListState implements IState {
    name = 'pendenzenInstitution';
    template = '<pendenzen-institution-list-view>';
    url = '/pendenzenInstitution';
}
