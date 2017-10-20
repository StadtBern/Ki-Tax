import * as webpack from 'webpack';
import * as webpackMerge from 'webpack-merge';
import {hasProcessFlag} from './helpers';
import commonConfig from './webpack.common';
import ExtractTextPlugin = require('extract-text-webpack-plugin');
import DefinePlugin = require('webpack/lib/DefinePlugin');
import LoaderOptionsPlugin = require('webpack/lib/LoaderOptionsPlugin');

/**
 * Webpack Constants
 */
const ENV = process.env.ENV = process.env.NODE_ENV = 'development';
const HMR = hasProcessFlag('hot');
//* to allow everybody to access the server
const METADATA = {
    title: 'ebegu Webpack',
    baseUrl: '/',
    host: '0.0.0.0',
    port: 3000,
    ENV: ENV,
    HMR: HMR,
    buildtstamp: {}
};

export default (env: any): webpack.Configuration => webpackMerge(commonConfig(env), {

    devtool: 'cheap-module-eval-source-map',
    plugins: [
        new LoaderOptionsPlugin({
            debug: true
        }),
        new ExtractTextPlugin({
            filename: '[name].css'
        }),

        // Plugin: DefinePlugin
        // Description: Define free variables.
        // Useful for having development builds with debug logging or adding global constants.
        //
        // Environment helpers
        //
        // See: https://webpack.github.io/docs/list-of-plugins.html#defineplugin
        // NOTE: when adding more properties make sure you include them in custom-typings.d.ts
        new DefinePlugin({
            'ENV': JSON.stringify(METADATA.ENV),
            'HMR': METADATA.HMR,
            // 'VERSION': JSON.stringify(METADATA.version),
            'BUILDTSTAMP': JSON.stringify(METADATA.buildtstamp),
            'process.env': {
                'ENV': JSON.stringify(METADATA.ENV),
                'NODE_ENV': JSON.stringify(METADATA.ENV),
                'HMR': METADATA.HMR,
            }
        })
    ],
    devServer: {
        port: METADATA.port,
        host: METADATA.host,
        disableHostCheck: true,
        historyApiFallback: true,
        watchOptions: {
            aggregateTimeout: 300,
            poll: 1000
        },
        proxy: {
            '/ebegu': {
                target: 'http://localhost:8080',
                secure: false
            }
        }
    }
});
