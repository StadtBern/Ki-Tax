import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSKindContainer from '../../../models/TSKindContainer';
let template = require('./betreuungView.html');

export class BetreuungViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = BetreuungViewController;
    controllerAs = 'vm';
}


export class BetreuungViewController extends AbstractGesuchViewController {

    static $inject = ['$state', 'GesuchModelManager'];
    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager) {
        super(state, gesuchModelManager);
    }

    public getKindModel(): TSKindContainer {
        return this.gesuchModelManager.getKindToWorkWith();
    }

    submit(): void {
    }

    cancel() {
        this.removeBetreuungFromKind();
        this.state.go('gesuch.betreuungen');
    }

    private removeBetreuungFromKind(): void {
        if (!this.gesuchModelManager.getBetreuungToWorkWith().timestampErstellt) {
            //wenn das Kind noch nicht erstellt wurde, löschen wir das Kind vom Array
            this.gesuchModelManager.removeBetreuungFromKind();
        }
    }
}
