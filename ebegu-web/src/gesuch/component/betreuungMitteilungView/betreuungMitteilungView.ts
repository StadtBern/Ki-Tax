import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSBetreuung from '../../../models/TSBetreuung';
import BerechnungsManager from '../../service/berechnungsManager';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {IStateService} from 'angular-ui-router';
import * as moment from 'moment';
import Moment = moment.Moment;
import IScope = angular.IScope;
import IFormController = angular.IFormController;
let template = require('./betreuungMitteilungView.html');
require('./betreuungMitteilungView.less');

export class BetreuungMitteilungViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = BetreuungMitteilungViewController;
    controllerAs = 'vm';
}

export class BetreuungMitteilungViewController extends AbstractGesuchViewController<TSBetreuung> {

    form: IFormController;

    static $inject = ['$state', 'GesuchModelManager', '$scope', 'BerechnungsManager', 'WizardStepManager'];
    /* @ngInject */
    constructor(private $state: IStateService, gesuchModelManager: GesuchModelManager, $scope: IScope,
                berechnungsManager: BerechnungsManager, wizardStepManager: WizardStepManager) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.BETREUUNG);
    }

    public cancel(): void {
        this.$state.go('gesuch.betreuungen', { gesuchId: this.getGesuchId() });
    }
}
