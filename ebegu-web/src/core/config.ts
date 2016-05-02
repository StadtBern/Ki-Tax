import 'angular';
import 'angular-translate';
import 'angular-unsavedchanges';
import {ITranslateProvider} from 'angular-translate';
import IInjectorService = angular.auto.IInjectorService;

configure.$inject = ['$translateProvider', '$injector'];
/* @ngInject */
export function configure($translateProvider: ITranslateProvider, $injector: IInjectorService) {
    //Translation Provider configuration
    let translProp = require('../assets/translations/translations_de.json');

    $translateProvider.useSanitizeValueStrategy('escapeParameters');
    $translateProvider
        .translations('de', translProp)
        .fallbackLanguage('de')
        .preferredLanguage('de');

    //Dirty Check configuration (nur wenn plugin vorhanden)
    if ($injector.has('unsavedWarningsConfigProvider')) {
        let unsavedWarningsConfigProvider : any = $injector.get('unsavedWarningsConfigProvider');
        unsavedWarningsConfigProvider.useTranslateService = true;
        unsavedWarningsConfigProvider.logEnabled = false;
        unsavedWarningsConfigProvider.navigateMessage = 'UNSAVED_WARNING';
        unsavedWarningsConfigProvider.reloadMessage = 'UNSAVED_WARNING_RELOAD';
    }
}
