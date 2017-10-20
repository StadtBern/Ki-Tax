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

// @AngularClass

'use strict';
var path = require('path');

module.exports = function (config) {
    var testWebpackConfig = require('./config/webpack.test.ts').default({environment: 'test'});

    config.set({

        // base path that will be used to resolve all patterns (e.g. files, exclude)
        basePath: '',

        // frameworks to use
        // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
        frameworks: ['jasmine'],

        // list of files / patterns to load in the browser
        // we are building the test environment in ./spec-bundle.ts
        files: [
            // Required (scoped) vendor modules
            'src/vendor.ts',

            // Global vendor modules (modules listed in webpack.ProvidePlugin need to be included here)
            'node_modules/jquery/dist/jquery.js',
            'node_modules/moment/moment.js',

            // Unit Testing vendor modules
            'node_modules/angular-mocks/angular-mocks.js',

            // The app itself
            'src/app.module.ts',

            // Tests
            'config/spec-bundle.ts'
            // alterntavie zum spec bundle. Dauert aber zu lange, da für jedes spec file 1 Webpack bundle erstellt wird
            // 'src/**/*.spec.ts'
        ],

        // preprocess matching files before serving them to the browser
        // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
        preprocessors: {
            'src/vendor.ts': ['webpack'],
            'src/app.module.ts': ['webpack', 'sourcemap'],
            'config/spec-bundle.ts': ['webpack']
            // 'src/**/*.spec.ts': ['webpack']
        },

        // Webpack Config at ./webpack.test.ts
        webpack: testWebpackConfig,

        // Webpack please don't spam the console when running in karma!
        webpackServer: {noInfo: true},

        // test results reporter to use
        // possible values: 'dots', 'progress'
        // available reporters: https://npmjs.org/browse/keyword/karma-reporter
        reporters: ['mocha', 'junit', 'coverage-istanbul'],

        // any of these options are valid:
        // tslint:disable-next-line:max-line-length
        // https://github.com/istanbuljs/istanbul-api/blob/47b7803fbf7ca2fb4e4a15f3813a8884891ba272/lib/config.js#L33-L38
        coverageIstanbulReporter: {

            // reports can be any that are listed here:
            // https://github.com/istanbuljs/istanbul-reports/tree/590e6b0089f67b723a1fdf57bc7ccc080ff189d7/lib
            reports: ['html', 'cobertura', 'lcovonly', 'text-summary'],

            // base output directory. If you include %browser% in the path it will be replaced with the karma browser
            // name
            dir: path.join(__dirname, 'build/coverage'),

            // if using webpack and pre-loaders, work around webpack breaking the source path
            fixWebpackSourcePaths: true,

            // stop istanbul outputting messages like `File [${filename}] ignored, nothing could be mapped`
            skipFilesWithNoCoverage: true,

            // Most reporters accept additional config options. You can pass these through the `report-config` option
            'report-config': {

                // all options available at:
                // tslint:disable-next-line:max-line-length
                // https://github.com/istanbuljs/istanbul-reports/blob/590e6b0089f67b723a1fdf57bc7ccc080ff189d7/lib/html/index.js#L135-L137
                html: {
                    // outputs the report in ./coverage/html
                    subdir: 'html'
                }

            }

            // to enforce thresholds see https://github.com/mattlewis92/karma-coverage-istanbul-reporter
        },

        // suppress skipped tests in reporter
        mochaReporter: {
            ignoreSkipped: true
        },

        junitReporter: {
            outputFile: 'build/karma-results.xml',
            useBrowserName: false,
            xmlVersion: 1
        },

        // web server port
        port: 9876,

        // enable / disable colors in the output (reporters and logs)
        colors: true,

        // level of logging
        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO ||
        // config.LOG_DEBUG
        logLevel: config.LOG_WARN,

        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: false,

        // start these browsers
        // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
        browsers: [
            // 'Chrome',
            'PhantomJS'
        ],

        // timeout when there's no activity, increased because the start up time is quite long with webpack
        browserNoActivityTimeout: 120000,

        // Continuous Integration mode
        // if true, Karma captures browsers, runs the tests and exits
        singleRun: true
    });

};
