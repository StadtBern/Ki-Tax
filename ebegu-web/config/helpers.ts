import * as path from 'path';

// Helper functions
let _root = path.resolve(__dirname, '..');

export function hasProcessFlag(flag) {
    return process.argv.join('').indexOf(flag) > -1;
}

export function root(...args) {
    args = Array.prototype.slice.call(arguments, 0);
    return path.join.apply(path, [_root].concat(args));
}

export function rootNode(args) {
    args = Array.prototype.slice.call(arguments, 0);
    return root.apply(path, ['node_modules'].concat(args));
}

export function prependExt(extensions, args) {
    args = args || [];
    if (!Array.isArray(args)) {
        args = [args];
    }
    return extensions.reduce(function (memo, val) {
        return memo.concat(val, args.map(function (prefix) {
            return prefix + val;
        }));
    }, ['']);
}

export function packageSort(packages) {
    // packages = ['polyfills', 'vendor', 'main']
    let len = packages.length - 1;
    let first = packages[0];
    let last = packages[len];
    return function sort(a, b) {
        // polyfills always first
        if (a.names[0] === first) {
            return -1;
        }
        // main always last
        if (a.names[0] === last) {
            return 1;
        }
        // vendor before app
        if (a.names[0] !== first && b.names[0] === last) {
            return -1;
        } else {
            return 1;
        }
    };
}

export function reverse(arr) {
    return arr.reverse();
}
