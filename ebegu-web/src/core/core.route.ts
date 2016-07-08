import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState, IStateService} from 'angular-ui-router';
import ListResourceRS from './service/listResourceRS.rest';
import {MandantRS} from './service/mandantRS.rest';
import {TSAuthEvent} from '../models/enums/TSAuthEvent';
import AuthServiceRS from '../authentication/service/AuthServiceRS.rest';
import IRootScopeService = angular.IRootScopeService;
import ITimeoutService = angular.ITimeoutService;

appRun.$inject = ['angularMomentConfig', 'RouterHelper', 'ListResourceRS', 'MandantRS', '$rootScope', 'hotkeys', '$timeout', 'AuthServiceRS', '$state'];

/* @ngInject */
export function appRun(angularMomentConfig: any, routerHelper: RouterHelper, listResourceRS: ListResourceRS,
                       mandantRS: MandantRS, $rootScope: IRootScopeService, hotkeys: any, $timeout: ITimeoutService,
                       authServiceRS: AuthServiceRS, $state: IStateService) {

    routerHelper.configureStates(getStates(), '/pendenzen');
    angularMomentConfig.format = 'DD.MM.YYYY';
    // dieser call macht mit tests probleme, daher wird er fuer test auskommentiert

    $rootScope.$on('$viewContentLoaded', function () {
        angular.element('html, body').animate({scrollTop: 0}, 200);
        //    oder so  $anchorScroll('top') mit einem <div id="top">;
    });

    $rootScope.$on(TSAuthEvent[TSAuthEvent.LOGIN_SUCCESS], () => {
        //do stuff if needed
        if (ENV !== 'test') {
            listResourceRS.getLaenderList();  //initial aufruefen damit cache populiert wird
            mandantRS.getFirst();
        }
    });

    $rootScope.$on(TSAuthEvent[TSAuthEvent.NOT_AUTHENTICATED], () => {
        //user is not yet authenticated, show loginpage
        $state.go('login');

    });


    // Attempt to restore a user session upon startup
    if (authServiceRS.initWithCookie()) {
        console.log('logged in from cookie');
    }


    // Wir meochten eigentlich ueberall mit einem hotkey das formular submitten koennen
    //https://github.com/chieffancypants/angular-hotkeys#angular-hotkeys-
    hotkeys.add({
        combo: 'ctrl+shift+x',
        description: 'Press the last button with style class .next',
        callback: function () {
            $timeout(() => angular.element('.next').last().click());
        }
    });

}

function getStates(): IState[] {
    return [
        /* Add New States Above */
    ];
}
