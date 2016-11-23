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
