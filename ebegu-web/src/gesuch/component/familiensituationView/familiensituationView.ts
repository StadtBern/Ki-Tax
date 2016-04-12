import AbstractGesuchViewController from "../abstractGesuchView";
import {IComponentOptions, IFormController} from "angular";
import {IStateService} from "angular-ui-router";
import TSGesuch from "../../../models/TSGesuch";
import * as template from "./familiensituationView.html";
import "./familiensituationView.less";

export class FamiliensituationViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = FamiliensituationViewController;
    controllerAs = 'vm';
}


export class FamiliensituationViewController extends AbstractGesuchViewController {
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
