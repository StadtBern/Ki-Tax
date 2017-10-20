const __karmaWebpackManifest__ = [];

// require all modules ending in "_test" from the
// current directory and all subdirectories
const testsContext = (<any>require).context('../src', true, /\.spec\.ts/);

// console.log('specbundle output', testsContext.keys());

function inManifest(path) {
    return __karmaWebpackManifest__.indexOf(path) >= 0;
}

let runnable = testsContext.keys().filter(inManifest);

// Run all tests if we didn't find any changes
if (!runnable.length) {
    runnable = testsContext.keys();
}

runnable.forEach(testsContext);
