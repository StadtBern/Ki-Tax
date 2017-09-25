import {Rule} from 'webpack';
import {root} from './helpers';

const ExtractTextPlugin = require('extract-text-webpack-plugin');

//um das css in ein eigenes file zu exportieren sollten wir wohl sowas machen:
// https://gist.github.com/squadwuschel/47b6248d500c1cf6de23127a695183b0 aktuell ist das aber irgendwie kaputt
// also https://github.com/webpack-contrib/extract-text-webpack-plugin/issues/263
export default (): Rule[] => {

    return [

        {
            // Static analysis linter for TypeScript advanced options configuration
            // Description: An extensible linter for the TypeScript language.
            //
            // See: https://github.com/wbuchwalter/tslint-loader
            test: /\.ts$/,
            enforce: 'pre',
            use: {
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
            }
        },
        {
            test: /\.ts$/,
            use: {
                loader: 'awesome-typescript-loader',
                options: {configFileName: root('src', 'tsconfig.json')}
            }
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
