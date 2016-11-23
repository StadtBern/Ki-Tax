import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';


pendenzRun.$inject = ['RouterHelper'];
/* @ngInject */
export function pendenzRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/start');
}

function getStates(): IState[] {
    return [
        new EbeguPendenzenInstitutionListState()
    ];
}

//STATES

export class EbeguPendenzenInstitutionListState implements IState {
    name = 'pendenzenInstitution';
    template = '<pendenzen-institution-list-view flex="auto" class="overflow-scroll">';
    url = '/pendenzenInstitution';
}
