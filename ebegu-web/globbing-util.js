/*jslint node: true */
'use strict';

module.exports = {
    //Using exclusion patterns slows down Grunt significantly
    //instead of creating a set of patterns like '**/*.js' and '!**/node_modules/**'
    //this method is used to create a set of inclusive patterns for all subdirectories
    //skipping node_modules, dist, and any .dirs
    //This enables users to create any directory structure they desire.
    createFolderGlobs: function (fileTypePatterns) {
        fileTypePatterns = Array.isArray(fileTypePatterns) ? fileTypePatterns : [fileTypePatterns];
        var ignore = ['node_modules', 'dist', 'build', 'temp'];
        var fs = require('fs');
        return fs.readdirSync(process.cwd())
            .map(function (file) {
                if (ignore.indexOf(file) !== -1 ||
                    file.indexOf('.') === 0 || !fs.lstatSync(file).isDirectory()) {
                    return null;
                } else {
                    return [].concat(fileTypePatterns.map(function (pattern) {
                        return file + '/**/' + pattern;
                    }));
                }
            })
            .filter(function (patterns) {
                return patterns;
            })
            .concat(fileTypePatterns);
    },

    flattenArrayOfArrays: function (a, r) {
        if (!r) {
            r = [];
        }
        for (var i = 0; i < a.length; i++) {
            if (a[i].constructor === Array) {
                this.flattenArrayOfArrays(a[i], r);
            } else {
                r.push(a[i]);
            }
        }
        return r;
    }
};
