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

import * as webpack from 'webpack';
import dev from './config/webpack.dev';
import prod from './config/webpack.prod';
import test from './config/webpack.test';

// noinspection JSUnusedGlobalSymbols
export default (env: string): webpack.Configuration => {

    let config: webpack.Configuration | null = null;

    switch (env) {
        case 'prod':
        case 'production':
            console.log('*** Using production config\n\n');
            config = prod('production');
            break;
        case 'test':
        case 'testing':
            console.log('*** Using testing config\n\n');
            config = test('test');
            break;
        case 'dev':
        case 'development':
        default:
            console.log('*** Using development config\n\n');
            config = dev('development');
    }

    // Output generated config
    // console.log(util.inspect(config, false, null));

    return config;
};
