import {IState} from 'angular-ui-router';
import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import * as gesuchTpl from './gesuch.html';


//STATES

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

export class EbeguStammdatenState implements angular.ui.IState {
    name = 'gesuch.stammdaten';
    template = '<stammdaten-view>';
    url = '/stammdaten/:gesuchstellerNumber';

    constructor() {
    }

}

export class EbeguKinderState implements angular.ui.IState {
    name = 'gesuch.kinder';
    template = '<kinder-view>';
    url = '/kinder';

    constructor() {
    }

}

//PARAMS
export class IStammdatenStateParams implements ng.ui.IStateParamsService {
    gesuchstellerNumber: number;
}


export class EbeguWebGesuchRun {
    static $inject = ['routerHelper'];
    /* @ngInject */
    constructor(routerHelper: RouterHelper) {
        routerHelper.configureStates(this.getStates());
    }

    /**
     * @returns {angular.ui.IState[]}
     */
    public getStates(): Array<angular.ui.IState> {
        return [new EbeguGesuchState(), new EbeguFamiliensituationState(), new EbeguStammdatenState(),
            new EbeguKinderState()];
    }

}

