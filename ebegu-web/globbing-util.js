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
