import {EbeguWebCore} from '../core/core.module';
import {searchRun} from './search.route';
import {SearchListViewComponentConfig} from './component/searchListView';

export const EbeguWebSearch =
    angular.module('ebeguWeb.search', [EbeguWebCore.name])
        .run(searchRun)
        .component('searchListView', new SearchListViewComponentConfig());
