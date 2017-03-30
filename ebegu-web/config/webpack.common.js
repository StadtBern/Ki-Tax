var webpack = require('webpack');
var helpers = require('./helpers');
var loaderRules = require('./rules');
var fs = require('fs');
var xml2js = require('xml2js');

var parsedversion = 0;
var parser = new xml2js.Parser();

var contents = fs.readFileSync(__dirname + '/../pom.xml').toString();

var re = new RegExp("<artifactId>ebegu</artifactId>[\\s\\S]*?<version>(.*?)</version>[\\s\\S]*?<packaging>pom</packaging>","im");
var myMatchArray = re.exec(contents);
parsedversion = ( myMatchArray !== null) ?  myMatchArray[1] : 'unknown';
console.log("Parsed Version from pom is " + parsedversion);


var currentTime = new Date();
/**
 * Webpack Plugins
 */
const CopyWebpackPlugin = require('copy-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const DefinePlugin = require('webpack/lib/DefinePlugin');
/**
 * Webpack Constants
 */
const METADATA = {
    title: 'seed Webpack from DV Bern',
    baseUrl: '/',
    version: parsedversion,
    buildtstamp: currentTime.toISOString() || ''
};

/**
 * Webpack configuration
 *
 * See: http://webpack.github.io/docs/configuration.html#cli
 */
module.exports = {



    // Static metadata for index.html
    //
    // See: (custom attribute)
    // metadata: METADATA,

    // Cache generated modules and chunks to improve performance for multiple incremental builds.
    // This is enabled by default in watch mode.
    // You can pass false to disable it.
    //
    // See: http://webpack.github.io/docs/configuration.html#cache
    // cache: false,

    // The entry point for the bundle
    // Our Angular.js app
    //
    // See: http://webpack.github.io/docs/configuration.html#entry
    // change dvbern
    entry: {
        // 'polyfills': './src/polyfills.ts',
        'vendor': './src/vendor.ts',
        'main': './src/bootstrap.ts'
    },

    // Options affecting the resolving of modules.
    //
    // See: http://webpack.github.io/docs/configuration.html#resolve
    resolve: {
        modules:[helpers.root('src'), 'node_modules'],

        // An array of extensions that should be used to resolve modules.
        //
        // See: http://webpack.github.io/docs/configuration.html#resolve-extensions
        extensions: ['.ts', '.js']
    },

    // Options affecting the normal modules.
    //
    // See: http://webpack.github.io/docs/configuration.html#module
    module: {

        // An array of automatically applied loaders.
        //
        // IMPORTANT: The loaders here are resolved relative to the resource which they are applied to.
        // This means they are not resolved relative to the configuration file.
        //
        // See: http://webpack.github.io/docs/configuration.html#module-loaders
        // loaders: loaders
        rules: loaderRules
    },

    // Add additional plugins to the compiler.
    //
    // See: http://webpack.github.io/docs/configuration.html#plugins
    plugins: [

        new webpack.ProvidePlugin({
            $: 'jquery',
            jQuery: 'jquery',
            'window.jQuery': 'jquery',
            'window.jquery': 'jquery',
            'moment': 'moment'
        }),

        // Plugin: ForkCheckerPlugin
        // Description: Do type checking in a separate process, so webpack don't need to wait.
        //
        // See: https://github.com/s-panferov/awesome-typescript-loader#forkchecker-boolean-defaultfalse
        // new ForkCheckerPlugin(),



        // Plugin: CommonsChunkPlugin
        // Description: Shares common code between the pages.
        // It identifies common modules and put them into a commons chunk.
        //
        // See: https://webpack.github.io/docs/list-of-plugins.html#commonschunkplugin
        // See: https://github.com/webpack/docs/wiki/optimization#multi-page-app
        new webpack.optimize.CommonsChunkPlugin({
            name: helpers.reverse(['polyfills', 'vendor', 'main']),
            minChunks: Infinity
        }),

        // Plugin: CopyWebpackPlugin
        // Description: Copy files and directories in webpack.
        //
        // Copies project static assets.
        //
        // See: https://www.npmjs.com/package/copy-webpack-plugin
        new CopyWebpackPlugin([
            {from: 'src/assets', to: 'src/assets'},
        ]),

        // Plugin: HtmlWebpackPlugin
        // Description: Simplifies creation of HTML files to serve your webpack bundles.
        // This is especially useful for webpack bundles that include a hash in the filename
        // which changes every compilation.
        //
        // See: https://github.com/ampedandwired/html-webpack-plugin
        new HtmlWebpackPlugin({
            template: 'src/index.html',
            chunksSortMode: helpers.packageSort(['polyfills', 'vendor', 'main'])
        }),

        new DefinePlugin({
            'ENV': JSON.stringify(METADATA.ENV),
            'HMR': METADATA.HMR,
            'VERSION': JSON.stringify(METADATA.version),
            'BUILDTSTAMP': JSON.stringify(METADATA.buildtstamp),
            'process.env': {
                'ENV': JSON.stringify(METADATA.ENV),
                'NODE_ENV': JSON.stringify(METADATA.ENV),
                'HMR': METADATA.HMR,
                'VERSION': JSON.stringify(METADATA.version)
            }
        })

    ],

    // Include polyfills or mocks for various node stuff
    // Description: Node configuration
    //
    // See: https://webpack.github.io/docs/configuration.html#node
    node: {
        global: true,
        crypto: 'empty',
        module: false,
        clearImmediate: false,
        setImmediate: false
    },
};
