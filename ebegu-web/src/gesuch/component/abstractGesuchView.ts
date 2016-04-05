/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.GesuchView {

    export class AbstractGesuchViewController {

        state: angular.ui.IStateService;

        constructor($state: angular.ui.IStateService) {
            this.state = $state;
        }

        submit(form: angular.IFormController): void {}

        previousStep(): void {}

        isAdminRole(): boolean {
            return true; // todo team wenn die Role definiert sind hier muss die Rolle berechnet werden
        }
    }
}
