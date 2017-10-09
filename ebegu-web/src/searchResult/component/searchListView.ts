/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import {IComponentOptions} from 'angular';
import {ISearchResultateStateParams} from '../search.route';
import TSQuickSearchResult from '../../models/dto/TSQuickSearchResult';
import {SearchIndexRS} from '../../core/service/searchIndexRS.rest';
import EbeguUtil from '../../utils/EbeguUtil';
import TSAbstractAntragDTO from '../../models/TSAbstractAntragDTO';
import ILogService = angular.ILogService;

let template = require('./searchListView.html');
require('./searchListView.less');

export class SearchListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = SearchListViewController;
    controllerAs = 'vm';
}

export class SearchListViewController {

    private antragList: Array<TSAbstractAntragDTO>;
    totalResultCount: string = '-';
    private ignoreRequest: boolean = true; //we want to ignore the first filter request because the default sort triggers always a second one
    searchString: string;


    static $inject: string[] = [ '$log', '$stateParams', 'SearchIndexRS', 'EbeguUtil'];

    constructor(private $log: ILogService,  $stateParams: ISearchResultateStateParams,
                private searchIndexRS: SearchIndexRS, private ebeguUtil: EbeguUtil) {
        this.searchString = $stateParams.searchString;
        this.initViewModel();

    }

    private initViewModel() {
        this.searchIndexRS.globalSearch(this.searchString).then((quickSearchResult: TSQuickSearchResult) => {
            this.antragList = [];
            for (let res of quickSearchResult.resultEntities) {
                this.antragList.push(res.antragDTO);
            }
            this.ebeguUtil.handleSmarttablesUpdateBug(this.antragList);
        }).catch(() => {
            this.$log.warn('error during globalSearch');
        });
    }

    public getSearchList(): Array<TSAbstractAntragDTO> {
        return this.antragList;
    }


}
