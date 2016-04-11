var loaders = require('./loaders');
var BrowserSyncPlugin = require('browser-sync-webpack-plugin');
var HtmlWebpackPlugin = require('html-webpack-plugin');
var webpack = require('webpack');
var helpers = require('./helpers');

/**
 * Webpack Constants
 */
const ENV = process.env.ENV = process.env.NODE_ENV = 'development';
const HMR = helpers.hasProcessFlag('hot');
const METADATA = {
    title: 'Angular2 Webpack Starter by @gdi2990 from @AngularClass',
    baseUrl: '/',
    host: 'localhost',
    port: 3000,
    ENV: ENV,
    HMR: HMR
};

module.exports = {
    // Static metadata for index.html
    //
    // See: (custom attribute)
    metadata: METADATA,

    // Switch loaders to debug mode.
    //
    // See: http://webpack.github.io/docs/configuration.html#debug
    debug: true,

    entry: ['./src/core/bootstrap.ts'],
    // entry: ['webpack/hot/dev-server', '/src/app.module.ts'],
    output: {
        filename: 'build.js',
        path: 'dist'
    },
    resolve: {
        root: __dirname,
        extensions: ['', '.ts', '.js', '.json']
    },
    resolveLoader: {
        modulesDirectories: ['node_modules']
    },
    devtool: 'source-map',
    plugins: [
        new HtmlWebpackPlugin({
            template: './src/index.html',
            inject: 'body',
            hash: true
        }),
        // new BrowserSyncPlugin({
        //     host: 'localhost',
        //     port: 8080,
        //     server: {
        //         baseDir: 'dist'
        //     },
        //     ui: false,
        //     online: false,
        //     notify: false
        // }),
        new webpack.ProvidePlugin({
            $: 'jquery',
            jQuery: 'jquery',
            'window.jQuery': 'jquery',
            'window.jquery': 'jquery'
        })
    ],
    module: {
        loaders: loaders
    }
};
