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
