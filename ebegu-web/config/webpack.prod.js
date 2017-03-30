// jscs:disable requireCamelCaseOrUpperCaseIdentifiers
/**
 * @author: @AngularClass
 */

var helpers = require('./helpers'); // Helper: root(), and rootDir() are defined at the bottom
var webpackMerge = require('webpack-merge'); //Used to merge webpack configs
var commonConfig = require('./webpack.common.js'); //The settings that are common to prod and dev


/**
 * Webpack Plugins
 */
var CopyWebpackPlugin = require('copy-webpack-plugin');
var DefinePlugin = require('webpack/lib/DefinePlugin');
var UglifyJsPlugin = require('webpack/lib/optimize/UglifyJsPlugin');
const LoaderOptionsPlugin = require("webpack/lib/LoaderOptionsPlugin");
var CompressionPlugin = require('compression-webpack-plugin');
var WebpackMd5Hash = require('webpack-md5-hash');
const ExtractTextPlugin = require('extract-text-webpack-plugin');

/**
 * Webpack Constants
 */
const ENV = process.env.NODE_ENV = process.env.ENV = 'production';
const HOST = process.env.HOST || 'localhost';
const PORT = process.env.PORT || 80;
const METADATA = webpackMerge(commonConfig.metadata, {
    host: HOST,
    port: PORT,
    ENV: ENV,
    HMR: false
});

module.exports = webpackMerge(commonConfig, {


    // Developer tool to enhance debugging
    //
    // See: http://webpack.github.io/docs/configuration.html#devtool
    // See: https://github.com/webpack/docs/wiki/build-performance#sourcemaps
    devtool: 'cheap-module-source-map ',

    // Options affecting the output of the compilation.
    //
    // See: http://webpack.github.io/docs/configuration.html#output
    output: {

        // The output directory as absolute path (required).
        //
        // See: http://webpack.github.io/docs/configuration.html#output-path
        path: helpers.root('dist'),

        // Specifies the name of each output file on disk.
        // IMPORTANT: You must not specify an absolute path here!
        //
        // See: http://webpack.github.io/docs/configuration.html#output-filename
        filename: '[name].[chunkhash].bundle.js',

        // The filename of the SourceMaps for the JavaScript files.
        // They are inside the output.path directory.
        //
        // See: http://webpack.github.io/docs/configuration.html#output-sourcemapfilename
        sourceMapFilename: '[name].[chunkhash].bundle.map',

        // The filename of non-entry chunks as relative path
        // inside the output.path directory.
        //
        // See: http://webpack.github.io/docs/configuration.html#output-chunkfilename
        chunkFilename: '[id].[chunkhash].chunk.js'

    },

    // Add additional plugins to the compiler.
    //
    // See: http://webpack.github.io/docs/configuration.html#plugins
    plugins: [

        new LoaderOptionsPlugin({
           debug: false,
            minimize: true
         }),

        new ExtractTextPlugin('[name].[hash].css'),

        // Plugin: WebpackMd5Hash
        // Description: Plugin to replace a standard webpack chunkhash with md5.
        //
        // See: https://www.npmjs.com/package/webpack-md5-hash
        new WebpackMd5Hash(),



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
        // Plugin: UglifyJsPlugin
        // Description: Minimize all JavaScript output of chunks.
        // Loaders are switched into minimizing mode.
        //
        // See: https://webpack.github.io/docs/list-of-plugins.html#uglifyjsplugin
        // NOTE: To debug prod builds uncomment //debug lines and comment //prod lines
        new UglifyJsPlugin({
            // beautify: true, //debug
            // mangle: false, //debug
            // dead_code: false, //debug
            // unused: false, //debug
            // deadCode: false, //debug
            // compress: {
            //   screw_ie8: true,
            //   keep_fnames: true,
            //   drop_debugger: false,
            //   dead_code: false,
            //   unused: false
            // }, // debug
            // comments: true, //debug

            beautify: false, //prod

            mangle: {
                screw_ie8: true,
                keep_fnames: true
            }, //prod
            /*
             mangle: {
             screw_ie8: true,
             except: [
             'App',
             'About',
             'Contact',
             'Home',
             'Menu',
             'Footer',
             'XLarge',
             'RouterActive',
             'RouterLink',
             'RouterOutlet',
             'NgFor',
             'NgIf',
             'NgClass',
             'NgSwitch',
             'NgStyle',
             'NgSwitchDefault',
             'NgControl',
             'NgControlName',
             'NgControlGroup',
             'NgFormControl',
             'NgModel',
             'NgFormModel',
             'NgForm',
             'NgSelectOption',
             'DefaultValueAccessor',
             'NumberValueAccessor',
             'CheckboxControlValueAccessor',
             'SelectControlValueAccessor',
             'RadioControlValueAccessor',
             'NgControlStatus',
             'RequiredValidator',
             'MinLengthValidator',
             'MaxLengthValidator',
             'PatternValidator',
             'AsyncPipe',
             'DatePipe',
             'JsonPipe',
             'NumberPipe',
             'DecimalPipe',
             'PercentPipe',
             'CurrencyPipe',
             'LowerCasePipe',
             'UpperCasePipe',
             'SlicePipe',
             'ReplacePipe',
             'I18nPluralPipe',
             'I18nSelectPipe'
             ] // Needed for uglify RouterLink problem
             }, // prod
             */
            compress: {
                screw_ie8: true
            }, //prod
            comments: false //prod
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





    node: {
        global: true,
        crypto: 'empty',
        process: false,
        module: false,
        clearImmediate: false,
        setImmediate: false
    }
});
