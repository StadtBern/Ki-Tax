import {IStateService} from 'angular-ui-router';
import {IFormController} from 'angular';
import GesuchModelManager from '../service/gesuchModelManager';
import BerechnungsManager from '../service/berechnungsManager';

export default class AbstractGesuchViewController {

    state: IStateService;
    gesuchModelManager: GesuchModelManager;
    berechnungsManager: BerechnungsManager;

    constructor($state: IStateService, $gesuchModelManager: GesuchModelManager, $berechnungsManager: BerechnungsManager) {
        this.state = $state;
        this.gesuchModelManager = $gesuchModelManager;
        this.berechnungsManager = $berechnungsManager;
    }

    submit(form: IFormController): void {
    }

    previousStep(): void {
    }

    isAdminRole(): boolean {
        return true; // todo team wenn die Role definiert sind hier muss die Rolle berechnet werden
    }
}
