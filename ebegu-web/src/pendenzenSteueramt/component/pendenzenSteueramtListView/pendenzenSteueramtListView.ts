import IComponentOptions = angular.IComponentOptions;
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import TSAntragDTO from '../../../models/TSAntragDTO';
import TSAntragSearchresultDTO from '../../../models/TSAntragSearchresultDTO';
import GesuchRS from '../../../gesuch/service/gesuchRS.rest';
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
let template = require('./pendenzenSteueramtListView.html');

export class PendenzenSteueramtListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = PendenzenSteueramtListViewController;
    controllerAs = 'vm';
}

export class PendenzenSteueramtListViewController {

    totalResultCount: string = '-';
    TSRoleUtil: any;


    static $inject: string[] = ['GesuchModelManager', '$state', '$log', 'GesuchRS'];

    constructor(private gesuchModelManager: GesuchModelManager, private $state: IStateService, private $log: ILogService,
                private gesuchRS: GesuchRS) {
        this.TSRoleUtil = TSRoleUtil;
    }

    $onInit() {
    }


    public editpendenzSteueramt(pendenz: TSAntragDTO, event: any): void {
        if (pendenz) {
            let isCtrlKeyPressed: boolean = (event && event.ctrlKey);
            this.openPendenz(pendenz, isCtrlKeyPressed);
        }
    }

    public passFilterToServer = (tableFilterState: any): IPromise<TSAntragSearchresultDTO> => {
        this.$log.debug('Triggering ServerFiltering with Filter Object', tableFilterState);
        return this.gesuchRS.searchAntraege(tableFilterState).then((response: TSAntragSearchresultDTO) => {
            this.totalResultCount = response.totalResultSize ? response.totalResultSize.toString() : undefined;
            return response;
        });

    }

    private openPendenz(pendenz: TSAntragDTO, isCtrlKeyPressed: boolean) {
        this.gesuchModelManager.clearGesuch();
        let navObj: any = {
            gesuchId: pendenz.antragId
        };
        if (isCtrlKeyPressed) {
            let url = this.$state.href('gesuch.familiensituation', navObj);
            window.open(url, '_blank');
        } else {
            this.$state.go('gesuch.familiensituation', navObj);
        }
    }

}
