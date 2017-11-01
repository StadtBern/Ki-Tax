/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
import {EbeguWebAlleVerfuegungen} from './alleVerfuegungen/alleVerfuegungen.module';
import {EbeguWebPendenzenSteueramt} from './pendenzenSteueramt/pendenzenSteueramt.module';
import {EbeguWebQuicksearch} from './quicksearch/quicksearch.module';
import './style/mediaqueries.less';

export default angular.module('ebeguWeb', [EbeguWebCore.name, EbeguWebAdmin.name, EbeguWebGesuch.name, EbeguWebPendenzen.name,
    EbeguWebPendenzenInstitution.name, EbeguWebPendenzenSteueramt.name, EbeguWebFaelle.name, EbeguWebGesuchstellerDashboard.name,
    EbeguWebMitteilungen.name, EbeguWebPosteingang.name, EbeguWebSearch.name, EbeguWebStatistik.name, EbeguWebZahlung.name,
    EbeguWebZahlungsauftrag.name, EbeguWebAlleVerfuegungen.name, EbeguWebVerlauf.name, EbeguWebQuicksearch.name]);
