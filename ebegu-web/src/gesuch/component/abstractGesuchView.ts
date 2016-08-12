import {IStateService} from 'angular-ui-router';
import {IFormController} from 'angular';
import GesuchModelManager from '../service/gesuchModelManager';
import BerechnungsManager from '../service/berechnungsManager';
import {TSRole} from '../../models/enums/TSRole';

export default class AbstractGesuchViewController {

    state: IStateService;
    gesuchModelManager: GesuchModelManager;
    berechnungsManager: BerechnungsManager;
    TSRole: any;

    constructor($state: IStateService, $gesuchModelManager: GesuchModelManager, $berechnungsManager: BerechnungsManager) {
        this.state = $state;
        this.gesuchModelManager = $gesuchModelManager;
        this.berechnungsManager = $berechnungsManager;
        this.TSRole = TSRole;
    }

    submit(form: IFormController): void {
    }

}
