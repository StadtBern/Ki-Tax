import {EbeguWebCore} from '../core/core.module';
import {PendenzenListViewComponentConfig} from './component/pendenzenListView/pendenzenListView';
import {pendenzRun} from './pendenzen.route';
import PendenzRS from './service/PendenzRS.rest';

export const EbeguWebPendenzen =
    angular.module('ebeguWeb.pendenzen', [EbeguWebCore.name])
        .run(pendenzRun)
        .service('PendenzRS', PendenzRS)
        .component('pendenzenListView', new PendenzenListViewComponentConfig());
