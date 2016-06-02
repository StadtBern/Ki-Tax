import {IComponentOptions, IFormController} from 'angular';
import {IStateService} from 'angular-ui-router';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import TSGesuch from '../../../models/TSGesuch';
let template = require('./fallCreationView.html');

export class FallCreationViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = FallCreationViewController;
    controllerAs = 'vm';
}

export class FallCreationViewController extends AbstractGesuchViewController {
    private gesuchsperiodeId: string;

    static $inject = ['$state', 'GesuchModelManager', 'BerechnungsManager'];
    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager) {
        super(state, gesuchModelManager, berechnungsManager);
        this.initViewModel();
    }

    private initViewModel(): void {
        this.gesuchModelManager.initGesuch();
        if (this.gesuchModelManager.getGesuchsperiode()) {
            this.gesuchsperiodeId = this.gesuchModelManager.getGesuchsperiode().id;
        }
    }

    public getGesuchModel(): TSGesuch {
        return this.gesuchModelManager.gesuch;
    }

    submit(form: IFormController) {
        if (!this.gesuchModelManager.isGesuchSaved() && form.$valid) {
            this.gesuchModelManager.createFallWithGesuch().then((response: any) => {
                this.state.go('gesuch.familiensituation');
            });
        } else if (this.gesuchModelManager.isGesuchSaved()) { // when the Gesuch is saved, we just move to the next step
            this.state.go('gesuch.familiensituation');
        }
    }

    /**
     * Calls getGesuchsperiodeAsString with the Gesuchsperiode of the current Gesuch
     * @returns {string}
     */
    public getCurrentGesuchsperiodeAsString(): string {
        return this.getGesuchsperiodeAsString(this.gesuchModelManager.getGesuchsperiode());
    }
    /**
     * Takes the given Gesuchsperiode and returns a string with the format "gueltigAb.year/gueltigBis.year"
     * @returns {any}
     */
    private getGesuchsperiodeAsString(gesuchsperiode: TSGesuchsperiode): string {
        if (gesuchsperiode && gesuchsperiode.gueltigkeit) {
            return gesuchsperiode.gueltigkeit.gueltigAb.year() + '/'
                + gesuchsperiode.gueltigkeit.gueltigBis.year();
        }
        return undefined;
    }

    public getAllActiveGesuchsperioden() {
        return this.gesuchModelManager.getAllActiveGesuchsperioden();
    }

    public setSelectedGesuchsperiode(): void {
        let gesuchsperiodeList = this.getAllActiveGesuchsperioden();
        for (let i: number = 0; i < gesuchsperiodeList.length; i++) {
            if (gesuchsperiodeList[i].id === this.gesuchsperiodeId) {
                this.getGesuchModel().gesuchsperiode = gesuchsperiodeList[i];
            }
        }
    }

}
