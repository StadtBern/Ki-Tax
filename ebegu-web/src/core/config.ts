import 'angular';
import 'angular-translate';
import 'angular-unsavedchanges';
import {ITranslateProvider} from 'angular-translate';
import 'angular-hotkeys';
import HttpErrorInterceptor from './errors/service/HttpErrorInterceptor';
import HttpAuthInterceptor from '../authentication/service/HttpAuthInterceptor';
import IInjectorService = angular.auto.IInjectorService;
import IThemingProvider = angular.material.IThemingProvider;
import IHttpProvider = angular.IHttpProvider;

configure.$inject = ['$translateProvider', '$injector', '$httpProvider', '$mdThemingProvider', 'hotkeysProvider'];
/* @ngInject */
export function configure($translateProvider: ITranslateProvider, $injector: IInjectorService, $httpProvider: IHttpProvider,
                          $mdThemingProvider: IThemingProvider, hotkeysProvider: any) {
    //Translation Provider configuration
    let translProp = require('../assets/translations/translations_de.json');

    // In case you have issues with double-escaped parameters, check out this issue: https://github.com/angular-translate/angular-translate/issues/1101
    $translateProvider.useSanitizeValueStrategy('sanitizeParameters');

    $translateProvider
        .translations('de', translProp)
        .fallbackLanguage('de')
        .preferredLanguage('de');

    //Dirty Check configuration (nur wenn plugin vorhanden)
    if ($injector.has('unsavedWarningsConfigProvider')) {
        let unsavedWarningsConfigProvider: any = $injector.get('unsavedWarningsConfigProvider');
        unsavedWarningsConfigProvider.useTranslateService = true;
        unsavedWarningsConfigProvider.logEnabled = false;
        unsavedWarningsConfigProvider.navigateMessage = 'UNSAVED_WARNING';
        unsavedWarningsConfigProvider.reloadMessage = 'UNSAVED_WARNING_RELOAD';
    }
    //Config Angular Module Theme
    $mdThemingProvider.theme('default')
    // .primaryPalette('red')
        .accentPalette('red');

    //Config hotkey provider: https://github.com/chieffancypants/angular-hotkeys#angular-hotkeys-
    hotkeysProvider.useNgRoute = false;
    hotkeysProvider.includeCheatSheet = false;

    //Configuration of $http service
    $httpProvider.interceptors.push('HttpErrorInterceptor');
    $httpProvider.interceptors.push('HttpAuthInterceptor');
}
