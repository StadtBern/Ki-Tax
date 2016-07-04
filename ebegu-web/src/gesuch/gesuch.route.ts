import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState, IStateParamsService} from 'angular-ui-router';
import {GesuchRouteController} from './gesuch';
let gesuchTpl = require('./gesuch.html');

gesuchRun.$inject = ['RouterHelper'];
/* @ngInject */
export function gesuchRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/auth/login');
}

//array mit allen States
function getStates(): IState[] {
    return [
        new EbeguGesuchState(),
        new EbeguFamiliensituationState(),
        new EbeguStammdatenState(),
        new EbeguKinderListState(),
        new EbeguFinanzielleSituationStartState(),
        new EbeguFinanzielleSituationState(),
        new EbeguFinanzielleSituationResultateState(),
        new EbeguKindState(),
        new EbeguErwerbspensenListState(),
        new EbeguErwerbspensumState(),
        new EbeguBetreuungListState(),
        new EbeguBetreuungState(),
        new EbeguNewFallState(),
        new EbeguEinkommensverschlechterungInfoState(),
        new EbeguEinkommensverschlechterungSteuernState(),
        new EbeguEinkommensverschlechterungState()
    ];
}


//STATES

export class EbeguGesuchState implements IState {
    name = 'gesuch';
    template = gesuchTpl;
    url = '/gesuch';
    abstract = true;
    controller = GesuchRouteController;
    controllerAs = 'vm';
}

export class EbeguNewFallState implements IState {
    name = 'gesuch.fallcreation';
    template = '<fall-creation-view>';
    url = '/fall/:createNew';
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
    name = 'gesuch.betreuungen';
    template = '<betreuung-list-view>';
    url = '/betreuungen';
}

export class EbeguBetreuungState implements IState {
    name = 'gesuch.betreuung';
    template = '<betreuung-view>';
    url = '/betreuungen/betreuung';
}
export class EbeguErwerbspensenListState implements IState {
    name = 'gesuch.erwerbsPensen';
    template = '<erwerbspensum-list-view>';
    url = '/erwerbspensen';
}

export class EbeguErwerbspensumState implements IState {
    name = 'gesuch.erwerbsPensum';
    template = '<erwerbspensum-view>';
    url = '/erwerbspensen/erwerbspensum/:gesuchstellerNumber/:erwerbspensumNum';
}

export class EbeguFinanzielleSituationState implements IState {
    name = 'gesuch.finanzielleSituation';
    template = '<finanzielle-situation-view>';
    url = '/finanzielleSituation/:gesuchstellerNumber';
}

export class EbeguFinanzielleSituationStartState implements IState {
    name = 'gesuch.finanzielleSituationStart';
    template = '<finanzielle-situation-start-view>';
    url = '/finanzielleSituationStart';
}


export class EbeguFinanzielleSituationResultateState implements IState {
    name = 'gesuch.finanzielleSituationResultate';
    template = '<finanzielle-situation-resultate-view>';
    url = '/finanzielleSituationResultate';
}

export class EbeguEinkommensverschlechterungInfoState implements IState {
    name = 'gesuch.einkommensverschlechterungInfo';
    template = '<einkommensverschlechterung-info-view>';
    url = '/einkommensverschlechterungInfo';
}

export class EbeguEinkommensverschlechterungSteuernState implements IState {
    name = 'gesuch.einkommensverschlechterungSteuern';
    template = '<einkommensverschlechterung-steuern-view>';
    url = '/einkommensverschlechterungSteuern';
}

export class EbeguEinkommensverschlechterungState implements IState {
    name = 'gesuch.einkommensverschlechterung';
    template = '<einkommensverschlechterung-view>';
    url = '/einkommensverschlechterung/:gesuchstellerNumber/:basisjahrPlus';
}

//PARAMS
export class IStammdatenStateParams implements IStateParamsService {
    gesuchstellerNumber: string;
}

export class IKindStateParams implements IStateParamsService {
    kindNumber: string;
}

export class INewFallStateParams implements IStateParamsService {
    createNew: boolean;
}

export class IErwerbspensumStateParams implements IStateParamsService {
    gesuchstellerNumber: string;
    erwerbspensumNum: string;
}

export class IEinkommensverschlechterungStateParams implements IStateParamsService {
    gesuchstellerNumber: string;
    basisjahrPlus: string;
}
