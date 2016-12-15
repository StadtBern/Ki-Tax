import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
require('./dv-pulldown-user-menu.less');
let template = require('./dv-pulldown-user-menu.html');

export class DvPulldownUserMenuComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = DvPulldownUserMenuController;
    controllerAs = 'vm';
}

export class DvPulldownUserMenuController {

    static $inject: any[] = ['$state', 'AuthServiceRS'];
    TSRoleUtil = TSRoleUtil;

    constructor(private $state: IStateService, private authServiceRS: AuthServiceRS) {
        this.TSRoleUtil = TSRoleUtil;
    }

    public logout(): void {
        this.$state.go('login', {type: 'logout'});
    }

    public getPrincipal() {
        return this.authServiceRS.getPrincipal();
    }

    public getVersion(): string {
        return VERSION;
    }

    public getBuildtimestamp(): string {
        return BUILDTSTAMP;
    }

}
