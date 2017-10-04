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

import {EbeguWebCore} from '../core/core.module';
import {PendenzenInstitutionListViewComponentConfig} from './component/pendenzenInstitutionListView/pendenzenInstitutionListView';
import {pendenzRun} from './pendenzenInstitution.route';
import PendenzInstitutionRS from './service/PendenzInstitutionRS.rest';
import {PendenzInstitutionFilter} from './filter/pendenzInstitutionFilter';

export const EbeguWebPendenzenInstitution =
    angular.module('ebeguWeb.pendenzenInstitution', [EbeguWebCore.name])
        .run(pendenzRun)
        .service('PendenzInstitutionRS', PendenzInstitutionRS)
        .filter('pendenzInstitutionFilter', PendenzInstitutionFilter)
        .component('pendenzenInstitutionListView', new PendenzenInstitutionListViewComponentConfig());
