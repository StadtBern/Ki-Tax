import {RouterHelper} from '../dvbModules/router/route-helper-provider';
import {IState, IStateParamsService} from 'angular-ui-router';
import {GesuchRouteController} from './gesuch';
import GesuchModelManager from './service/gesuchModelManager';
import TSGesuch from '../models/TSGesuch';
import BerechnungsManager from './service/berechnungsManager';
import WizardStepManager from './service/wizardStepManager';
import MahnungRS from './service/mahnungRS.rest';
import {TSEingangsart} from '../models/enums/TSEingangsart';
import IPromise = angular.IPromise;
import IQService = angular.IQService;
import ILogService = angular.ILogService;
let gesuchTpl = require('./gesuch.html');

gesuchRun.$inject = ['RouterHelper'];
/* @ngInject */
export function gesuchRun(routerHelper: RouterHelper) {
    routerHelper.configureStates(getStates(), '/start');
}


//array mit allen States
function getStates(): IState[] {
    return [
        new EbeguGesuchState(),
        new EbeguFamiliensituationState(),
        new EbeguStammdatenState(),
        new EbeguUmzugState(),
        new EbeguKinderListState(),
        new EbeguFinanzielleSituationStartState(),
        new EbeguFinanzielleSituationState(),
        new EbeguFinanzielleSituationResultateState(),
        new EbeguKindState(),
        new EbeguErwerbspensenListState(),
        new EbeguErwerbspensumState(),
        new EbeguBetreuungListState(),
        new EbeguBetreuungState(),
        new EbeguAbwesenheitState(),
        new EbeguNewFallState(),
        new EbeguMutationState(),
        new EbeguVerfuegenListState(),
        new EbeguVerfuegenState(),
        new EbeguEinkommensverschlechterungInfoState(),
        new EbeguEinkommensverschlechterungSteuernState(),
        new EbeguEinkommensverschlechterungState(),
        new EbeguEinkommensverschlechterungResultateState(),
        new EbeguDokumenteState(),
        new EbeguFreigabeState()
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
    url = '/fall/:createNew/:eingangsart/:gesuchsperiodeId/:gesuchId/:fallId';

    views: {[name: string]: IState} = {
        'gesuchViewPort': {
            template: '<fall-creation-view>'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: reloadGesuchModelManager
    };
}

export class EbeguMutationState implements IState {
    name = 'gesuch.mutation';
    url = '/mutation/:createMutation/:eingangsart/:gesuchsperiodeId/:gesuchId/:fallId';

    views: {[name: string]: IState} = {
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

    views: {[name: string]: IState} = {
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

    views: {[name: string]: IState} = {
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

export class EbeguUmzugState implements IState {
    name = 'gesuch.umzug';
    url = '/umzug/:gesuchId';

    views: {[name: string]: IState} = {
        'gesuchViewPort': {
            template: '<umzug-view>'
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

    views: {[name: string]: IState} = {
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

    views: {[name: string]: IState} = {
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

    views: {[name: string]: IState} = {
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
    url = '/betreuungen/betreuung/:gesuchId/:kindNumber/:betreuungNumber';

    views: {[name: string]: IState} = {
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

export class EbeguAbwesenheitState implements IState {
    name = 'gesuch.abwesenheit';
    url = '/abwesenheit/:gesuchId';

    views: {[name: string]: IState} = {
        'gesuchViewPort': {
            template: '<abwesenheit-view>'
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

    views: {[name: string]: IState} = {
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

    views: {[name: string]: IState} = {
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

    views: {[name: string]: IState} = {
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

    views: {[name: string]: IState} = {
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

    views: {[name: string]: IState} = {
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

    views: {[name: string]: IState} = {
        'gesuchViewPort': {
            template: '<verfuegen-list-view mahnung-list="$resolve.mahnungList">'
        },
        'kommentarViewPort': {
            template: '<kommentar-view>'
        }
    };

    resolve = {
        gesuch: getGesuchModelManager,
        mahnungList: getMahnungen
    };
}

export class EbeguVerfuegenState implements IState {
    name = 'gesuch.verfuegenView';
    url = '/verfuegenView/:gesuchId/:betreuungNumber/:kindNumber';

    views: {[name: string]: IState} = {
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

    views: {[name: string]: IState} = {
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

    views: {[name: string]: IState} = {
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

    views: {[name: string]: IState} = {
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

    views: {[name: string]: IState} = {
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

    views: {[name: string]: IState} = {
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

export class EbeguFreigabeState implements IState {
    name = 'gesuch.freigabe';
    url = '/freigabe/:gesuchId';

    views: {[name: string]: IState} = {
        'gesuchViewPort': {
            template: '<freigabe-view>'
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

export class IBetreuungStateParams implements IStateParamsService {
    betreuungNumber: string;
    kindNumber: string;
}


export class INewFallStateParams implements IStateParamsService {
    createNew: string;
    createMutation: string;
    eingangsart: TSEingangsart;
    gesuchsperiodeId: string;
    gesuchId: string;
    fallId: string;
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


// FIXME dieses $inject wird ignoriert, d.h, der Parameter der Funktion muss exact dem Namen des Services entsprechen (Grossbuchstaben am Anfang). Warum?
getMahnungen.$inject = ['MahnungRS', '$stateParams', '$q', '$log'];
/* @ngInject */
export function getMahnungen(MahnungRS: MahnungRS, $stateParams: IGesuchStateParams, $q: IQService, $log: ILogService) {
    // return [];
    if ($stateParams) {
        let gesuchIdParam = $stateParams.gesuchId;
        if (gesuchIdParam) {
            return MahnungRS.findMahnungen(gesuchIdParam);
        }
    }
    $log.warn('keine stateParams oder keine gesuchId, gebe undefined zurueck');
    let deferred = $q.defer();
    deferred.resolve(undefined);
    return deferred.promise;
}


getGesuchModelManager.$inject = ['GesuchModelManager', 'BerechnungsManager', 'WizardStepManager', '$stateParams', '$q', '$log'];
/* @ngInject */
export function getGesuchModelManager(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                                      wizardStepManager: WizardStepManager, $stateParams: IGesuchStateParams, $q: IQService, $log: ILogService): IPromise<TSGesuch> {
    if ($stateParams) {
        let gesuchIdParam = $stateParams.gesuchId;
        if (gesuchIdParam) {
            if (!gesuchModelManager.getGesuch() || gesuchModelManager.getGesuch() && gesuchModelManager.getGesuch().id !== gesuchIdParam) {
                // Wenn die antrags id im GescuchModelManager nicht mit der GesuchId ueberreinstimmt wird das gesuch neu geladen
                berechnungsManager.clear();
                return gesuchModelManager.openGesuch(gesuchIdParam);
            } else {
                let deferred = $q.defer();
                deferred.resolve(gesuchModelManager.getGesuch());
                return deferred.promise;
            }

        }
    }
    $log.warn('keine stateParams oder keine gesuchId, gebe undefined zurueck');
    let deferred = $q.defer();
    deferred.resolve(undefined);
    return deferred.promise;
}

reloadGesuchModelManager.$inject = ['GesuchModelManager', 'BerechnungsManager', 'WizardStepManager', '$stateParams', '$q'];
/* @ngInject */
export function reloadGesuchModelManager(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                                         wizardStepManager: WizardStepManager, $stateParams: INewFallStateParams, $q: any): IPromise<TSGesuch> {
    if ($stateParams) {
        let gesuchIdParams = $stateParams.gesuchId;
        if (gesuchIdParams) {
            if ($stateParams.createNew !== 'true') {
                berechnungsManager.clear();
                return gesuchModelManager.openGesuch(gesuchIdParams);
            } else {
                let eingangsart = $stateParams.eingangsart;
                let gesuchsperiodeId = $stateParams.gesuchsperiodeId;
                let fallId = $stateParams.fallId;
                gesuchModelManager.initGesuchWithEingangsart(true, eingangsart, gesuchsperiodeId, fallId);
                return gesuchModelManager.openGesuch(gesuchIdParams);
            }
        }
    }
    return $q.defer(gesuchModelManager.getGesuch());
}

createEmptyMutation.$inject = ['GesuchModelManager', '$stateParams', '$q'];
export function createEmptyMutation(gesuchModelManager: GesuchModelManager, $stateParams: INewFallStateParams, $q: any): IPromise<TSGesuch> {
    if ($stateParams) {
        let gesuchId = $stateParams.gesuchId;
        let eingangsart = $stateParams.eingangsart;
        let gesuchsperiodeId = $stateParams.gesuchsperiodeId;
        let fallId = $stateParams.fallId;
        if (gesuchId && eingangsart) {
            gesuchModelManager.initMutation(gesuchId, eingangsart, gesuchsperiodeId, fallId);
        }
    }
    return $q.defer(gesuchModelManager.getGesuch());
}
