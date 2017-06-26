import {EbeguWebCore} from '../core/core.module';
import {VerlaufViewComponentConfig} from './component/verlaufView/verlaufView';
import {verlaufRun} from './verlauf.route';

export const EbeguWebVerlauf =
    angular.module('ebeguWeb.verlauf', [EbeguWebCore.name])
        .run(verlaufRun)
        .component('verlaufView', new VerlaufViewComponentConfig());
