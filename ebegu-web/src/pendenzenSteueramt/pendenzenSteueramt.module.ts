import {EbeguWebCore} from '../core/core.module';
import {pendenzRun} from './pendenzenSteueramt.route';
import {PendenzenSteueramtListViewComponentConfig} from './component/pendenzenSteueramtListView/pendenzenSteueramtListView';
import PendenzSteueramtRS from './service/pendenzSteueramtRS.rest';

export const EbeguWebPendenzenSteueramt =
    angular.module('ebeguWeb.pendenzenSteueramt', [EbeguWebCore.name])
        .run(pendenzRun)
        .component('pendenzenSteueramtListView', new PendenzenSteueramtListViewComponentConfig())
        .service('PendenzSteueramtRS', PendenzSteueramtRS);
