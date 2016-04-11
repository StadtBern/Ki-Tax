import {ITranslateProvider} from 'angular-translate';

/* @ngInject */
export function configure($translateProvider: ITranslateProvider) {

    $translateProvider.useSanitizeValueStrategy('escapeParameters');
    $translateProvider.useStaticFilesLoader({
            prefix: 'src/translations/translations_',
            suffix: '.json'
        })
        .fallbackLanguage('de')
        .preferredLanguage('de');

}
