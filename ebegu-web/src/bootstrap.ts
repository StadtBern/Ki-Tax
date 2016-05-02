import 'angular';
import 'angular-material';
import 'angular-animate';
import 'angular-sanitize';
import 'angular-messages';
import 'angular-aria';
import 'angular-cookies';
import 'angular-translate';
import 'angular-translate-loader-static-files';
import 'angular-ui-bootstrap';
import 'angular-smart-table';
import 'angular-moment';
import 'angular-unsavedchanges';
import appModule from './app.module';

angular.element(document).ready(function () {
    angular.bootstrap(document, [appModule.name], {
        strictDi: true
    });
});
