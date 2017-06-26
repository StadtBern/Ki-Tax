import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';
import {IStateParamsService} from 'angular-ui-router';


alleVerfuegungenRun.$inject = ['RouterHelper'];
/* @ngInject */
export function alleVerfuegungenRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/start');
}

function getStates(): IState[] {
    return [
        new EbeguAlleVerfuegungenState()
    ];
}

//STATES

export class EbeguAlleVerfuegungenState implements IState {
    name = 'alleVerfuegungen';
    template = '<alle-verfuegungen-view flex="auto" class="overflow-hidden" layout="column">';
    url = '/alleVerfuegungen/:fallId';
}

// PARAMS

export class IAlleVerfuegungenStateParams implements IStateParamsService {
    fallId: string;
}
