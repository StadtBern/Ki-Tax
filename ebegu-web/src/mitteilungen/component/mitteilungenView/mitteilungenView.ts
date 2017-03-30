import IComponentOptions = angular.IComponentOptions;
import FallRS from '../../../gesuch/service/fallRS.rest';
import {IMitteilungenStateParams} from '../../mitteilungen.route';
import TSFall from '../../../models/TSFall';
import IPromise = angular.IPromise;
import IQService = angular.IQService;
import IFormController = angular.IFormController;
import IStateService = angular.ui.IStateService;
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';

let template = require('./mitteilungenView.html');
require('./mitteilungenView.less');

export class MitteilungenViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = MitteilungenViewController;
    controllerAs = 'vm';
}

export class MitteilungenViewController {

    form: IFormController;
    fall: TSFall;
    TSRoleUtil = TSRoleUtil;

    static $inject: string[] = ['$state', '$stateParams', 'FallRS', 'AuthServiceRS', '$q'];
    /* @ngInject */
    constructor(private $state: IStateService, private $stateParams: IMitteilungenStateParams,
                private fallRS: FallRS, private authServiceRS: AuthServiceRS, private $q: IQService) {
        this.initViewModel();
    }

    private initViewModel() {
        if (this.$stateParams.fallId) {
            this.fallRS.findFall(this.$stateParams.fallId).then((response) => {
                this.fall = response;
            });
        }
    }

    public cancel(): void {
        if (this.authServiceRS.isOneOfRoles(this.TSRoleUtil.getGesuchstellerOnlyRoles())) {
            this.$state.go('gesuchstellerDashboard');
        } else {
            this.$state.go('posteingang');
        }
    }
}
