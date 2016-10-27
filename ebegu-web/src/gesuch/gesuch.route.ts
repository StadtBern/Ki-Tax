import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState, IStateParamsService} from 'angular-ui-router';
import {GesuchRouteController} from './gesuch';
import GesuchModelManager from './service/gesuchModelManager';
import TSGesuch from '../models/TSGesuch';
import BerechnungsManager from './service/berechnungsManager';
import WizardStepManager from './service/wizardStepManager';
import IPromise = angular.IPromise;
let gesuchTpl = require('./gesuch.html');

gesuchRun.$inject = ['RouterHelper'];
/* @ngInject */
export function gesuchRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/login');
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
        new EbeguMutationState(),
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

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguMutationState implements IState {
    name = 'gesuch.mutation';
    url = '/mutation/:createMutation/:gesuchId';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<fall-creation-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: createEmptyMutation
    };
}

export class EbeguFamiliensituationState implements IState {
    name = 'gesuch.familiensituation';
    url = '/familiensituation/:gesuchId';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<familiensituation-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguStammdatenState implements IState {
    name = 'gesuch.stammdaten';
    url = '/stammdaten/:gesuchId/:gesuchstellerNumber';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<stammdaten-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguKinderListState implements IState {
    name = 'gesuch.kinder';
    url = '/kinder/:gesuchId';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<kinder-list-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguKindState implements IState {
    name = 'gesuch.kind';
    url = '/kinder/kind/:gesuchId/:kindNumber';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<kind-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguBetreuungListState implements IState {
    name = 'gesuch.betreuungen';
    url = '/betreuungen/:gesuchId';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<betreuung-list-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguBetreuungState implements IState {
    name = 'gesuch.betreuung';
    url = '/betreuungen/betreuung/:gesuchId';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<betreuung-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguErwerbspensenListState implements IState {
    name = 'gesuch.erwerbsPensen';
    url = '/erwerbspensen/:gesuchId';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<erwerbspensum-list-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguErwerbspensumState implements IState {
    name = 'gesuch.erwerbsPensum';
    url = '/erwerbspensen/erwerbspensum/:gesuchId/:gesuchstellerNumber/:erwerbspensumNum';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<erwerbspensum-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguFinanzielleSituationState implements IState {
    name = 'gesuch.finanzielleSituation';
    url = '/finanzielleSituation/:gesuchId/:gesuchstellerNumber';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<finanzielle-situation-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguFinanzielleSituationStartState implements IState {
    name = 'gesuch.finanzielleSituationStart';
    url = '/finanzielleSituationStart/:gesuchId';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<finanzielle-situation-start-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguFinanzielleSituationResultateState implements IState {
    name = 'gesuch.finanzielleSituationResultate';
    url = '/finanzielleSituationResultate/:gesuchId';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<finanzielle-situation-resultate-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguVerfuegenListState implements IState {
    name = 'gesuch.verfuegen';
    url = '/verfuegen/:gesuchId';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<verfuegen-list-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguVerfuegenState implements IState {
    name = 'gesuch.verfuegenView';
    url = '/verfuegenView/:gesuchId';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<verfuegen-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguEinkommensverschlechterungInfoState implements IState {
    name = 'gesuch.einkommensverschlechterungInfo';
    url = '/einkommensverschlechterungInfo/:gesuchId';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<einkommensverschlechterung-info-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguEinkommensverschlechterungSteuernState implements IState {
    name = 'gesuch.einkommensverschlechterungSteuern';
    url = '/einkommensverschlechterungSteuern/:gesuchId';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<einkommensverschlechterung-steuern-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguEinkommensverschlechterungState implements IState {
    name = 'gesuch.einkommensverschlechterung';
    url = '/einkommensverschlechterung/:gesuchId/:gesuchstellerNumber/:basisjahrPlus';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<einkommensverschlechterung-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguEinkommensverschlechterungResultateState implements IState {
    name = 'gesuch.einkommensverschlechterungResultate';
    url = '/einkommensverschlechterungResultate/:gesuchId/:basisjahrPlus';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<einkommensverschlechterung-resultate-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
    };
}

export class EbeguDokumenteState implements IState {
    name = 'gesuch.dokumente';
    url = '/dokumente/:gesuchId/:gesuchstellerNumber';

    views: { [name: string]: IState } = {
        'gesuchViewPort': {
            template: '<dokumente-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager
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

export class INewFallStateParams implements IStateParamsService {
    createNew: string;
    createMutation: string;
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


getGesuchModelManager.$inject = ['GesuchModelManager', 'BerechnungsManager', 'WizardStepManager', '$stateParams', '$q'];
/* @ngInject */
export function getGesuchModelManager(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                                      wizardStepManager: WizardStepManager, $stateParams: IGesuchStateParams, $q: any): IPromise<TSGesuch> {
    if ($stateParams) {
        let gesuchIdParams = $stateParams.gesuchId;
        if (gesuchIdParams) {
            if (!gesuchModelManager.getGesuch() || gesuchModelManager.getGesuch() && gesuchModelManager.getGesuch().id !== gesuchIdParams) {
                // Wenn die antrags id im GescuchModelManager nicht mit der GesuchId ueberreinstimmt wird das gesuch neu geladen
                berechnungsManager.clear();
                wizardStepManager.findStepsFromGesuch(gesuchIdParams);
                return gesuchModelManager.openGesuch(gesuchIdParams);
            }
        }
    }
    return $q.defer(gesuchModelManager.getGesuch());
}

createEmptyMutation.$inject = ['GesuchModelManager', '$stateParams', '$q'];
export function createEmptyMutation(gesuchModelManager: GesuchModelManager, $stateParams: IGesuchStateParams, $q: any): IPromise<TSGesuch> {
    if ($stateParams) {
        let gesuchId = $stateParams.gesuchId;
        if (gesuchId) {
            gesuchModelManager.initMutation(gesuchId);
        }
    }
    return $q.defer(gesuchModelManager.getGesuch());
}
