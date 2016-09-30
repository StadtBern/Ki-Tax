import {IComponentOptions, IFilterService} from 'angular';
import {IStateService} from 'angular-ui-router';
import GesuchRS from '../../gesuch/service/gesuchRS.rest';
import GesuchModelManager from '../../gesuch/service/gesuchModelManager';
import BerechnungsManager from '../../gesuch/service/berechnungsManager';
import TSGesuchsperiode from '../../models/TSGesuchsperiode';
import TSAntragDTO from '../../models/TSAntragDTO';
import TSAntragSearchresultDTO from '../../models/TSAntragSearchresultDTO';
import {TSAntragTyp} from '../../models/enums/TSAntragTyp';
import TSGesuch from '../../models/TSGesuch';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import ITimeoutService = angular.ITimeoutService;
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;
import {TSRole} from '../../models/enums/TSRole';
let template = require('./faelleListView.html');
require('./faelleListView.less');

export class FaelleListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FaelleListViewController;
    controllerAs = 'vm';
}

export class FaelleListViewController {

    private antragList: Array<TSAntragDTO>;
    totalResultCount: string = '-';


    static $inject: string[] = ['$filter', 'GesuchRS', 'GesuchModelManager',
        'BerechnungsManager', '$state', '$log', 'CONSTANTS', 'AuthServiceRS'];

    constructor(private $filter: IFilterService, private gesuchRS: GesuchRS,
                private gesuchModelManager: GesuchModelManager, private berechnungsManager: BerechnungsManager,
                private $state: IStateService, private $log: ILogService, private CONSTANTS: any, private authServiceRS: AuthServiceRS) {
        this.initViewModel();
    }

    private initViewModel() {
        // this.updateAntragList();

    }


    public passFilterToServer = (tableFilterState: any): IPromise<TSAntragSearchresultDTO> => {
        this.$log.debug('Triggering ServerFiltering with Filter Object', tableFilterState);
        return this.gesuchRS.searchAntraege(tableFilterState).then((response: TSAntragSearchresultDTO) => {
            this.totalResultCount = response.totalResultSize ? response.totalResultSize.toString() : undefined;
            this.antragList = response.antragDTOs;
            return response;
        });
    };


    public getAntragList(): Array<TSAntragDTO> {
        return this.antragList;
    }

    public getGesuchsperiodeAsString(gesuchsperiode: TSGesuchsperiode): string {
        return gesuchsperiode.gesuchsperiodeString;
    }


    public editFall(antrag: TSAntragDTO): void {
        if (antrag) {
            //todo xaver fragen muessen wir hier was anders machen fuer inst und ja?
            if (antrag && antrag.antragTyp === TSAntragTyp.GESUCH) {
                this.gesuchRS.findGesuchForInstitution(antrag.antragId).then((response) => {
                    if (response) {
                        this.openGesuch(response);
                    }
                });
            }
        }
    }


    private openGesuch(gesuch: TSGesuch): void {
        if (gesuch) {
            this.berechnungsManager.clear();
            this.gesuchModelManager.setGesuch(gesuch);
            if (this.authServiceRS.isRole(TSRole.SACHBEARBEITER_INSTITUTION) || this.authServiceRS.isRole(TSRole.SACHBEARBEITER_TRAEGERSCHAFT)) {
                this.$state.go('gesuch.verfuegen');
            } else {
                this.$state.go('gesuch.fallcreation');
            }
        }
    }

}
