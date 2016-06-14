import HttpAuthInterceptor from './service/HttpAuthInterceptor';
import {AuthenticationComponentConfig} from './dummyAuthenticaton';
import {authenticationRun} from './authentication.route';

export const EbeguAuthentication: angular.IModule =
    angular.module('dvbAngular.authentication', [])
        .run(authenticationRun)
        .service('HttpAuthInterceptor', HttpAuthInterceptor)
        .component('dummyAuthenticationView', new AuthenticationComponentConfig());
