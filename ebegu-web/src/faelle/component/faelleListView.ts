import {IComponentOptions, IFilterService} from 'angular';
import {IStateService} from 'angular-ui-router';
import {InstitutionRS} from '../../core/service/institutionRS.rest';
import {InstitutionStammdatenRS} from '../../core/service/institutionStammdatenRS.rest';
import GesuchRS from '../../gesuch/service/gesuchRS.rest';
import GesuchModelManager from '../../gesuch/service/gesuchModelManager';
import BerechnungsManager from '../../gesuch/service/berechnungsManager';
import EbeguUtil from '../../utils/EbeguUtil';
import GesuchsperiodeRS from '../../core/service/gesuchsperiodeRS.rest';
import TSGesuchsperiode from '../../models/TSGesuchsperiode';
import TSPendenzJA from '../../models/TSPendenzJA';
import TSAntragSearchresultDTO from '../../models/TSAntragSearchresultDTO';
import {TSAntragTyp} from '../../models/enums/TSAntragTyp';
import TSGesuch from '../../models/TSGesuch';
import ITimeoutService = angular.ITimeoutService;
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;
let template = require('./faelleListView.html');
require('./faelleListView.less');

export class FaelleListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FaelleListViewController;
    controllerAs = 'vm';
}

export class FaelleListViewController {

    private antragList: Array<TSPendenzJA>;
    totalResultCount: string = '-';


    static $inject: string[] = ['EbeguUtil', '$filter', 'InstitutionRS', 'InstitutionStammdatenRS', 'GesuchsperiodeRS',
        'GesuchRS', 'GesuchModelManager', 'BerechnungsManager', '$state', '$log', 'CONSTANTS'];

    constructor(private ebeguUtil: EbeguUtil, private $filter: IFilterService,
                private institutionRS: InstitutionRS, private institutionStammdatenRS: InstitutionStammdatenRS, private gesuchsperiodeRS: GesuchsperiodeRS,
                private gesuchRS: GesuchRS, private gesuchModelManager: GesuchModelManager, private berechnungsManager: BerechnungsManager,
                private $state: IStateService, private $log: ILogService, private CONSTANTS: any) {
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


    public getAntragList(): Array<TSPendenzJA> {
        return this.antragList;
    }

    public getGesuchsperiodeAsString(gesuchsperiode: TSGesuchsperiode): string {
        return this.ebeguUtil.getGesuchsperiodeAsString(gesuchsperiode);
    }


    public editFall(antrag: TSPendenzJA): void {
        if (antrag) {
            //todo xaver fragen muessen wir hier was anders machen fuer inst und ja?
            if (antrag && antrag.antragTyp === TSAntragTyp.GESUCH) {
                this.gesuchRS.findGesuch(antrag.antragId).then((response) => {
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
            this.$state.go('gesuch.fallcreation');
        }
    }

//
//     private openBetreuung(pendenz: TSPendenzInstitution): void {
//         if (this.gesuchModelManager.getGesuch() && pendenz) {
//             this.gesuchModelManager.findKindById(pendenz.kindId);
//             let betreuungNumber: number = this.gesuchModelManager.findBetreuungById(pendenz.betreuungsId);
//             if (betreuungNumber > 0) {
//                 this.berechnungsManager.clear(); // nur um sicher zu gehen, dass alle alte Werte geloescht sind
//                 this.$state.go('gesuch.betreuung');
//             }
//         }
//     }
}
