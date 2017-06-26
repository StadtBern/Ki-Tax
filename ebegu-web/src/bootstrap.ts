require('./vendor');
import appModule from './app.module';

angular.element(document).ready(function () {
    angular.bootstrap(document, [appModule.name], {
        strictDi: true
    });
});
