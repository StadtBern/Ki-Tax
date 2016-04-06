import {IStateService} from 'angular-ui-router';
import {IFormController} from 'angular';

export default class AbstractGesuchViewController {

    state: IStateService;

    constructor($state: IStateService) {
        this.state = $state;
    }

    submit(form: IFormController): void {
    }

    previousStep(): void {
    }

    isAdminRole(): boolean {
        return true; // todo team wenn die Role definiert sind hier muss die Rolle berechnet werden
    }
}
