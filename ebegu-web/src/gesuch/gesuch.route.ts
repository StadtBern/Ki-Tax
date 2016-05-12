import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState, IStateParamsService} from 'angular-ui-router';
let gesuchTpl = require('./gesuch.html');

gesuchRun.$inject = ['RouterHelper'];
/* @ngInject */
export function gesuchRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/gesuch/familiensituation');
}

//array mit allen States
function getStates(): IState[] {
    return [new EbeguGesuchState(), new EbeguFamiliensituationState(), new EbeguStammdatenState(),
        new EbeguKinderListState(), new EbeguFinanzielleSituationState(), new EbeguKindState(),
        new EbeguBetreuungListState()];
}


//STATES

export class EbeguGesuchState implements IState {
    name = 'gesuch';
    template = gesuchTpl;
    url = '/gesuch';
    abstract = true;
}

export class EbeguFamiliensituationState implements IState {
    name = 'gesuch.familiensituation';
    template = '<familiensituation-view>';
    url = '/familiensituation';
}

export class EbeguStammdatenState implements IState {
    name = 'gesuch.stammdaten';
    template = '<stammdaten-view>';
    url = '/stammdaten/:gesuchstellerNumber';
}

export class EbeguKinderListState implements IState {
    name = 'gesuch.kinder';
    template = '<kinder-list-view>';
    url = '/kinder';
}

export class EbeguKindState implements IState {
    name = 'gesuch.kind';
    template = '<kind-view>';
    url = '/kinder/kind/:kindNumber';
}

export class EbeguBetreuungListState implements IState {
    name = 'gesuch.betreuung';
    template = '<betreuung-list-view>';
    url = '/betreuung';
}

export class EbeguFinanzielleSituationState implements IState {
    name = 'gesuch.finanzielleSituation';
    template = '<finanzielle-situation-view>';
    url = '/finanzielleSituation/:gesuchstellerNumber';
}

//PARAMS
export class IStammdatenStateParams implements IStateParamsService {
    gesuchstellerNumber: string;
}

export class IKindStateParams implements IStateParamsService {
    kindNumber: string;
}
