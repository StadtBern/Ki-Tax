import 'angular';
import './core.module.less';
// import {appRun} from './core.route';
import {configure} from './config';
// import router from '../dvbModules/router/router.module';

export default angular
    .module('ebeguWeb.core', [
        /* Angular modules */
        'ngAnimate',
        'ngSanitize',
        'ngMessages',
        'ngAria',
        'ngCookies',
        /* shared DVBern modules */
        // router.name,
        /* 3rd-party modules */
        'ui.bootstrap',
        'smart-table',
        'pascalprecht.translate',
        'angularMoment'
    ])
    // .run(appRun)
    .config(configure);
