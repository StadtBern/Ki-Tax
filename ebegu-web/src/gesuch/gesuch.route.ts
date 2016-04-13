import {IState} from 'angular-ui-router';
import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import * as gesuchTpl from './gesuch.html';

gesuchRun.$inject = ['RouterHelper'];
/* @ngInject */
export function gesuchRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/gesuch/familiensituation');
}

function getStates(): IState[] {
    return [new EbeguGesuchState(), new EbeguFamiliensituationState(), new EbeguStammdatenState(),
        new EbeguKinderState()];
}


//STATES

export class EbeguGesuchState implements angular.ui.IState {
    name = 'gesuch';
    template = gesuchTpl;
    url = '/gesuch';
    abstract = true;
}

export class EbeguFamiliensituationState implements angular.ui.IState {
    name = 'gesuch.familiensituation';
    template = '<familiensituation-view>';
    url = '/familiensituation';
}

export class EbeguStammdatenState implements angular.ui.IState {
    name = 'gesuch.stammdaten';
    template = '<stammdaten-view>';
    url = '/stammdaten/:gesuchstellerNumber';
}

export class EbeguKinderState implements angular.ui.IState {
    name = 'gesuch.kinder';
    template = '<kinder-view>';
    url = '/kinder';
}

//PARAMS
export class IStammdatenStateParams implements ng.ui.IStateParamsService {
    gesuchstellerNumber: number;
}
