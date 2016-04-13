import {IState} from 'angular-ui-router';
import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import * as gesuchTpl from './gesuch.html';

gesuchRun.$inject = ['RouterHelper'];

/* @ngInject */
export function gesuchRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/gesuch/familiensituation');
}

function getStates(): IState[] {
    return [
        {
            name: 'gesuch',
            template: gesuchTpl,
            url: '/gesuch',
            abstract: true
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
