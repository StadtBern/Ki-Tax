import HttpAuthInterceptor from './service/HttpAuthInterceptor';
import {DummyAuthenticationComponentConfig} from './dummyAuthenticaton';
import {authenticationRun} from './authentication.route';
import AuthServiceRS from './service/AuthServiceRS.rest';
import HttpBuffer from './service/HttpBuffer';
import {AuthenticationComponentConfig} from './authenticaton';
import {StartComponentConfig} from './component/startView/startView';
import {SchulungComponentConfig} from './schulung';

export const EbeguAuthentication: angular.IModule =
    angular.module('dvbAngular.authentication', ['ngCookies', 'utf8-base64'])
        .run(authenticationRun)
        .service('HttpAuthInterceptor', HttpAuthInterceptor)
        .service('AuthServiceRS', AuthServiceRS)
        .service('httpBuffer', HttpBuffer)
        .component('startView', new StartComponentConfig())
        .component('dummyAuthenticationView', new DummyAuthenticationComponentConfig())
        .component('schulungView', new SchulungComponentConfig())
        .component('authenticationView', new AuthenticationComponentConfig());
