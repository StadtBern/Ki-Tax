import {EbeguWebCore} from '../core/core.module';
import {StatistikViewComponentConfig} from './component/statistikView/statistikView';
import {statistikRun} from './statistik.route';

export const EbeguWebStatistik =
    angular.module('ebeguWeb.statistik', [EbeguWebCore.name])
        .run(statistikRun)
        .component('statistikView', new StatistikViewComponentConfig());
