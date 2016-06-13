import HttpErrorInterceptor from './service/HttpErrorInterceptor';
import ErrorService from './service/ErrorService';
import {DvErrorMessagesPanelComponentConfig} from './directive/dvb-error-messages/dvb-error-messages-panel';
let style = require('./errors.less');

export const EbeguErrors: angular.IModule = angular.module('dvbAngular.errors', ['ui.bootstrap', 'ui.router', 'ngAnimate'])
    .service('ErrorService', ErrorService)
    .service('HttpErrorInterceptor', HttpErrorInterceptor)
    .component('dvbErrorMessagesPanel', new DvErrorMessagesPanelComponentConfig());

