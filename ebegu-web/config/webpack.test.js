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
const METADATA = webpackMerge(commonConfig.metadata, {
    host: HOST,
    port: PORT,
    ENV: ENV,
    HMR: false
});


/**
 * Webpack configuration
 *
 * See: http://webpack.github.io/docs/configuration.html#cli
 */
module.exports = {
    devtool: 'inline-source-map',
    entry: {
      // 'polyfills': './src/polyfills.ts',
      // 'vendor': './src/vendor.ts',
      'main': './src/bootstrap.ts'
    },

    resolve: {
      extensions: ['.ts', '.js']
    },

    module: {
      rules: [
        {
          test: /\.ts$/,
          loaders: [
            {
              loader: 'awesome-typescript-loader',
              options: { configFileName: helpers.root('src', 'tsconfig.json') }
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
              include: helpers.root('src','assets')
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
