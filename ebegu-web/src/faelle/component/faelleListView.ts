import {IComponentOptions, IFilterService} from 'angular';
import {IStateService} from 'angular-ui-router';
import GesuchRS from '../../gesuch/service/gesuchRS.rest';
import GesuchModelManager from '../../gesuch/service/gesuchModelManager';
import BerechnungsManager from '../../gesuch/service/berechnungsManager';
import TSGesuchsperiode from '../../models/TSGesuchsperiode';
import TSAntragDTO from '../../models/TSAntragDTO';
import TSAntragSearchresultDTO from '../../models/TSAntragSearchresultDTO';
import {TSAntragTyp} from '../../models/enums/TSAntragTyp';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {TSRole} from '../../models/enums/TSRole';
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

    /**
     * Fuer Benutzer mit der Rolle SACHBEARBEITER_INSTITUTION oder SACHBEARBEITER_TRAEGERSCHAFT oeffnet es das Gesuch mit beschraenkten Daten
     * Fuer anderen Benutzer wird das Gesuch mit allen Daten geoeffnet
     * @param antrag
     */
    public editFall(antrag: TSAntragDTO): void {
        if (antrag) {
            //todo xaver fragen muessen wir hier was anders machen fuer inst und ja?
            if (antrag) {
                if (this.authServiceRS.isRole(TSRole.SACHBEARBEITER_INSTITUTION) || this.authServiceRS.isRole(TSRole.SACHBEARBEITER_TRAEGERSCHAFT)) {
                    this.openGesuch(antrag.antragId, 'gesuch.verfuegen');
                } else {
                    this.openGesuch(antrag.antragId, 'gesuch.fallcreation');
                }
            }
        }
    }

    /**
     * Oeffnet das Gesuch und geht zur gegebenen Seite (route)
     * @param antragId
     * @param urlToGoTo
     */
    private openGesuch(antragId: string, urlToGoTo: string): void {
        if (antragId) {
            this.$state.go(urlToGoTo, {createNew: false, gesuchId: antragId});
        }
    }

}
