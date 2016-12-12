import {IComponentOptions} from 'angular';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import {IStateService} from 'angular-ui-router';
let template = require('./dv-home-icon.html');

export class DvHomeIconComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = DvHomeIconController;
    controllerAs = 'vm';
}

export class DvHomeIconController {

    TSRoleUtil: any;

    static $inject: any[] = ['$state'];

    constructor(private $state: IStateService) {
        this.TSRoleUtil = TSRoleUtil;
    }

    public goBackHome(): void {
        this.$state.go('gesuchstellerDashboard');
    }

    public isCurrentPageGSDashboard(): boolean {
        return (this.$state.current && this.$state.current.name === 'gesuchstellerDashboard');
    }
}
