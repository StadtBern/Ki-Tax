import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import AuthenticationUtil from '../../../utils/AuthenticationUtil';
import TSUser from '../../../models/TSUser';
import {TSAuthEvent} from '../../../models/enums/TSAuthEvent';
import IWindowService = angular.IWindowService;
import IHttpParamSerializer = angular.IHttpParamSerializer;
import ITimeoutService = angular.ITimeoutService;
import ILocationService = angular.ILocationService;
import IRootScopeService = angular.IRootScopeService;
let template = require('./startView.html');
require('./startView.less');

export class StartComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = StartViewController;
    controllerAs = 'vm';
}

export class StartViewController {


    static $inject: string[] = ['$state', '$rootScope', 'AuthServiceRS'];

    constructor(private $state: IStateService, private $rootScope: IRootScopeService, private authService: AuthServiceRS) {


    }

    $onInit() {
        let user: TSUser = this.authService.getPrincipal();
        if (this.authService.getPrincipal()) {  // wenn logged in
            AuthenticationUtil.navigateToStartPageForRole(user, this.$state);
        } else {
            //wenn wir noch nicht eingeloggt sind werden wir das event welches das login prozedere anstoesst
            this.$rootScope.$broadcast(TSAuthEvent[TSAuthEvent.NOT_AUTHENTICATED], 'not logged in on startpage');
        }
    }
}
