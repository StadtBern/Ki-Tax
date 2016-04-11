import 'angular';
import './core.module.less';
import {appRun} from './core.route';
import {configure} from './config';
import router from '../dvbModules/router/router.module';
import AdresseRS from './service/adresseRS';
import ListResourceRS from './service/listResourceRS';
import EbeguRestUtil from '../utils/EbeguRestUtil';
import PersonRS from './service/personRS.rest';

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
    .service('ListResourceRS', ListResourceRS);
