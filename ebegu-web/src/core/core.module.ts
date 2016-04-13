import 'angular';
import './core.module.less';
import {appRun} from './core.route';
import {configure} from './config';
import router from '../dvbModules/router/router.module';
import AdresseRS from './service/adresseRS.rest';
import ListResourceRS from './service/listResourceRS.rest';
import EbeguRestUtil from '../utils/EbeguRestUtil';
import PersonRS from './service/personRS.rest';
import {AdresseComponentConfig} from './component/dv-adresse/dv-adresse';
import {DvErrorMessagesComponentConfig} from './component/dv-error-messages/dv-error-messages';
import FallRS from '../gesuch/service/fallRS.rest';
import GesuchForm from '../gesuch/service/gesuchForm';
import GesuchRS from '../gesuch/service/gesuchRS.rest';
import FamiliensituationRS from '../gesuch/service/familiensituationRS.rest';

export const EbeguWebCore = angular
    .module('ebeguWeb.core', [
        /* Angular modules */
        'ngAnimate',
        'ngSanitize',
        'ngMessages',
        'ngAria',
        'ngCookies',
        /* shared DVBern modules */
        router.name,
        /* 3rd-party modules */
        'ui.bootstrap',
        'smart-table',
        'pascalprecht.translate',
        'angularMoment'
    ])
    .run(appRun)
    .config(configure)
    .constant('REST_API', '/ebegu/api/v1/')
    .constant('MAX_LENGTH', 255)
    .constant('CONFIG', {
        name: 'EBEGU',
        REST_API: '/ebegu/api/v1/'
    })
    .service('EbeguRestUtil', EbeguRestUtil)
    .service('PersonRS', PersonRS)
    .service('AdresseRS', AdresseRS)
    .service('ListResourceRS', ListResourceRS)
    .service('fallRS', FallRS)
    .service('familiensituationRS', FamiliensituationRS)
    .service('gesuchForm', GesuchForm)
    .service('gesuchRS', GesuchRS)
    .component('dvAdresse', new AdresseComponentConfig())
    .component('dvErrorMessages', new DvErrorMessagesComponentConfig());

