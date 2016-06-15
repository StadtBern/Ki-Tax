import {IComponentOptions} from 'angular';
import TSUser from '../models/TSUser';
import {TSRole} from '../models/enums/TSRole';
import {IStateService} from 'angular-ui-router';
import AuthServiceRS from './service/AuthServiceRS.rest';
let template = require('./dummyAuthentication.html');
require('./dummyAuthentication.less');

export class AuthenticationComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = AuthenticationListViewController;
    controllerAs = 'vm';
}

export class AuthenticationListViewController {

    public usersList: Array<TSUser>;

    static $inject: string[] = ['$state', 'AuthServiceRS'];

    constructor(private $state: IStateService, private authServiceRS: AuthServiceRS) {
        this.usersList = [];
        this.usersList.push(new TSUser('jobe', 'Jörg', 'Becker', 'jobe', 'password1', 'joerg.becker@bern.ch', [TSRole.SACHBEARBEITER_JA]));
        this.usersList.push(new TSUser('jemu', 'Jennifer', 'Müller', 'jemu', 'password2', 'jenniver.mueller@bern.ch', [TSRole.SACHBEARBEITER_JA]));
        this.usersList.push(new TSUser('beso', 'Sophie', 'Bergmann', 'beso', 'password3', 'sophie.bergmann@gugus.ch', [TSRole.SACHBEARBEITER_INSTITUTION]));
        this.usersList.push(new TSUser('blku', 'Kurt', 'Blaser', 'blku', 'password4', 'kurt.blaser@bern.ch', [TSRole.ADMIN]));
    }

    public logIn(user: TSUser): void {
        this.authServiceRS.loginRequest(user).then(() => {
            this.$state.go('pendenzen');
        });
    }
}
