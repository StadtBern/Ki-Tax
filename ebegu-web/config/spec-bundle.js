var testsContext;

// require('babel-core/polyfill');
// require('./vendor/angular.src');
// require('angular');
// require('angular-mocks');
// require('../src/core/bootstrap');
//homa note the duble cc

testsContext = require.context('../src', true, /\.spec\.ts/);
console.log('specbundle output', testsContext.keys());
testsContext.keys().forEach(testsContext);
