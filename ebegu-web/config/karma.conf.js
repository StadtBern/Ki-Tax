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

module.exports = function (config) {
    var testWebpackConfig = require('./webpack.test.js');

    config.set({

        // base path that will be used to resolve all patterns (e.g. files, exclude)
        basePath: '',

        // frameworks to use
        // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
        frameworks: ['jasmine'],

        // list of files to exclude
        exclude: [],

        // list of files / patterns to load in the browser
        // we are building the test environment in ./spec-bundle.js
        files: [{pattern: './config/spec-bundle.js', watched: false}],

        // preprocess matching files before serving them to the browser
        // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
        preprocessors: {'./config/spec-bundle.js': ['webpack', 'sourcemap', 'coverage']},

        webpackMiddleware: {
            stats: {
                chunkModules: false,
                colors: true
            }
        },

        // Webpack Config at ./webpack.test.js
        webpack: testWebpackConfig,

        coverageReporter: {
            dir: 'coverage/',
            reporters: [
                {type: 'text-summary'},
                {type: 'json'},
                {type: 'html'}
            ]
        },

        // Webpack please don't spam the console when running in karma!
        webpackServer: {noInfo: false},

        // test results reporter to use
        // possible values: 'dots', 'progress'
        // available reporters: https://npmjs.org/browse/keyword/karma-reporter
        reporters: ['mocha'],
        // reporters: [ 'mocha', 'coverage' ],

        // web server port
        port: 9876,

        // enable / disable colors in the output (reporters and logs)
        colors: true,

        // level of logging
        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
        logLevel: config.LOG_DEBUG,

        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: false,

        // start these browsers
        // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
        browsers: [
            // 'Chrome',
            'PhantomJS'
        ],

        // Continuous Integration mode
        // if true, Karma captures browsers, runs the tests and exits
        singleRun: true
    });

};
