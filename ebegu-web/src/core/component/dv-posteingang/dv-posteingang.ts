import {IComponentOptions} from 'angular';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import {IStateService} from 'angular-ui-router';
import MitteilungRS from '../../service/mitteilungRS.rest';
import {TSSubmitEvent} from '../../events/TSSubmitEvent';
import IRootScopeService = angular.IRootScopeService;
let template = require('./dv-posteingang.html');

export class DvPosteingangComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = DvPosteingangController;
    controllerAs = 'vm';
}

export class DvPosteingangController {

    amountMitteilungen: number;

    static $inject: any[] = ['MitteilungRS', '$rootScope'];

    constructor(private mitteilungRS: MitteilungRS, private $rootScope: IRootScopeService) {
        this.getAmountNewMitteilungen();

        // call every 5 minutes (5*60*1000)
        setInterval(() => this.getAmountNewMitteilungen(), 300000);

        this.$rootScope.$on('POSTEINGANG_MAY_CHANGED', (event: any) => {
            this.getAmountNewMitteilungen();
        });
    }

    private getAmountNewMitteilungen(): void {
        this.mitteilungRS.getAmountMitteilungenForPosteingang().then((response: number) => {
            console.log(response);
            this.amountMitteilungen = response;
        });
    }

}
