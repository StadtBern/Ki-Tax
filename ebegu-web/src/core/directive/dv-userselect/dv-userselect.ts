import {IDirective, IDirectiveFactory} from 'angular';
import TSUser from '../../../models/TSUser';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import UserRS from '../../service/userRS.rest';
import Moment = moment.Moment;
import INgModelController = angular.INgModelController;
let template = require('./dv-userselect.html');


export class DVUserselect implements IDirective {
    restrict = 'E';
    require: any = {smartTable: '?^stTable'};
    scope = {
        ngModel: '=',
        inputId: '@',
        dvUsersearch: '@',
        ngDisabled: '<'
    };
    controller = UserselectController;
    controllerAs = 'vm';
    bindToController = true;
    template = template;

    static factory(): IDirectiveFactory {
        const directive = () => new DVUserselect();
        directive.$inject = [];
        return directive;
    }
}
/**
 * Direktive  der initial die smart table nach dem aktuell eingeloggtem user filtert
 */
export class UserselectController {
    selectedUser: TSUser;
    smartTable: any;
    userList: Array<TSUser>;
    dvUsersearch: string;

    static $inject: string[] = ['UserRS', 'AuthServiceRS'];
    /* @ngInject */
    constructor(private userRS: UserRS, private authService: AuthServiceRS) {
        this.updateUserList();
        this.selectedUser = authService.getPrincipal();
    }


    //wird von angular aufgerufen
    $onInit() {
        //initial nach aktuell eingeloggtem filtern
        if (this.smartTable) {
            this.smartTable.search(this.selectedUser.getFullName(), this.dvUsersearch);
        }
    }

    private updateUserList() {
        this.userRS.getAllUsers().then((response: any) => {
            this.userList = angular.copy(response);
        });
    }


}
