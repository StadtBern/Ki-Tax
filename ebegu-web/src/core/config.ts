import 'angular';
import 'angular-translate';
import 'angular-unsavedchanges';
import {ITranslateProvider} from 'angular-translate';

configure.$inject = ['$translateProvider', 'unsavedWarningsConfigProvider'];
/* @ngInject */
export function configure($translateProvider: ITranslateProvider, unsavedWarningsConfigProvider: any) {
    //Translation Provider configuration
    let translProp = require('../assets/translations/translations_de.json');

    $translateProvider.useSanitizeValueStrategy('escapeParameters');
    $translateProvider
        .translations('de', translProp)
        .fallbackLanguage('de')
        .preferredLanguage('de');

    //Dirty Check configuration
    unsavedWarningsConfigProvider.useTranslateService = true;
    unsavedWarningsConfigProvider.logEnabled = true;
    unsavedWarningsConfigProvider.navigateMessage = 'UNSAVED_WARNING';
    unsavedWarningsConfigProvider.reloadMessage = 'UNSAVED_WARNING_RELOAD';

        console.log(process.env);



}
