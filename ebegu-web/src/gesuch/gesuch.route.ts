/// <reference path="../../typings/browser.d.ts" />
module ebeguWeb.routes {
    import IApplicationPropertyRS = ebeguWeb.services.IApplicationPropertyRS;
    import ApplicationProperty = ebeguWeb.API.TSApplicationProperty;
    'use strict';


    export class EbeguGesuchState implements angular.ui.IState {
        name = 'gesuch';
        templateUrl = 'src/gesuch/gesuch.html';
        url = '/gesuch';

        constructor() {
        }
    }

    export class EbeguFamiliensituationState implements angular.ui.IState {
        name = 'gesuch.familiensituation';
        template = '<familiensituation-view>';
        url = '/familiensituation';

        constructor() {
        }

    }

    export class EbeguStammDatenState implements angular.ui.IState {
        name = 'gesuch.stammdaten';
        template = '<stammdaten-view>';
        url = '/stammdaten';

        constructor() {
        }

    }


    export class EbeguWebGesuchRun {
        static $inject = ['routerHelper'];
        /* @ngInject */
        constructor(routerHelper: IRouterHelper) {
            routerHelper.configureStates(this.getStates());
        }

        /**
         * @returns {angular.ui.IState[]}
         */
        public getStates(): Array<angular.ui.IState> {
            return [new EbeguGesuchState(), new EbeguFamiliensituationState(), new EbeguStammDatenState()];
        }

        public static instance(routerHelper) : EbeguWebGesuchRun {
            return new EbeguWebGesuchRun(routerHelper);
        }

    }


    angular.module('ebeguWeb.gesuch').run(EbeguWebGesuchRun.instance);
}
