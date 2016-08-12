import {IStateService} from 'angular-ui-router';
import {IFormController} from 'angular';
import GesuchModelManager from '../service/gesuchModelManager';
import BerechnungsManager from '../service/berechnungsManager';
import {TSRole} from '../../models/enums/TSRole';
import {TSRoleUtil} from '../../utils/TSRoleUtil';

export default class AbstractGesuchViewController {

    state: IStateService;
    gesuchModelManager: GesuchModelManager;
    berechnungsManager: BerechnungsManager;
    TSRole: any;
    TSRoleUtil: any;

    constructor($state: IStateService, $gesuchModelManager: GesuchModelManager, $berechnungsManager: BerechnungsManager) {
        this.state = $state;
        this.gesuchModelManager = $gesuchModelManager;
        this.berechnungsManager = $berechnungsManager;
        this.TSRole = TSRole;
        this.TSRoleUtil = TSRoleUtil;
    }

    submit(form: IFormController): void {
    }

    previousStep(): void {
    }

}
