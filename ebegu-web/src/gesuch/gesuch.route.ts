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
        new EbeguVerfuegenListState(),
        new EbeguVerfuegenState(),
        new EbeguEinkommensverschlechterungInfoState(),
        new EbeguEinkommensverschlechterungSteuernState(),
        new EbeguEinkommensverschlechterungState(),
        new EbeguEinkommensverschlechterungResultateState(),
        new EbeguDokumenteState()
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
    url = '/fall/:createNew/:gesuchId';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<fall-creation-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}


export class EbeguFamiliensituationState implements IState {
    name = 'gesuch.familiensituation';
    url = '/familiensituation';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<familiensituation-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguStammdatenState implements IState {
    name = 'gesuch.stammdaten';
    url = '/stammdaten/:gesuchstellerNumber';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<stammdaten-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguKinderListState implements IState {
    name = 'gesuch.kinder';
    url = '/kinder';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<kinder-list-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguKindState implements IState {
    name = 'gesuch.kind';
    url = '/kinder/kind/:kindNumber';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<kind-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguBetreuungListState implements IState {
    name = 'gesuch.betreuungen';
    url = '/betreuungen';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<betreuung-list-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguBetreuungState implements IState {
    name = 'gesuch.betreuung';
    url = '/betreuungen/betreuung';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<betreuung-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguErwerbspensenListState implements IState {
    name = 'gesuch.erwerbsPensen';
    url = '/erwerbspensen';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<erwerbspensum-list-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguErwerbspensumState implements IState {
    name = 'gesuch.erwerbsPensum';
    url = '/erwerbspensen/erwerbspensum/:gesuchstellerNumber/:erwerbspensumNum';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<erwerbspensum-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguFinanzielleSituationState implements IState {
    name = 'gesuch.finanzielleSituation';
    url = '/finanzielleSituation/:gesuchstellerNumber';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<finanzielle-situation-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguFinanzielleSituationStartState implements IState {
    name = 'gesuch.finanzielleSituationStart';
    url = '/finanzielleSituationStart';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<finanzielle-situation-start-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguFinanzielleSituationResultateState implements IState {
    name = 'gesuch.finanzielleSituationResultate';
    url = '/finanzielleSituationResultate';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<finanzielle-situation-resultate-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguVerfuegenListState implements IState {
    name = 'gesuch.verfuegen';
    url = '/verfuegen/:gesuchId?';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<verfuegen-list-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguVerfuegenState implements IState {
    name = 'gesuch.verfuegenView';
    url = '/verfuegenView';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<verfuegen-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguEinkommensverschlechterungInfoState implements IState {
    name = 'gesuch.einkommensverschlechterungInfo';
    url = '/einkommensverschlechterungInfo';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<einkommensverschlechterung-info-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguEinkommensverschlechterungSteuernState implements IState {
    name = 'gesuch.einkommensverschlechterungSteuern';
    url = '/einkommensverschlechterungSteuern';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<einkommensverschlechterung-steuern-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguEinkommensverschlechterungState implements IState {
    name = 'gesuch.einkommensverschlechterung';
    url = '/einkommensverschlechterung/:gesuchstellerNumber/:basisjahrPlus';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<einkommensverschlechterung-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguEinkommensverschlechterungResultateState implements IState {
    name = 'gesuch.einkommensverschlechterungResultate';
    url = '/einkommensverschlechterungResultate/:basisjahrPlus';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<einkommensverschlechterung-resultate-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

export class EbeguDokumenteState implements IState {
    name = 'gesuch.dokumente';
    url = '/dokumente/:gesuchstellerNumber';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<dokumente-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };
}

//PARAMS

export class IGesuchStateParams implements IStateParamsService {
    gesuchId: string;
}

export class IStammdatenStateParams implements IStateParamsService {
    gesuchstellerNumber: string;
}

export class IKindStateParams implements IStateParamsService {
    kindNumber: string;
}

export class INewFallStateParams implements IGesuchStateParams {
    gesuchId: string;
    createNew: string;
}

export class IErwerbspensumStateParams implements IStateParamsService {
    gesuchstellerNumber: string;
    erwerbspensumNum: string;
}

export class IEinkommensverschlechterungStateParams implements IStateParamsService {
    gesuchstellerNumber: string;
    basisjahrPlus: string;
}

export class IEinkommensverschlechterungResultateStateParams implements IStateParamsService {
    basisjahrPlus: string;
}
