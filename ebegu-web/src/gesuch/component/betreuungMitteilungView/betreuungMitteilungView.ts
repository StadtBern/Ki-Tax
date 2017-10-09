/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSBetreuung from '../../../models/TSBetreuung';
import BerechnungsManager from '../../service/berechnungsManager';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {IStateService} from 'angular-ui-router';
import IScope = angular.IScope;
import IFormController = angular.IFormController;
import ITimeoutService = angular.ITimeoutService;

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

    static $inject = ['$state', 'GesuchModelManager', '$scope', 'BerechnungsManager', 'WizardStepManager', '$timeout'];

    /* @ngInject */
    constructor(private $state: IStateService, gesuchModelManager: GesuchModelManager, $scope: IScope,
                berechnungsManager: BerechnungsManager, wizardStepManager: WizardStepManager, $timeout: ITimeoutService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.BETREUUNG, $timeout);
    }

    public cancel(): void {
        this.$state.go('gesuch.betreuungen', {gesuchId: this.getGesuchId()});
    }
}
