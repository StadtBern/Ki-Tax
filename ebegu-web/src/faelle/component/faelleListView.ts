import {IComponentOptions, IFilterService} from 'angular';
import {IStateService} from 'angular-ui-router';
import GesuchRS from '../../gesuch/service/gesuchRS.rest';
import GesuchModelManager from '../../gesuch/service/gesuchModelManager';
import BerechnungsManager from '../../gesuch/service/berechnungsManager';
import TSAntragDTO from '../../models/TSAntragDTO';
import TSAntragSearchresultDTO from '../../models/TSAntragSearchresultDTO';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {isAnyStatusOfVerfuegt} from '../../models/enums/TSAntragStatus';
import {TSRoleUtil} from '../../utils/TSRoleUtil';
import ITimeoutService = angular.ITimeoutService;
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;
import IQService = angular.IQService;
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
        'BerechnungsManager', '$state', '$log', 'CONSTANTS', 'AuthServiceRS', '$q'];

    constructor(private $filter: IFilterService, private gesuchRS: GesuchRS,
                private gesuchModelManager: GesuchModelManager, private berechnungsManager: BerechnungsManager,
                private $state: IStateService, private $log: ILogService, private CONSTANTS: any,
                private authServiceRS: AuthServiceRS, private $q: IQService) {
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

    /**
     * Fuer Benutzer mit der Rolle SACHBEARBEITER_INSTITUTION oder SACHBEARBEITER_TRAEGERSCHAFT oeffnet es das Gesuch mit beschraenkten Daten
     * Fuer anderen Benutzer wird das Gesuch mit allen Daten geoeffnet
     * @param antrag
     * @param event optinally this function can check if ctrl was clicked when opeing
     */
    public editFall(antrag: TSAntragDTO, event: any): void {
        if (antrag) {
            let isCtrlKeyPressed : boolean = (event && event.ctrlKey);
            if (this.authServiceRS.isOneOfRoles(TSRoleUtil.getTraegerschaftInstitutionOnlyRoles())) {
                // Reload Gesuch in gesuchModelManager on Init in fallCreationView because it has been changed since last time
                this.gesuchModelManager.clearGesuch();
                if (isAnyStatusOfVerfuegt(antrag.status)) {
                    this.openGesuch(antrag.antragId, 'gesuch.verfuegen', isCtrlKeyPressed);
                } else {
                    this.openGesuch(antrag.antragId, 'gesuch.betreuungen', isCtrlKeyPressed);
                }
            } else {
                this.openGesuch(antrag.antragId, 'gesuch.fallcreation', isCtrlKeyPressed);
            }
        }
    }

    /**
     * Oeffnet das Gesuch und geht zur gegebenen Seite (route)
     * @param antragId
     * @param urlToGoTo
     * @param isCtrlKeyPressed true if user pressed ctrl when clicking
     */
    private openGesuch(antragId: string, urlToGoTo: string, isCtrlKeyPressed: boolean): void {
        if (antragId) {
            if (isCtrlKeyPressed) {
                let url = this.$state.href(urlToGoTo, {createNew: false, gesuchId: antragId});
                window.open(url, '_blank');
            } else {
                this.$state.go(urlToGoTo, {createNew: false, gesuchId: antragId});
            }
        }
    }
}
