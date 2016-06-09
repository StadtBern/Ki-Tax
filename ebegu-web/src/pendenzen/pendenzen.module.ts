import {EbeguWebCore} from '../core/core.module';
import {PendenzenListViewComponentConfig} from './component/pendenzenListView/pendenzenListView';
import {pendenzRun} from './pendenzen.route';
import PendenzRS from './service/PendenzRS.rest';
import {PendenzFilter} from './filter/pendenzFilter';

export const EbeguWebPendenzen =
    angular.module('ebeguWeb.pendenzen', [EbeguWebCore.name])
        .run(pendenzRun)
        .service('PendenzRS', PendenzRS)
        .filter('pendenzFilter', PendenzFilter)
        .component('pendenzenListView', new PendenzenListViewComponentConfig());
