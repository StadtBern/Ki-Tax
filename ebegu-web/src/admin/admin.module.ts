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
import 'angular-smart-table';
import {EbeguWebCore} from '../core/core.module';
import {InstitutionRS} from '../core/service/institutionRS.rest';
import './admin.module.less';
import {adminRun} from './admin.route';
import {AdminViewComponentConfig} from './component/adminView/adminView';
import {InstitutionenListViewComponentConfig} from './component/institutionenListView/institutionenListView';
import {InstitutionStammdatenViewComponentConfig} from './component/institutionStammdatenView/institutionStammdatenView';
import {InstitutionViewComponentConfig} from './component/institutionView/institutionView';
import {ParameterViewComponentConfig} from './component/parameterView/parameterView';
import {TraegerschaftViewComponentConfig} from './component/traegerschaftView/traegerschaftView';
import {ApplicationPropertyRS} from './service/applicationPropertyRS.rest';
import {EbeguParameterRS} from './service/ebeguParameterRS.rest';
import {EbeguVorlageRS} from './service/ebeguVorlageRS.rest';
import {ReindexRS} from './service/reindexRS.rest';
import {TestFaelleRS} from './service/testFaelleRS.rest';
import {TestdatenViewComponentConfig} from './component/testdatenView/testdatenView';
import {FerieninselStammdatenRS} from './service/ferieninselStammdatenRS.rest';
import {FerieninselViewComponentConfig} from './component/ferieninselView/ferieninselView';
import {DailyBatchRS} from './service/dailyBatchRS.rest';

export const EbeguWebAdmin = angular.module('ebeguWeb.admin', [EbeguWebCore.name, 'smart-table'])
    .service('ApplicationPropertyRS', ApplicationPropertyRS)
    .service('InstitutionRS', InstitutionRS)
    .service('EbeguParameterRS', EbeguParameterRS)
    .service('EbeguVorlageRS', EbeguVorlageRS)
    .service('ReindexRS', ReindexRS)
    .service('TestFaelleRS', TestFaelleRS)
    .service('DailyBatchRS', DailyBatchRS)
    .service('FerieninselStammdatenRS', FerieninselStammdatenRS)
    .component('dvAdminView', new AdminViewComponentConfig())
    .component('dvInstitutionenListView', new InstitutionenListViewComponentConfig())
    .component('dvInstitutionView', new InstitutionViewComponentConfig())
    .component('dvInstitutionStammdatenView', new InstitutionStammdatenViewComponentConfig())
    .component('dvParameterView', new ParameterViewComponentConfig())
    .component('dvTraegerschaftView', new TraegerschaftViewComponentConfig())
    .component('dvTestdatenView', new TestdatenViewComponentConfig())
    .component('dvFerieninselView', new FerieninselViewComponentConfig())
    .run(adminRun);

export default EbeguWebAdmin;
