import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import TSAntragDTO from '../../models/TSAntragDTO';
import PendenzRS from '../../pendenzen/service/PendenzRS.rest';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import ITimeoutService = angular.ITimeoutService;
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;
let template = require('./gesuchstellerDashboardView.html');
require('./gesuchstellerDashboardView.less');

export class GesuchstellerDashboardListViewConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = GesuchstellerDashboardListViewController;
    controllerAs = 'vm';
}

export class GesuchstellerDashboardListViewController {

    private antragList: Array<TSAntragDTO>;
    totalResultCount: string = '-';


    static $inject: string[] = ['$state', '$log', 'CONSTANTS', 'AuthServiceRS', 'PendenzRS'];

    constructor(private $state: IStateService, private $log: ILogService, private CONSTANTS: any,
                private authServiceRS: AuthServiceRS, private pendenzRS: PendenzRS) {
        this.initViewModel();
    }

    private initViewModel() {
        this.updateAntragList();
    }

    private updateAntragList() {
        this.pendenzRS.getAntraegeGesuchstellerList(this.authServiceRS.getPrincipal().username).then((response: any) => {
            this.antragList = angular.copy(response);
        });
    }

    public goToMitteilungenOeffen() {
        this.$log.warn('Not yet impl');
    }

    public getAntragList(): Array<TSAntragDTO> {
        return this.antragList;
    }

    public getNumberMitteilungen(): number {
        return 12;
    }

    public editAntrag(antrag: TSAntragDTO): void {
        if (antrag) {
            this.$state.go('gesuch.fallcreation', {createNew: false, gesuchId: antrag.antragId});
        }
    }


}
