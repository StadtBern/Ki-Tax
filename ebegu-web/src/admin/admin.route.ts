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

import {IState} from 'angular-ui-router';
import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {ApplicationPropertyRS} from './service/applicationPropertyRS.rest';
import {InstitutionRS} from '../core/service/institutionRS.rest';
import {TraegerschaftRS} from '../core/service/traegerschaftRS.rest';
import {MandantRS} from '../core/service/mandantRS.rest';

adminRun.$inject = ['RouterHelper'];

/* @ngInject */
export function adminRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates());
}

function getStates(): IState[] {
    return [
        {
            name: 'admin',
            template: '<dv-admin-view flex="auto" class="overflow-scroll" application-properties="$resolve.applicationProperties"></dv-admin-view>',
            url: '/admin',
            resolve: {
                applicationProperties: getApplicationProperties
            }
        },
        {
            name: 'testdaten',
            template: '<dv-testdaten-view flex="auto" class="overflow-scroll"></dv-testdaten-view>',
            url: '/testdaten'
        },
        {
            name: 'institution',
            template: '<dv-institution-view flex="auto" class="overflow-scroll" institutionen="$resolve.institutionen" ' +
            'traegerschaften="$resolve.traegerschaften" mandant="$resolve.mandant"></dv-institution-view>',
            url: '/institution',
            resolve: {
                institutionen: getInstitutionen,
                traegerschaften: getTraegerschaften,
                mandant: getMandant
            }
        },
        {
            name: 'parameter',
            template: '<dv-parameter-view flex="auto" class="overflow-scroll" ebeguParameter="vm.ebeguParameter"></dv-parameter-view>',
            url: '/parameter',
        },
        {
            name: 'ferieninsel',
            template: '<dv-ferieninsel-view flex="auto" class="overflow-scroll"></dv-ferieninsel-view>',
            url: '/ferieninsel',
        },
        {
            name: 'traegerschaft',
            template: '<dv-traegerschaft-view flex="auto" class="overflow-scroll" traegerschaften="$resolve.traegerschaften" ></dv-traegerschaft-view>',
            url: '/traegerschaft',
            resolve: {
                traegerschaften: getTraegerschaften,
            }
        }
    ];
}

// FIXME dieses $inject wird ignoriert, d.h, der Parameter der Funktion muss exact dem Namen des Services entsprechen (Grossbuchstaben am Anfang). Warum?
getApplicationProperties.$inject = ['ApplicationPropertyRS'];

/* @ngInject */
function getApplicationProperties(ApplicationPropertyRS: ApplicationPropertyRS) {
    return ApplicationPropertyRS.getAllApplicationProperties();
}

// FIXME dieses $inject wird ignoriert, d.h, der Parameter der Funktion muss exact dem Namen des Services entsprechen (Grossbuchstaben am Anfang). Warum?
getInstitutionen.$inject = ['InstitutionRS'];

/* @ngInject */
function getInstitutionen(InstitutionRS: InstitutionRS) {
    return InstitutionRS.getAllActiveInstitutionen();
}

// FIXME dieses $inject wird ignoriert, d.h, der Parameter der Funktion muss exact dem Namen des Services entsprechen (Grossbuchstaben am Anfang). Warum?
getTraegerschaften.$inject = ['TraegerschaftRS'];

/* @ngInject */
function getTraegerschaften(TraegerschaftRS: TraegerschaftRS) {
    return TraegerschaftRS.getAllActiveTraegerschaften();
}

// FIXME dieses $inject wird ignoriert, d.h, der Parameter der Funktion muss exact dem Namen des Services entsprechen (Grossbuchstaben am Anfang). Warum?
getMandant.$inject = ['MandantRS'];

/* @ngInject */
function getMandant(MandantRS: MandantRS) {
    return MandantRS.getFirst();
}
