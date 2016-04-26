import {IComponentOptions} from 'angular';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
let template = require('./kinderView.html');

export class KinderViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = KinderViewController;
    controllerAs = 'vm';
}

export class KinderViewController {

    static $inject: string[] = ['$state', 'GesuchModelManager'];
    /* @ngInject */
    constructor(private state: IStateService, private gesuchModelManager: GesuchModelManager) {

    }


    previousStep() {
        if ((this.gesuchModelManager.gesuchstellerNumber === 2)) {
            this.state.go('gesuch.stammdaten', {gesuchstellerNumber: 2});
        } else {
            this.state.go('gesuch.stammdaten', {gesuchstellerNumber: 1});
        }
    }
}
