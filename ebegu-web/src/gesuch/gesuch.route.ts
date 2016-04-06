import {IState} from 'angular-ui-router';
import {RouterHelper} from '../dvbModules/router/route-helper-provider';

export class EbeguWebGesuchRun {
    static $inject = ['routerHelper'];
    /* @ngInject */
    constructor(routerHelper: RouterHelper) {
        routerHelper.configureStates(this.getStates());
    }

    public static instance(routerHelper: RouterHelper): EbeguWebGesuchRun {
        return new EbeguWebGesuchRun(routerHelper);
    }

    public getStates(): IState[] {
        return [
            {
                name: 'gesuch',
                templateUrl: 'src/gesuch/gesuch.html',
                url: '/gesuch'
            },
            {
                name: 'gesuch.familiensituation',
                template: '<familiensituation-view>',
                url: '/familiensituation'

            },
            {
                name: 'gesuch.stammdaten',
                template: '<stammdaten-view>',
                url: '/stammdaten'
            }
        ];
    }
}

angular.module('ebeguWeb.gesuch').run(EbeguWebGesuchRun.instance);
