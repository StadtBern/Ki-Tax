import {IComponentOptions} from 'angular';
import TSUser from '../models/TSUser';
import {TSRole} from '../models/enums/TSRole';
import {IStateService} from 'angular-ui-router';
import AuthServiceRS from './service/AuthServiceRS.rest';
import {TSMandant} from '../models/TSMandant';
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
        let mandant = new TSMandant();
        mandant.name = 'TestMandant';
        mandant.id = 'e3736eb8-6eef-40ef-9e52-96ab48d8f220';
        this.usersList.push(new TSUser('Jörg', 'Becker', 'jobe', 'password1', 'joerg.becker@bern.ch', mandant, TSRole.SACHBEARBEITER_JA));
        this.usersList.push(new TSUser('Jennifer', 'Müller', 'jemu', 'password2', 'jenniver.mueller@bern.ch', mandant, TSRole.SACHBEARBEITER_JA));
        this.usersList.push(new TSUser('Sophie', 'Bergmann', 'beso', 'password3', 'sophie.bergmann@gugus.ch', mandant, TSRole.SACHBEARBEITER_INSTITUTION));
        this.usersList.push(new TSUser('Kurt', 'Blaser', 'blku', 'password4', 'kurt.blaser@bern.ch', mandant, TSRole.ADMIN));
        this.usersList.push(new TSUser('Emma', 'Gerber', 'geem', 'password5', 'emma.gerber@myemail.ch', mandant, TSRole.GESUCHSTELLER));
    }

    public logIn(user: TSUser): void {
        this.authServiceRS.loginRequest(user).then(() => {
            this.$state.go('pendenzen');
        });
    }

}
