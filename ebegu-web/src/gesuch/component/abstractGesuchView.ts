import {IStateService} from 'angular-ui-router';
import GesuchModelManager from '../service/gesuchModelManager';
import BerechnungsManager from '../service/berechnungsManager';
import {TSRole} from '../../models/enums/TSRole';
import {TSRoleUtil} from '../../utils/TSRoleUtil';
import WizardStepManager from '../service/wizardStepManager';

export default class AbstractGesuchViewController {

    state: IStateService;
    gesuchModelManager: GesuchModelManager;
    berechnungsManager: BerechnungsManager;
    wizardStepManager: WizardStepManager;
    TSRole: any;
    TSRoleUtil: any;

    constructor($state: IStateService, $gesuchModelManager: GesuchModelManager, $berechnungsManager: BerechnungsManager,
                wizardStepManager: WizardStepManager) {
        this.state = $state;
        this.gesuchModelManager = $gesuchModelManager;
        this.berechnungsManager = $berechnungsManager;
        this.wizardStepManager = wizardStepManager;
        this.TSRole = TSRole;
        this.TSRoleUtil = TSRoleUtil;
    }

}
