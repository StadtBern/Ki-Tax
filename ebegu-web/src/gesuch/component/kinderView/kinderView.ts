import {IComponentOptions} from 'angular';
import GesuchForm from '../../service/gesuchForm';
import {IStateService} from 'angular-ui-router';
let template = require('./kinderView.html');

export class KinderViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = KinderViewController;
    controllerAs = 'vm';
}

export class KinderViewController {

    static $inject: string[] = ['$state', 'GesuchForm'];
    /* @ngInject */
    constructor(private state: IStateService, private gesuchForm: GesuchForm) {

    }


    previousStep() {
        if ((this.gesuchForm.gesuchstellerNumber === 2)) {
            this.state.go('gesuch.stammdaten', {gesuchstellerNumber: 2});
        } else {
            this.state.go('gesuch.stammdaten', {gesuchstellerNumber: 1});
        }
    }
}
