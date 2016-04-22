import 'angular-translate';
import {ITranslateProvider} from 'angular-translate';
import * as translations from '../assets/translations/translations_de.json';

configure.$inject = ['$translateProvider'];
/* @ngInject */
export function configure($translateProvider: ITranslateProvider) {

    $translateProvider.useSanitizeValueStrategy('escapeParameters');
    $translateProvider
        .translations('de', translations)
        .fallbackLanguage('de')
        .preferredLanguage('de');
}
