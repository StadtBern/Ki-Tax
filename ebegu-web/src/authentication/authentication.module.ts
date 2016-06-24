import HttpAuthInterceptor from './service/HttpAuthInterceptor';
import {AuthenticationComponentConfig} from './dummyAuthenticaton';
import {authenticationRun} from './authentication.route';
import AuthServiceRS from './service/AuthServiceRS.rest';
import HttpBuffer from './service/HttpBuffer';

export const EbeguAuthentication: angular.IModule =
    angular.module('dvbAngular.authentication', ['ngCookies', 'utf8-base64'])
        .run(authenticationRun)
        .service('HttpAuthInterceptor', HttpAuthInterceptor)
        .service('AuthServiceRS', AuthServiceRS)
        .service('httpBuffer', HttpBuffer)
        .component('dummyAuthenticationView', new AuthenticationComponentConfig());
