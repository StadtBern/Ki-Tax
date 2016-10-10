import {EbeguWebCore} from '../core/core.module';
import {GesuchstellerDashboardListViewConfig} from './component/gesuchstellerDashboardView';
import {gesuchstellerDashboardRun} from './gesuchstellerDashboard.route';

export const EbeguWebGesuchstellerDashboard =
    angular.module('ebeguWeb.gesuchstellerDashboard', [EbeguWebCore.name])
        .run(gesuchstellerDashboardRun)
        .component('gesuchstellerDashboardView', new GesuchstellerDashboardListViewConfig());
