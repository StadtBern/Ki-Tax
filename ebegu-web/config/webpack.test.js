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

var helpers = require('./helpers');
var commonConfig = require('./webpack.common.js'); //The settings that are common to prod and dev
var webpackMerge = require('webpack-merge'); //Used to merge webpack configs

/**
 * Webpack Plugins
 */
var ProvidePlugin = require('webpack/lib/ProvidePlugin');
var DefinePlugin = require('webpack/lib/DefinePlugin');

/**
 * Webpack Constants
 */
const HOST = process.env.HOST || 'localhost';
const PORT = process.env.PORT || 80;
const ENV = process.env.ENV = process.env.NODE_ENV = 'test';
const METADATA = {
    host: HOST,
    port: PORT,
    ENV: ENV,
    HMR: false
};

/**
 * Webpack configuration
 *
 * See: http://webpack.github.io/docs/configuration.html#cli
 */
module.exports = {
    devtool: 'inline-source-map',
    entry: {
        // 'polyfills': './src/polyfills.ts',
        'vendor': './src/vendor.ts',
        'main': './src/bootstrap.ts'
    },

    resolve: {
        extensions: ['.ts', '.js']
    },

    module: {
        rules: [
            {
                // Static analysis linter for TypeScript advanced options configuration
                // Description: An extensible linter for the TypeScript language.
                //
                // See: https://github.com/wbuchwalter/tslint-loader
                test: /\.ts$/,
                enforce: 'pre',
                loader: 'tslint-loader',
                options: {

                    // can specify a custom config file relative to current directory or with absolute path
                    // 'tslint-custom.json'
                    configFile: 'tslint.json',

                    // tslint errors are displayed by default as warnings
                    // set emitErrors to true to display them as errors
                    emitErrors: false,

                    // tslint does not interrupt the compilation by default
                    // if you want any file with tslint errors to fail
                    // set failOnHint to true
                    failOnHint: true,

                    // enables type checked rules like 'for-in-array'
                    // uses tsconfig.json from current working directory
                    typeCheck: false,

                    // automatically fix linting errors
                    fix: false,

                    // // can specify a custom tsconfig file relative to current directory or with absolute path
                    // // to be used with type checked rules
                    // tsConfigFile: '../src/tsconfig.json'
                }
            },

            {
                test: /\.ts$/,
                loaders: [
                    {
                        loader: 'awesome-typescript-loader',
                        options: {configFileName: helpers.root('src', 'tsconfig.json')}
                    }
                ]
            },
            {
                test: /\.html$/,
                loader: 'html-loader'

            },
            {
                test: /\.(png|jpe?g|gif|svg|woff|woff2|ttf|eot|ico)$/,
                loader: 'null-loader'
            },
            {
                test: /\.json$/,
                use: 'json-loader',
                include: helpers.root('src', 'assets')
            },
            {
                test: /\.(scss|less)$/,
                loader: 'null-loader'
            },
            {
                test: /\.css$/,
                exclude: helpers.root('src', 'app'),
                loader: 'null-loader'
            },
            {
                test: /\.css$/,
                include: helpers.root('src', 'app'),
                loader: 'raw-loader'
            }
        ]
    },

    plugins: [
        new DefinePlugin({
            'ENV': JSON.stringify(ENV),
            'HMR': false,
            'VERSION': JSON.stringify(METADATA.version),
            'BUILDTSTAMP': JSON.stringify(METADATA.buildtstamp),
            'process.env': {
                'ENV': JSON.stringify(ENV),
                'NODE_ENV': JSON.stringify(ENV),
                'HMR': false,
            }
        }),

    ],

    target: 'node',//wegen moment
    // Include polyfills or mocks for various node stuff
    // Description: Node configuration
    //
    // See: https://webpack.github.io/docs/configuration.html#node
    node: {
        global: true,
        process: false,
        crypto: 'empty',
        module: false,
        clearImmediate: false,
        setImmediate: false
    }

};
