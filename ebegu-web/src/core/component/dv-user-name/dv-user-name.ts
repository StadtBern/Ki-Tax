import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
require('./dv-user-name.less');
let template = require('./dv-user-name.html');

export class DvUserNameComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = DvUserNameController;
    controllerAs = 'vm';
}

export class DvUserNameController {

    static $inject: any[] = ['$state', 'AuthServiceRS'];

    constructor(private $state: IStateService, private authServiceRS: AuthServiceRS) {
    }

    public logout(): void {
        this.authServiceRS.logoutRequest().then(() => {
            this.$state.go('login' , {type: 'logout'});
        });
    }

    public getPrincipal() {
        return this.authServiceRS.getPrincipal();
    }

    public getVersion(): string {
        return VERSION;
    }

}
