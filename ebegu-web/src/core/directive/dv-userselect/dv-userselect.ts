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
        ngDisabled: '<',
        initialAll: '='
        //initialAll -> tritt nur ein, wenn explizit  { initial-all="true" } geschrieben ist
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
    initialAll: boolean;

    static $inject: string[] = ['UserRS', 'AuthServiceRS'];
    /* @ngInject */
    constructor(private userRS: UserRS, private authService: AuthServiceRS) {

    }

    //wird von angular aufgerufen
    $onInit() {
        this.updateUserList();
        if (!this.initialAll) { //tritt nur ein, wenn explizit  { initial-all="true" } geschrieben ist
            this.selectedUser = this.authService.getPrincipal();
        } else {
            this.selectedUser = undefined;
        }
        //initial nach aktuell eingeloggtem filtern
        if (this.smartTable && !this.initialAll) {
            this.smartTable.search(this.selectedUser.getFullName(), this.dvUsersearch);
        }
    }

    private updateUserList() {
        this.userRS.getBenutzerJAorAdmin().then((response: any) => {
            this.userList = angular.copy(response);
        });
    }


}
