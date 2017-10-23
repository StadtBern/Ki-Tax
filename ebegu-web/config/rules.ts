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

import {Rule} from 'webpack';
import {root} from './helpers';

const ExtractTextPlugin = require('extract-text-webpack-plugin');

//um das css in ein eigenes file zu exportieren sollten wir wohl sowas machen:
// https://gist.github.com/squadwuschel/47b6248d500c1cf6de23127a695183b0 aktuell ist das aber irgendwie kaputt
// also https://github.com/webpack-contrib/extract-text-webpack-plugin/issues/263
export default (): Rule[] => {

    return [
        {
            test: /\.ts$/,
            exclude: /node_modules/,
            use: [
                {
                    loader: 'cache-loader',
                },
                {
                    loader: 'thread-loader',
                    options: {
                        // there should be 1 cpu for the fork-ts-checker-webpack-plugin
                        workers: require('os').cpus().length - 1,
                        name: 'tsPool',
                    },
                },
                {
                    loader: 'ts-loader',
                    options: {
                        silent: true, // avoid breaking up webpack progress
                        // disable type checker - we will use it in fork plugin
                        transpileOnly: true,
                        happyPackMode: true,
                    },
                },
            ],
        },

        // Html loader advanced options
        //
        // See: https://github.com/webpack/html-loader#advanced-options
        {
            test: /\.html$/,
            use: {
                loader: 'html-loader',
                options: {
                    minimize: false,  //minimize breaks for dv-userselect
                    removeAttributeQuotes: false,
                    caseSensitive: true,
                    // customAttrSurround: [
                    //     [/#/, /(?:)/],
                    //     [/\*/, /(?:)/],
                    //     [/\[?\(?/, /(?:)/]
                    // ],
                    // customAttrAssign: [/\)?\]?=/]
                }
            }
        },
        {
            test: /\.(png|jpe?g|gif|svg|woff|woff2|ttf|eot)$/,
            use: 'file-loader?name=assets/[name].[hash].[ext]'
        },
        {
            test: /\.json$/,
            use: 'json-loader',
            include: root('src', 'assets')
        },
        //include the default styles in a seperat file
        {
            test: /\.less$/,
            exclude: root('src'),
            use: ExtractTextPlugin.extract({
                fallback: 'style-loader',
                use: [
                    {
                        loader: 'css-loader',
                        options: {
                            sourceMap: true
                        }
                    },
                    {
                        loader: 'less-loader',
                        options: {
                            sourceMap: true
                        }
                    }
                ]
            })
        },
        //include the styles inline for the appdirectory
        {
            test: /\.less$/,
            include: root('src'),
            use: [{
                loader: 'style-loader' // creates style nodes from JS strings
            }, {
                loader: 'css-loader' // translates CSS into CommonJS
            }, {
                loader: 'less-loader' // compiles Less to CSS
            }]
        },
        //include the default styles in a seperat file
        {
            test: /\.scss$/,
            exclude: root('src'),
            loader: ExtractTextPlugin.extract({
                fallback: 'style-loader',
                use: [
                    {
                        loader: 'css-loader',
                        options: {
                            sourceMap: true
                        }
                    },
                    {
                        loader: 'sass-loader',
                        options: {
                            sourceMap: true
                        }
                    }
                ]
            })
        },
        {
            test: /\.scss$/,
            include:
                root('src'),
            use: [
                {
                    loader: 'style-loader' // creates style nodes from JS strings
                }, {
                    loader: 'css-loader' // translates CSS into CommonJS
                }, {
                    loader: 'sass-loader' // compiles Less to CSS
                }
            ]
        },

        //include css not belonging to app directly
        {
            test: /\.css$/,
            exclude:
                root('src'),
            loader:
                ExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: {
                        loader: 'css-loader',
                        options: {
                            sourceMap: true
                        }
                    }
                })
        },

        //include css for our components etc
        {
            test: /\.css$/,
            include:
                root('src'),
            loader:
                'raw-loader'
        }
    ];
};
