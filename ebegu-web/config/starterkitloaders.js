var helpers = require('./helpers');

module.exports = [

    // Typescript loader support for .ts and Angular 2 async routes via .async.ts
    //
    // See: https://github.com/s-panferov/awesome-typescript-loader
    {
        test: /\.ts$/,
        loader: 'awesome-typescript-loader',
        exclude: [/\.(spec|e2e)\.ts$/]
    },

    // Json loader support for *.json files.
    //
    // See: https://github.com/webpack/json-loader
    {
        test: /\.json$/,
        loader: 'json-loader'
    },
    // added by homa
    // Less to CSS to Styles
    {test: /\.less$/, loader: 'style!css!less'},

    // Raw loader support for *.css files
    // Returns file content as string
    //
    // See: https://github.com/webpack/raw-loader
    {
        test: /\.css$/,
        loader: 'raw-loader'
    },

    // Raw loader support for *.html
    // Returns file content as string
    //
    // See: https://github.com/webpack/raw-loader
    {
        test: /\.html$/,
        loader: 'raw-loader',
        exclude: [helpers.root('src/index.html')]
    },

];
