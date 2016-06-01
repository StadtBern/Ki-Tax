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

    static $inject = ['$state', 'GesuchModelManager', 'BerechnungsManager'];
    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager) {
        super(state, gesuchModelManager, berechnungsManager);
    }


    public getGesuchModel(): TSGesuch {
        return this.gesuchModelManager.gesuch;
    }

    submit(form: IFormController) {
        if (form.$valid) {
            this.gesuchModelManager.createFallWithGesuch().then((response: any) => {
                this.state.go('gesuch.familiensituation');
            });
        }
    }

    /**
     * Calls getGesuchsperiode with the Gesuchsperiode of the current Gesuch
     * @returns {string}
     */
    public getCurrentGesuchsperiode(): string {
        return this.getGesuchsperiode(this.gesuchModelManager.getGesuchsperiode());
    }
    /**
     * Takes the given Gesuchsperiode and returns a string with the format "gueltigAb.year/gueltigBis.year" 
     * @returns {any}
     */
    private getGesuchsperiode(gesuchsperiode: TSGesuchsperiode): string {
        if (gesuchsperiode && gesuchsperiode.gueltigkeit) {
            return gesuchsperiode.gueltigkeit.gueltigAb.year() + '/'
                + gesuchsperiode.gueltigkeit.gueltigBis.year();
        }
        return '';
    }

    public getAllActiveGesuchsperioden() {
        // this.gesuchModelManager.getAllActiveGesuchsperioden().forEach((gesuchsPeriode) => {
        //    
        // });
        return this.gesuchModelManager.getAllActiveGesuchsperioden();
    }

}
