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
