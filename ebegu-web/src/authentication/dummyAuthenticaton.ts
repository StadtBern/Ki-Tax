import {IComponentOptions} from 'angular';
import TSUser from '../models/TSUser';
import {TSRole} from '../models/enums/TSRole';
import {IStateService} from 'angular-ui-router';
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

    static $inject: string[] = ['$state'];

    constructor(private $state: IStateService) {
        this.usersList = [];
        this.usersList.push(new TSUser('Jörg', 'Becker', 'joerg.becker@bern.ch', TSRole.SACHBEARBEITER_JA));
        this.usersList.push(new TSUser('Jennifer', 'Müller', 'jenniver.mueller@bern.ch', TSRole.SACHBEARBEITER_JA));
        this.usersList.push(new TSUser('Sophie', 'Bergmann', 'sophie.bergmann@gugus.ch', TSRole.SACHBEARBEITER_INSTITUTION));
        this.usersList.push(new TSUser('Kurt', 'Blaser', 'kurt.blaser@bern.ch', TSRole.ADMIN));
        console.log('list', this.usersList);
    }

    public logIn(user: TSUser): void {
        this.$state.go('pendenzen')
        console.log('login!');
    }
}
