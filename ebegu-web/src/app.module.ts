import 'angular';
import './app.module.less';
import {EbeguWebCore} from './core/core.module';
import {EbeguWebAdmin} from './admin/admin.module';
import {EbeguWebGesuch} from './gesuch/gesuch.module';
import {EbeguWebPendenzen} from './pendenzen/pendenzen.module';
import {EbeguWebPendenzenInstitution} from './pendenzenInstitution/pendenzenInstitution.module';
import {EbeguWebFaelle} from './faelle/faelle.module';
import {EbeguWebStatistik} from './statistik/statistik.module';
import {EbeguWebGesuchstellerDashboard} from './gesuchstellerDashboard/gesuchstellerDashboard.module';
import {EbeguWebMitteilungen} from './mitteilungen/mitteilungen.module';
import {EbeguWebVerlauf} from './verlauf/verlauf.module';
import {EbeguWebPosteingang} from './posteingang/posteingang.module';
import {EbeguWebSearch} from './searchResult/search.module';
import {EbeguWebZahlung} from './zahlung/zahlung.module';
import {EbeguWebZahlungsauftrag} from './zahlungsauftrag/zahlungsauftrag.module';

export default angular.module('ebeguWeb', [EbeguWebCore.name, EbeguWebAdmin.name, EbeguWebGesuch.name, EbeguWebPendenzen.name,
    EbeguWebPendenzenInstitution.name, EbeguWebFaelle.name, EbeguWebGesuchstellerDashboard.name, EbeguWebMitteilungen.name,
    EbeguWebPosteingang.name, EbeguWebSearch.name,  EbeguWebStatistik.name, EbeguWebZahlung.name, EbeguWebZahlungsauftrag.name, EbeguWebVerlauf.name]);
