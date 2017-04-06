import {IComponentOptions} from 'angular';
import MitteilungRS from '../../service/mitteilungRS.rest';
import IRootScopeService = angular.IRootScopeService;
import {TSAuthEvent} from '../../../models/enums/TSAuthEvent';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
let template = require('./dv-posteingang.html');

export class DvPosteingangComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = DvPosteingangController;
    controllerAs = 'vm';
}

export class DvPosteingangController {

    amountMitteilungen: number = 0;
    reloadAmountMitteilungenInterval: number;

    static $inject: any[] = ['MitteilungRS', '$rootScope', 'AuthServiceRS'];

    constructor(private mitteilungRS: MitteilungRS, private $rootScope: IRootScopeService, private authServiceRS: AuthServiceRS) {
        this.getAmountNewMitteilungen();

        // call every 5 minutes (5*60*1000)
        this.reloadAmountMitteilungenInterval = setInterval(() => this.getAmountNewMitteilungen(), 300000);

        this.$rootScope.$on('POSTEINGANG_MAY_CHANGED', (event: any) => {
            this.getAmountNewMitteilungen();
        });

        this.$rootScope.$on(TSAuthEvent[TSAuthEvent.LOGIN_SUCCESS], () => {
            if (this.authServiceRS.isOneOfRoles(TSRoleUtil.getGesuchstellerJugendamtRoles())) {
                this.getAmountNewMitteilungen();
            }
        });

        // Das Interval muss nach jedem LOGOUT entfernt werden, um zu vermeiden dass es bei Benutzern auftritt die keinen Mitteilungen haben
        this.$rootScope.$on(TSAuthEvent[TSAuthEvent.LOGOUT_SUCCESS], () => {
            clearInterval(this.reloadAmountMitteilungenInterval);
        });
    }

    private getAmountNewMitteilungen(): void {
        this.mitteilungRS.getAmountMitteilungenForCurrentBenutzer().then((response: number) => {
            console.log(response);
            if (!response) {
                response = 0;
            }
            this.amountMitteilungen = response;
        });
    }

}
