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

var webpack = require('webpack');
var helpers = require('./helpers');
var webpackMerge = require('webpack-merge'); //Used to merge webpack configs
var commonConfig = require('./webpack.common.js'); //The settings that are common to prod and dev

/**
 * Webpack Plugins
 */
const DefinePlugin = require('webpack/lib/DefinePlugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const LoaderOptionsPlugin = require("webpack/lib/LoaderOptionsPlugin");

/**
 * Webpack Constants
 */
const ENV = process.env.ENV = process.env.NODE_ENV = 'development';
const HMR = helpers.hasProcessFlag('hot');
//* to allow everybody to access the server
const METADATA = {
    title: 'ebegu Webpack',
    baseUrl: '/',
    host: '0.0.0.0',
    port: 3000,
    ENV: ENV,
    HMR: HMR
};

module.exports = webpackMerge(commonConfig, {

    // Developer tool to enhance debugging
    //
    // See: http://webpack.github.io/docs/configuration.html#devtool
    // See: https://github.com/webpack/docs/wiki/build-performance#sourcemaps
    devtool: 'cheap-module-eval-source-map',

    output: {
        // The output directory as absolute path (required).
        //
        // See: http://webpack.github.io/docs/configuration.html#output-path
        path: helpers.root('dist'),

        // Specifies the name of each output file on disk.
        // IMPORTANT: You must not specify an absolute path here!
        //
        // See: http://webpack.github.io/docs/configuration.html#output-filename
        filename: '[name].bundle.js',

        // The filename of the SourceMaps for the JavaScript files.
        // They are inside the output.path directory.
        //
        // See: http://webpack.github.io/docs/configuration.html#output-sourcemapfilename
        sourceMapFilename: '[name].map',

        // The filename of non-entry chunks as relative path
        // inside the output.path directory.
        //
        // See: http://webpack.github.io/docs/configuration.html#output-chunkfilename
        chunkFilename: '[id].chunk.js'

    },

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

    // Static analysis linter for TypeScript advanced options configuration
    // Description: An extensible linter for the TypeScript language.
    //
    // See: https://github.com/wbuchwalter/tslint-loader
    // tslint: {
    //     emitErrors: false,
    //     failOnHint: false,
    //     resourcePath: 'src'
    // },

    // Webpack Development Server configuration
    // Description: The webpack-dev-server is a little node.js Express server.
    // The server emits information about the compilation state to the client,
    // which reacts to those events.
    //
    // See: https://webpack.github.io/docs/webpack-dev-server.html
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
                secure: false,
                proxyTimeout: 240000
            },
            '/connector': {
                target: 'http://localhost:8080',
                secure: false,
                proxyTimeout: 240000
            }
        },
        headers: {
                   'Content-Security-Policy': "default-src 'none'; object-src 'self'; script-src 'self' 'unsafe-eval' ajax.googleapis.com; connect-src 'self' ws:; img-src 'self'; style-src 'self' 'unsafe-inline'  https://fonts.googleapis.com; font-src 'self'  https://fonts.googleapis.com https://fonts.gstatic.com;",
                   'X-Frame-Options': 'DENY'
               }
    },
    node: {
        global: true,
        crypto: 'empty',
        process: true,
        module: false,
        clearImmediate: false,
        setImmediate: false
    }
});
