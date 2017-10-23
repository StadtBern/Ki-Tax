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
import {HashedModuleIdsPlugin} from 'webpack';
import * as webpackMerge from 'webpack-merge';
import commonConfig, {METADATA} from './webpack.common';

/**
 * @author: @AngularClass
 */

/**
 * Webpack Plugins
 */
const CopyWebpackPlugin = require('copy-webpack-plugin');
const DefinePlugin = require('webpack/lib/DefinePlugin');
const UglifyJsPlugin = require('webpack/lib/optimize/UglifyJsPlugin');
const LoaderOptionsPlugin = require('webpack/lib/LoaderOptionsPlugin');
const CompressionPlugin = require('compression-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const BundleAnalyzer = require('webpack-bundle-analyzer');

/**
 * Webpack Constants
 */
const ENV = process.env.NODE_ENV = process.env.ENV = 'production';
const HOST = process.env.HOST || 'localhost';
const PORT = process.env.PORT || 80;
METADATA.ENV = ENV;
METADATA.HOST = HOST;
METADATA.PORT = PORT;

export default (env: any): webpack.Configuration => webpackMerge(commonConfig(env), {
    devtool: 'source-map',
    output: {
        // Specifies the name of each output file on disk.
        // IMPORTANT: You must not specify an absolute path here!
        //
        // See: https://webpack.js.org/configuration/output/#output-filename
        filename: '[name].[chunkhash].bundle.js',

        // The filename of the SourceMaps for the JavaScript files.
        // They are inside the output.path directory.
        //
        // See: https://webpack.js.org/configuration/output/#output-sourcemapfilename
        sourceMapFilename: '[file].map',

        // The filename of non-entry chunks as relative path
        // inside the output.path directory.
        //
        // See: https://webpack.js.org/configuration/output/#output-chunkfilename
        chunkFilename: '[id].[chunkhash].chunk.js',

    },
    plugins: [
        // Sets these variables on each loader.
        new LoaderOptionsPlugin({
            debug: false,
            minimize: true
        }),

        new ExtractTextPlugin('[name].[hash].css'),

        // Cause hashes to be based on the relative path of the module, generating a string as the module id.
        new HashedModuleIdsPlugin({
            hashFunction: 'md5',
            hashDigest: 'base64',
            hashDigestLength: 4,
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
        }),

        // Create bundle report
        // See https://github.com/th0r/webpack-bundle-analyzer
        new BundleAnalyzer.BundleAnalyzerPlugin({
            analyzerMode: 'static',
            openAnalyzer: false,
            reportFilename: 'bundle-report.html',
        }),

        // Minimize all JavaScript output of chunks.
        new UglifyJsPlugin({
            beautify: false,
            mangle: {
                screw_ie8: true,
                keep_fnames: true
            },
            compress: {
                screw_ie8: true,
                // warnings are from vendor libraries that are safely to ignore
                warnings: false,
            },
            comments: false,
            sourceMap: true,
        }),

        // Plugin: CompressionPlugin
        // Description: Prepares compressed versions of assets to serve
        // them with Content-Encoding
        //
        // See: https://github.com/webpack/compression-webpack-plugin
        new CompressionPlugin({
            regExp: /\.css$|\.html$|\.js$|\.map$/,
            threshold: 2 * 1024
        }),
        //copy swagger-ui to dist
        new CopyWebpackPlugin([
            {from: 'node_modules/swagger-ui/dist/', to: 'swagger-ui/'}
        ])

    ],
});
