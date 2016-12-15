import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState, IStateService} from 'angular-ui-router';
import ListResourceRS from './service/listResourceRS.rest';
import {MandantRS} from './service/mandantRS.rest';
import {TSAuthEvent} from '../models/enums/TSAuthEvent';
import AuthServiceRS from '../authentication/service/AuthServiceRS.rest';
import TSUser from '../models/TSUser';
import {TSRoleUtil} from '../utils/TSRoleUtil';
import ErrorService from './errors/service/ErrorService';
import IRootScopeService = angular.IRootScopeService;
import ITimeoutService = angular.ITimeoutService;
import ILocationService = angular.ILocationService;
import ILogService = angular.ILogService;

appRun.$inject = ['angularMomentConfig', 'RouterHelper', 'ListResourceRS', 'MandantRS', '$rootScope', 'hotkeys',
    '$timeout', 'AuthServiceRS', '$state', '$location', '$window', '$log' , 'ErrorService'];

/* @ngInject */
export function appRun(angularMomentConfig: any, routerHelper: RouterHelper, listResourceRS: ListResourceRS,
                       mandantRS: MandantRS, $rootScope: IRootScopeService, hotkeys: any, $timeout: ITimeoutService,
                       authServiceRS: AuthServiceRS, $state: IStateService, $location: ILocationService, $window: ng.IWindowService,  $log: ILogService, errorService: ErrorService) {
    // navigationLogger.toggle();

    // Fehler beim Navigieren ueber ui-route ins Log schreiben
    $rootScope.$on('$stateChangeError',  (event, toState, toParams, fromState, fromParams, error) => {
        $log.error('Fehler beim Navigieren');
        $log.error('$stateChangeError --- event, toState, toParams, fromState, fromParams, error');
        $log.error(event, toState, toParams, fromState, fromParams, error);
    });
    //Normale Benutzer duefen nicht auf admin Seite
    $rootScope.$on('$stateChangeStart',
        (event, toState, toParams, fromState, fromParams, options) => {
            let principal: TSUser = authServiceRS.getPrincipal();
            let forbiddenPlaces = ['admin', 'institution', 'parameter', 'traegerschaft'];
            let isAdmin: boolean = authServiceRS.isOneOfRoles(TSRoleUtil.getAdministratorRoles());
            if (toState && forbiddenPlaces.indexOf(toState.name) !== -1 && authServiceRS.getPrincipal() && !isAdmin) {
                errorService.addMesageAsError('ERROR_UNAUTHORIZED');
                $log.debug('prevented navigation to page because user is not admin');
                event.preventDefault();
            }
        });

    routerHelper.configureStates(getStates(), '/start');
    angularMomentConfig.format = 'DD.MM.YYYY';
    // dieser call macht mit tests probleme, daher wird er fuer test auskommentiert


    // not used anymore?
    $rootScope.$on(TSAuthEvent[TSAuthEvent.LOGIN_SUCCESS], () => {
        //do stuff if needed
        if (ENV !== 'test') {
            listResourceRS.getLaenderList();  //initial aufruefen damit cache populiert wird
            mandantRS.getFirst();
        }
    });

    $rootScope.$on(TSAuthEvent[TSAuthEvent.CHANGE_USER], () => {
        // User has changed with backdoor, we need to reload app to delete stored data.
        // See: http://stackoverflow.com/questions/26522875/best-practices-for-clearing-data-in-sevices-on-logout-in-angularjs
        $window.location.reload();
    });

    $rootScope.$on(TSAuthEvent[TSAuthEvent.NOT_AUTHENTICATED], () => {
        //user is not yet authenticated, show loginpage

        let currentPath = angular.copy($location.absUrl());
        console.log('going to login page wiht current path ', currentPath);

        //wenn wir schon auf der lognseite oder im redirect sind redirecten wir nicht
        if (currentPath.indexOf('fedletSSOInit') === -1
            && ($state.current !== undefined && $state.current.name !== 'login')
            && ($state.current !== undefined && $state.current.name !== 'locallogin')
            && currentPath.indexOf('sendRedirectForValidation') === -1) {
            $state.go('login', {relayPath: currentPath, type: 'login'});
        } else {
            console.log('supressing redirect to ', currentPath);
        }

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
