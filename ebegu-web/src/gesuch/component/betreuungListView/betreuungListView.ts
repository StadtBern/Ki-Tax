import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import AbstractGesuchViewController from '../abstractGesuchView';
import IDialogService = angular.material.IDialogService;
import GesuchModelManager from '../../service/gesuchModelManager';
let template = require('./betreuungListView.html');


export class BetreuungListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = BetreuungListViewController;
    controllerAs = 'vm';
}

export class BetreuungListViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$state', 'GesuchModelManager', '$mdDialog', 'DvDialog'];
    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager) {
        super(state, gesuchModelManager);
    }

    submit(): void {
        this.nextStep();
    }

    previousStep(): void {
        this.state.go('gesuch.kinder');
    }

    // TODO (team) vorübergehend direkt auf FinanzSit navigieren
    nextStep(): void  {
        this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: 1});
    }

}
