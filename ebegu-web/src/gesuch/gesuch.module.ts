import 'angular';
import './gesuch.module.less';
import {EbeguWebCore} from '../core/core.module';
import {gesuchRun} from './gesuch.route';
import {StammdatenViewComponentConfig} from './component/stammdatenView/stammdatenView';
import {FamiliensituationViewComponentConfig} from './component/familiensituationView/familiensituationView';
import {KinderViewComponentConfig} from './component/kinderView/kinderView';
import {FinanzielleSituationViewComponentConfig} from './component/finanzielleSituationView/finanzielleSituationView';

export const EbeguWebGesuch = angular.module('ebeguWeb.gesuch', [EbeguWebCore.name])
    .run(gesuchRun)
    .component('familiensituationView', new FamiliensituationViewComponentConfig())
    .component('stammdatenView', new StammdatenViewComponentConfig)
    .component('kinderView', new KinderViewComponentConfig())
    .component('finanzielleSituationView', new FinanzielleSituationViewComponentConfig());

