import 'angular';
import './gesuch.module.less';
import {EbeguWebCore} from '../core/core.module';
import {gesuchRun} from './gesuch.route';
import {StammdatenViewComponentConfig} from './component/stammdatenView/stammdatenView';
import {FamiliensituationViewComponentConfig} from './component/familiensituationView/familiensituationView';

export const EbeguWebGesuch = angular.module('ebeguWeb.gesuch', [EbeguWebCore.name])
    .run(gesuchRun)
    .component('familiensituationView', new FamiliensituationViewComponentConfig())
    .component('stammdatenView', new StammdatenViewComponentConfig);
