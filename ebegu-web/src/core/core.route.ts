import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState} from 'angular-ui-router';
import ListResourceRS from './service/listResourceRS.rest';
import IRootScopeService = angular.IRootScopeService;

appRun.$inject = ['angularMomentConfig', 'RouterHelper', 'ListResourceRS', '$rootScope'];

/* @ngInject */
export function appRun(angularMomentConfig: any, routerHelper: RouterHelper, listResourceRS: ListResourceRS, $rootScope: IRootScopeService) {
    routerHelper.configureStates(getStates());
    angularMomentConfig.format = 'DD.MM.YYYY';
    //todo homa dieser call macht mit tests problemen
    if (ENV !== 'test') {
        listResourceRS.getLaenderList();  //initial aufruefen damit cache populiert wird
    }
    $rootScope.$on('$viewContentLoaded', function () {
        angular.element('html, body').animate({scrollTop: 0}, 200);
    //    oder so  $anchorScroll('top') mit einem <div id="top">;
    });

}

function getStates(): IState[] {
    return [
        /* Add New States Above */
    ];
}
