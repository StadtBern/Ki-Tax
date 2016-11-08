import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';


gesuchstellerDashboardRun.$inject = ['RouterHelper'];
/* @ngInject */
export function gesuchstellerDashboardRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/start');
}

function getStates(): IState[] {
    return [
        new EbeguGesuchstellerDashboardState()
    ];
}

//STATES

export class EbeguGesuchstellerDashboardState implements IState {
    name = 'gesuchstellerDashboard';
    template = '<gesuchsteller-dashboard-view class="layout-column flex-100">';
    url = '/gesuchstellerDashboard';
}
