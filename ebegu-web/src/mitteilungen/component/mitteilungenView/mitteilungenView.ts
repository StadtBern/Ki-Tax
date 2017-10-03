import IComponentOptions = angular.IComponentOptions;
import IFormController = angular.IFormController;
import IStateService = angular.ui.IStateService;
import ITimeoutService = angular.ITimeoutService;
import {IMitteilungenStateParams} from '../../mitteilungen.route';
import TSFall from '../../../models/TSFall';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import EbeguUtil from '../../../utils/EbeguUtil';

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
    fallId: string;
    TSRoleUtil = TSRoleUtil;

    static $inject: string[] = ['$state', '$stateParams', 'AuthServiceRS', '$timeout'];

    /* @ngInject */
    constructor(private $state: IStateService, private $stateParams: IMitteilungenStateParams,
                private authServiceRS: AuthServiceRS, private $timeout: ITimeoutService) {
    }

    $onInit() {
        if (this.$stateParams.fallId) {
            this.fallId = this.$stateParams.fallId;
        }
    }

    public cancel(): void {
        if (this.authServiceRS.isOneOfRoles(this.TSRoleUtil.getGesuchstellerOnlyRoles())) {
            this.$state.go('gesuchstellerDashboard');
        } else {
            this.$state.go('posteingang');
        }
    }

    $postLink() {
        this.$timeout(() => {
            EbeguUtil.selectFirst();
        }, 500); // this is the only way because it needs a little until everything is loaded
    }
}
