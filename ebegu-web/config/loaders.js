var helpers = require('./helpers');

module.exports = [
    // Typescript
    {test: /\.ts(x?)$/, loader: 'ts-loader' /* loader: 'awesome-typescript-loader'*/, exclude: [/\.(spec|e2e)\.ts$/]},

    // Less to CSS to Styles
    {test: /\.less$/, loader: 'style!css!less'},

    // Raw loader support for *.html files
    // Returns file content as string
    //
    // See: https://github.com/webpack/raw-loader
    {test: /\.html$/, loader: 'raw-loader', exclude: [helpers.root('src/index.html')]},

    // Raw loader support for *.css
    // Returns file content as string
    //
    // See: https://github.com/webpack/raw-loader
    {test: /\.css$/, loader: 'raw-loader', exclude: [helpers.root('src/index.html')]},
    
    // Json loader support for *.json files.
    //
    // See: https://github.com/webpack/json-loader
    {test: /\.json$/, loader: 'json-loader', exclude: [helpers.root('src/index.html')]},

    {
        test: /\.woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?$/,
        loader: 'url-loader?limit=10000&mimetype=application/font-woff'
    }, {
        test: /\.(ttf|eot|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/,
        loader: 'file-loader'
    }, {
        test: '\.jpg$',
        exclude: /node_modules/,
        loader: 'file'
    }, {
        test: '\.png$',
        exclude: /node_modules/,
        loader: 'url'
    }
];
