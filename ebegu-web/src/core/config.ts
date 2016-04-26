import 'angular';
import 'angular-translate';
import {ITranslateProvider} from 'angular-translate';

configure.$inject = ['$translateProvider'];
/* @ngInject */
export function configure($translateProvider: ITranslateProvider) {
    let translProp = require('../assets/translations/translations_de.json');

    $translateProvider.useSanitizeValueStrategy('escapeParameters');
    $translateProvider
        .translations('de', translProp)
        .fallbackLanguage('de')
        .preferredLanguage('de');
}
