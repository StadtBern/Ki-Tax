import {IComponentOptions} from 'angular';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import {IStateService} from 'angular-ui-router';
import MitteilungRS from '../../service/mitteilungRS.rest';
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

    static $inject: any[] = ['MitteilungRS'];

    constructor(private mitteilungRS: MitteilungRS) {
        this.getAmountNewMitteilungen();
    }

    private getAmountNewMitteilungen(): void {
        this.mitteilungRS.getAmountMitteilungenForPosteingang().then((response: number) => {
            console.log(response);
            this.amountMitteilungen = response;
        });
    }

}
