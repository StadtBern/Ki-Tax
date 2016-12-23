import 'angular';
import './gesuch.module.less';
import {EbeguWebCore} from '../core/core.module';
import {gesuchRun} from './gesuch.route';
import {StammdatenViewComponentConfig} from './component/stammdatenView/stammdatenView';
import {FamiliensituationViewComponentConfig} from './component/familiensituationView/familiensituationView';
import {KinderListViewComponentConfig} from './component/kinderListView/kinderListView';
import {FinanzielleSituationViewComponentConfig} from './component/finanzielleSituationView/finanzielleSituationView';
import {KindViewComponentConfig} from './component/kindView/kindView';
import {BetreuungListViewComponentConfig} from './component/betreuungListView/betreuungListView';
import {BetreuungViewComponentConfig} from './component/betreuungView/betreuungView';
import {ErwerbspensumListViewComponentConfig} from './component/erwerbspensumListView/erwerbspensumListView';
import {ErwerbspensumViewComponentConfig} from './component/erwerbspensumView/erwerbspensumView';
import {FinanzielleSituationStartViewComponentConfig} from './component/finanzielleSituationStartView/finanzielleSituationStartView';
import {FinanzielleSituationResultateViewComponentConfig} from './component/finanzielleSituationResultateView/finanzielleSituationResultateView';
import {FallCreationViewComponentConfig} from './component/fallCreationView/fallCreationView';
import {VerfuegenListViewComponentConfig} from './component/verfuegenListView/verfuegenListView';
import {
    GesuchToolbarComponentConfig,
    GesuchToolbarGesuchstellerComponentConfig
} from './component/gesuchToolbar/gesuchToolbar';
import {EinkommensverschlechterungInfoViewComponentConfig} from './component/einkommensverschlechterungInfoView/einkommensverschlechterungInfoView';
import {EinkommensverschlechterungSteuernViewComponentConfig} from './component/einkommensverschlechterungSteuernView/einkommensverschlechterungSteuernView';
import {EinkommensverschlechterungViewComponentConfig} from './component/einkommensverschlechterungView/einkommensverschlechterungView';
import {EinkommensverschlechterungResultateViewComponentConfig} from './component/einkommensverschlechterungResultateView/einkommensverschlechterungResultateView';
import {DokumenteViewComponentConfig} from './component/DokumenteView/dokumenteView';
import {VerfuegenViewComponentConfig} from './component/verfuegenView/verfuegenView';
import {KommentarViewComponentConfig} from './component/kommentarView/kommentarView';
import {UmzugViewComponentConfig} from './component/umzugView/umzugView';
import {AbwesenheitViewComponentConfig} from './component/abwesenheitView/abwesenheitView';
import {FreigabeViewComponentConfig} from './component/freigabeView/freigabeView';

export const EbeguWebGesuch =
    angular.module('ebeguWeb.gesuch', [EbeguWebCore.name])
        .run(gesuchRun)
        .component('familiensituationView', new FamiliensituationViewComponentConfig())
        .component('stammdatenView', new StammdatenViewComponentConfig)
        .component('umzugView', new UmzugViewComponentConfig())
        .component('kinderListView', new KinderListViewComponentConfig())
        .component('finanzielleSituationView', new FinanzielleSituationViewComponentConfig())
        .component('finanzielleSituationStartView', new FinanzielleSituationStartViewComponentConfig())
        .component('finanzielleSituationResultateView', new FinanzielleSituationResultateViewComponentConfig())
        .component('kindView', new KindViewComponentConfig())
        .component('betreuungListView', new BetreuungListViewComponentConfig())
        .component('betreuungView', new BetreuungViewComponentConfig())
        .component('abwesenheitView', new AbwesenheitViewComponentConfig())
        .component('erwerbspensumListView', new ErwerbspensumListViewComponentConfig())
        .component('erwerbspensumView', new ErwerbspensumViewComponentConfig())
        .component('fallCreationView', new FallCreationViewComponentConfig())
        .component('verfuegenListView', new VerfuegenListViewComponentConfig())
        .component('verfuegenView', new VerfuegenViewComponentConfig())
        .component('gesuchToolbar', new GesuchToolbarComponentConfig())
        .component('gesuchToolbarGesuchsteller', new GesuchToolbarGesuchstellerComponentConfig())
        .component('einkommensverschlechterungInfoView', new EinkommensverschlechterungInfoViewComponentConfig())
        .component('einkommensverschlechterungSteuernView', new EinkommensverschlechterungSteuernViewComponentConfig())
        .component('einkommensverschlechterungView', new EinkommensverschlechterungViewComponentConfig())
        .component('einkommensverschlechterungResultateView', new EinkommensverschlechterungResultateViewComponentConfig())
        .component('freigabeView', new FreigabeViewComponentConfig())
        .component('dokumenteView', new DokumenteViewComponentConfig())
        .component('kommentarView', new KommentarViewComponentConfig());
