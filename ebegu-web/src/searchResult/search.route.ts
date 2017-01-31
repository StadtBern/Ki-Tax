import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';
import {IStateParamsService} from 'angular-ui-router';


searchRun.$inject = ['RouterHelper'];
/* @ngInject */
export function searchRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/start');
}

function getStates(): IState[] {
    return [
        new EbeguSearchListState()
    ];
}

//STATES

export class EbeguSearchListState implements IState {
    name = 'search';
    template = '<search-list-view flex="auto" class="overflow-scroll">';
    url = '/search/:searchString';
}


export class ISearchResultateStateParams implements IStateParamsService {
    searchString: string;
}
