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

    isJugendamt(): boolean {
        return true;
    }

    isGesuchstellerRole(): boolean {
        return false; // team wir haben gesagt ein user hat immer nur eine Rolle. Daher gilt wenn Rolle Gesuchsteller ist hat er nur diese Rolle.
    }

    isInstitutionRole(): boolean {
        return false;
    }
}
