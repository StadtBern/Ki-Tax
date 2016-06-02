import {EbeguWebCore} from '../core/core.module';
import {PendenzenListViewComponentConfig} from './component/pendenzenListView/pendenzenListView';
import {pendenzenRun} from './pendenzen.route';

export const EbeguWebPendenzen =
    angular.module('ebeguWeb.pendenzen', [EbeguWebCore.name])
        .run(pendenzenRun)
        .component('pendenzenListView', new PendenzenListViewComponentConfig());
