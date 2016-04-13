import {ITranslateProvider} from 'angular-translate';

configure.$inject = ['$translateProvider'];
/* @ngInject */
export function configure($translateProvider: ITranslateProvider) {

    $translateProvider.useSanitizeValueStrategy('escapeParameters');
    $translateProvider.useStaticFilesLoader({
            prefix: 'src/assets/translations/translations_',
            suffix: '.json'
        })
        .fallbackLanguage('de')
        .preferredLanguage('de');
}
