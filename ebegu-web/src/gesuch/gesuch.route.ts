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
        new EbeguKinderState(), new EbeguFinanzielleSituationState()];
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

export class EbeguKinderState implements IState {
    name = 'gesuch.kinder';
    template = '<kinder-view>';
    url = '/kinder';
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
