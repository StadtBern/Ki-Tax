import * as webpack from 'webpack';
import {ProvidePlugin} from 'webpack';
import * as merge from 'webpack-merge';
import {root} from './helpers';
import commonConfig, {mainChunk, vendorChunk} from './webpack.common';
import ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');
import DefinePlugin = require('webpack/lib/DefinePlugin');
import CommonsChunkPlugin = webpack.optimize.CommonsChunkPlugin;

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
    HMR: false,
    version: undefined,
    buildtstamp: undefined
};

/**
 * Webpack configuration
 *
 * See: http://webpack.github.io/docs/configuration.html#cli
 */
export default (env: string): webpack.Configuration => merge.smartStrategy({
    entry: 'replace',
    plugins: 'replace',
    module: 'replace',
})(commonConfig(env), {
    entry: null,
    devtool: 'inline-source-map',

    module: {
        rules: [
            {
                test: /\.ts$/,
                exclude: root('node_modules'),
                use: [
                    {
                        loader: 'ts-loader',
                        options: {
                            silent: true,
                            // disable type checker - we will use it in fork plugin
                            transpileOnly: true,
                            configFile: root('src', 'tsconfig.json')
                        },
                    },
                ],
            },

            {
                test: /src\/.+\.[tj]s$/,
                exclude: /(node_modules|\.spec\.[tj]s$)/,
                loader: 'istanbul-instrumenter-loader',
                enforce: 'post',
                options: {
                    esModules: true,
                },
            },
            {
                test: /\.html$/,
                loader: 'html-loader'

            },
            {
                test: /\.json$/,
                use: 'json-loader',
                include: root('src', 'assets')
            },
            {
                // Do not load any of these files for unit testing
                test: /\.(png|jpe?g|gif|svg|woff|woff2|ttf|eot|ico|scss|less)$/,
                loader: 'null-loader'
            },
            {
                test: /\.css$/,
                exclude: root('src', 'app'),
                loader: 'null-loader'
            },
            {
                test: /\.css$/,
                include: root('src', 'app'),
                loader: 'raw-loader'
            }
        ]
    },

    plugins: [
        new ProvidePlugin({
            $: 'jquery',
            jQuery: 'jquery',
            'window.jQuery': 'jquery',
            'window.jquery': 'jquery',
            'moment': 'moment'
        }),

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

        // // run TypeScript checker in a separate thread for build performance gain
        new ForkTsCheckerWebpackPlugin({
            checkSyntacticErrors: true,
            // tslint runs through tslint-loader
            tslint: true,
            tsconfig: root('src', 'tsconfig.json'),
        }),

        // Bundle vendor modules together
        new CommonsChunkPlugin(vendorChunk),

        // Bundle main chunk
        new CommonsChunkPlugin(mainChunk),
    ],

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
});
