import {IStateService} from 'angular-ui-router';
import {IFormController} from 'angular';
import GesuchModelManager from '../service/gesuchModelManager';

export default class AbstractGesuchViewController {

    state: IStateService;
    gesuchModelManager: GesuchModelManager;

    constructor($state: IStateService, $gesuchModelManager: GesuchModelManager) {
        this.state = $state;
        this.gesuchModelManager = $gesuchModelManager;
    }

    submit(form: IFormController): void {
    }

    previousStep(): void {
    }

    isAdminRole(): boolean {
        return true; // todo team wenn die Role definiert sind hier muss die Rolle berechnet werden
    }
}
