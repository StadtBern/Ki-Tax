import {IComponentOptions} from 'angular';
import TSAntragDTO from '../../../models/TSAntragDTO';
import PendenzRS from '../../service/PendenzRS.rest';
import * as moment from 'moment';
import ITimeoutService = angular.ITimeoutService;
import Moment = moment.Moment;
import TSUser from '../../../models/TSUser';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
let template = require('./pendenzenListView.html');
require('./pendenzenListView.less');

export class PendenzenListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = PendenzenListViewController;
    controllerAs = 'vm';
}

export class PendenzenListViewController {

    private pendenzenList: Array<TSAntragDTO>;

    static $inject: string[] = ['PendenzRS', 'CONSTANTS', 'AuthServiceRS'];

    constructor(public pendenzRS: PendenzRS, private CONSTANTS: any, private authServiceRS: AuthServiceRS) {
        this.initViewModel();
    }

    private initViewModel() {
        // Initial werden die Pendenzen des eingeloggten Benutzers geladen
        this.updatePendenzenList(this.authServiceRS.getPrincipal().username);
    }

    private updatePendenzenList(username: string) {
        this.pendenzRS.getPendenzenListForUser(username).then((response: any) => {
            this.pendenzenList = angular.copy(response);
        });
    }

    public getPendenzenList(): Array<TSAntragDTO> {
        return this.pendenzenList;
    }

    public userChanged(user: TSUser): void {
        if (user) {
            this.updatePendenzenList(user.username);
        }
    }
}
