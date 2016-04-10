import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';
import ListResourceRS from './service/listResourceRS';

// FIXME webpack ngAnnotate
/* @ngInject */
export function appRun(angularMomentConfig: any, routerHelper: RouterHelper, listResourceRS: ListResourceRS) {
    routerHelper.configureStates(getStates());
    angularMomentConfig.format = 'DD.MM.YYYY';
    listResourceRS.getLaenderList();  //initial aufruefen damit cache populiert wird
}

function getStates(): IState[] {
    return [
        /* Add New States Above */
    ];
}
