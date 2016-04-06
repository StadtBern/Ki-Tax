import AbstractGesuchViewController from '../abstractGesuchView';
import {IComponentOptions, IFormController} from 'angular';
import {IStateService} from 'angular-ui-router';
import TSGesuch from '../../../models/TSGesuch';

class FamiliensituationViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    templateUrl = 'src/gesuch/component/familiensituationView/familiensituationView.html';
    controller = FamiliensituationViewController;
    controllerAs = 'vm';
}


class FamiliensituationViewController extends AbstractGesuchViewController {
    static $inject = ['$state'];

    gesuch: TSGesuch;

    /* @ngInject */
    constructor($state: IStateService) {
        super($state);
        this.gesuch = new TSGesuch();
    }

    submit($form: IFormController) {
        if ($form.$valid) {
            this.state.go('gesuch.stammdaten');
        }
    }

    showBeantragen(): boolean {
        return this.gesuch.familiensituation === 'ALLEINERZIEHEND' || this.gesuch.familiensituation === 'WENIGER_FUENF_JAHRE';
    }
}

angular.module('ebeguWeb.gesuch').component('familiensituationView', new FamiliensituationViewComponentConfig());
