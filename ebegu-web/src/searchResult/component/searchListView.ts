import {IComponentOptions, IFilterService} from 'angular';
import {IStateService} from 'angular-ui-router';
import GesuchRS from '../../gesuch/service/gesuchRS.rest';
import GesuchModelManager from '../../gesuch/service/gesuchModelManager';
import BerechnungsManager from '../../gesuch/service/berechnungsManager';
import TSGesuchsperiode from '../../models/TSGesuchsperiode';
import TSAntragDTO from '../../models/TSAntragDTO';
import TSAntragSearchresultDTO from '../../models/TSAntragSearchresultDTO';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {isAnyStatusOfVerfuegt} from '../../models/enums/TSAntragStatus';
import {TSRoleUtil} from '../../utils/TSRoleUtil';
import ITimeoutService = angular.ITimeoutService;
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;
import IQService = angular.IQService;
import {ISearchResultateStateParams} from '../search.route';
import {IStateParamsService} from 'angular-ui-router';
import TSQuickSearchResult from '../../models/dto/TSQuickSearchResult';
import {SearchIndexRS} from '../../core/service/searchIndexRS.rest';
import EbeguUtil from '../../utils/EbeguUtil';
let template = require('./searchListView.html');
require('./searchListView.less');

export class SearchListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = SearchListViewController;
    controllerAs = 'vm';
}

export class SearchListViewController {

    private antragList: Array<TSAntragDTO>;
    totalResultCount: string = '-';
    private ignoreRequest: boolean = true; //we want to ignore the first filter request because the default sort triggers always a second one
    searchString: string;


    static $inject: string[] = ['$filter', 'GesuchRS', 'GesuchModelManager',
        'BerechnungsManager', '$state', '$log', 'CONSTANTS', 'AuthServiceRS', '$q', '$stateParams', 'SearchIndexRS', 'EbeguUtil'];

    constructor(private $filter: IFilterService, private gesuchRS: GesuchRS,
                private gesuchModelManager: GesuchModelManager, private berechnungsManager: BerechnungsManager,
                private $state: IStateService, private $log: ILogService, private CONSTANTS: any,
                private authServiceRS: AuthServiceRS, private $q: IQService, $stateParams : ISearchResultateStateParams,
                private searchIndexRS : SearchIndexRS, private ebeguUtil: EbeguUtil) {
        this.searchString = $stateParams.searchString;
        this.initViewModel();

    }

    private initViewModel() {
        this.searchIndexRS.globalSearch(this.searchString).then((quickSearchResult: TSQuickSearchResult) => {
            this.antragList = [];
            for (let res of quickSearchResult.resultEntities) {
                this.antragList.push(res.antragDTO)
            }
            this.ebeguUtil.handleSmarttablesUpdateBug(this.antragList);
        }).catch(() => {
            this.$log.warn('error during quicksearch');
        });
    }


    public passFilterToServer = (tableFilterState: any): IPromise<TSAntragSearchresultDTO> => {
        if (!this.ignoreRequest) {
            this.$log.debug('Triggering ServerFiltering with Filter Object', tableFilterState);
            let x= new TSAntragSearchresultDTO;
            x.antragDTOs = this.getAntragList();
            x.totalResultSize = 27;
            return this.$q.when(x);
/*            return this.gesuchRS.searchAntraegeWithQuicksearch(tableFilterState, this.searchString).then((response: TSAntragSearchresultDTO) => {
                this.totalResultCount = response.totalResultSize ? response.totalResultSize.toString() : undefined;
                this.antragList = response.antragDTOs;
                return response;
            });*/
        } else {
            this.ignoreRequest = false;
            let deferred = this.$q.defer();
            deferred.resolve(undefined);
            return deferred.promise;
        }
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
     * @param event optinally this function can check if ctrl was clicked when opeing
     */
    public editFall(antrag: TSAntragDTO, event: any): void {
        if (antrag) {
            let isCtrlKeyPressed: boolean = (event && event.ctrlKey);
            if (this.authServiceRS.isOneOfRoles(TSRoleUtil.getTraegerschaftInstitutionRoles())) {
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
