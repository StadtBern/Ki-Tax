import {IComponentOptions, ILogService, IFilterService, IQService, IPromise} from 'angular';
import * as moment from 'moment';
import TSSearchResultEntry from '../../../../models/dto/TSSearchResultEntry';
import TSQuickSearchResult from '../../../../models/dto/TSQuickSearchResult';
import {IStateService} from 'angular-ui-router';
import AuthServiceRS from '../../../../authentication/service/AuthServiceRS.rest';
import {TSRoleUtil} from '../../../../utils/TSRoleUtil';
import {isAnyStatusOfVerfuegt} from '../../../../models/enums/TSAntragStatus';
import GesuchModelManager from '../../../../gesuch/service/gesuchModelManager';
import EbeguUtil from '../../../../utils/EbeguUtil';
import {SearchIndexRS} from '../../../service/searchIndexRS.rest';
import Moment = moment.Moment;
import ITranslateService = angular.translate.ITranslateService;
let template = require('./dv-quicksearchbox.html');


export class DvQuicksearchboxComponentConfig implements IComponentOptions {
    transclude = true;
    template = template;
    controller = DvQuicksearchboxController;
    controllerAs = 'vm';

}

export class DvQuicksearchboxController {

    noCache: boolean = true;
    delay: number = 250;

    selectedItem: TSSearchResultEntry;
    searchQuery: string;

    static $inject: any[] = ['EbeguUtil', '$timeout', '$log', '$q', 'SearchIndexRS', 'CONSTANTS', '$filter', '$translate',
        '$state', 'AuthServiceRS', 'GesuchModelManager'];
    /* @ngInject */
    constructor(private ebeguUtil: EbeguUtil, private $timeout: IFilterService, private $log: ILogService,
                private $q: IQService, private searchIndexRS: SearchIndexRS, private CONSTANTS: any,
                private $filter: IFilterService, private $translate: ITranslateService,
                private $state: IStateService, private authServiceRS: AuthServiceRS, private gesuchModelManager: GesuchModelManager) {

    }

    //wird von angular aufgerufen
    $onInit() {
        this.selectedItem = undefined;
    }


    public querySearch(query: string): IPromise<Array<TSSearchResultEntry>> {
        let deferred = this.$q.defer();
        this.searchIndexRS.quickSearch(query).then((quickSearchResult: TSQuickSearchResult) => {
            this.limitResultsize(quickSearchResult);
            deferred.resolve(quickSearchResult.resultEntities);
        }).catch(() => {
            deferred.resolve([]);
            this.$log.warn('error during quicksearch');
        });
        return deferred.promise;

    }

    private limitResultsize(quickSearchResult: TSQuickSearchResult) {

        let limitedResults = this.$filter('limitTo')(quickSearchResult.resultEntities, 8);
        // if (limitedResults.length < quickSearchResult.length) { //total immer anzeigen
        this.addFakeTotalResultEntry(quickSearchResult, limitedResults)
    }

    private addFakeTotalResultEntry(quickSearchResult: TSQuickSearchResult, limitedResults: TSSearchResultEntry[]) {
        if (angular.isArray(limitedResults) && limitedResults.length > 0) {
            let totalResEntry: TSSearchResultEntry = new TSSearchResultEntry();
            totalResEntry.text = this.$translate.instant('QUICKSEARCH_ALL_RESULTS', {totalNum: quickSearchResult.totalResultSize});
            limitedResults.push(totalResEntry);
        }
        quickSearchResult.resultEntities = limitedResults;
    }

    private selectItemChanged() {
        this.navigateToFall()

    }


    //TODO: ähnlicher code wie bei faelleListView.
    private navigateToFall() {
        if (this.selectedItem && this.selectedItem.gesuchID) {
            if (this.authServiceRS.isOneOfRoles(TSRoleUtil.getTraegerschaftInstitutionRoles()) && this.selectedItem.antragDTO) {
                // Reload Gesuch in gesuchModelManager on Init in fallCreationView because it has been changed since last time
                this.gesuchModelManager.clearGesuch();
                if (isAnyStatusOfVerfuegt(this.selectedItem.antragDTO.status)) {

                    this.openGesuch(this.selectedItem.gesuchID, 'gesuch.verfuegen');
                } else {
                    this.openGesuch(this.selectedItem.gesuchID, 'gesuch.betreuungen');
                }
            } else {
                this.openGesuch(this.selectedItem.gesuchID, 'gesuch.fallcreation');
            }
        } else {
            window.alert('navigate to result page')
        }
    }


    /**
     * Oeffnet das Gesuch und geht zur gegebenen Seite (route)
     * @param antragId
     * @param urlToGoTo
     * @param inNewTab true if fall should be opend in new tab
     */
    private openGesuch(antragId: string, urlToGoTo: string, inNewTab?: boolean): void {
        if (antragId) {
            if (inNewTab) {
                let url = this.$state.href(urlToGoTo, {createNew: false, gesuchId: antragId});
                window.open(url, '_blank');
            } else {
                this.$state.go(urlToGoTo, {createNew: false, gesuchId: antragId});
            }
        }
    }
}
