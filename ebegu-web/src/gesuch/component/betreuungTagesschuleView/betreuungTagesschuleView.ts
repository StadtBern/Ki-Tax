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
import {IStateService} from 'angular-ui-router';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import ErrorService from '../../../core/errors/service/ErrorService';
import MitteilungRS from '../../../core/service/mitteilungRS.rest';
import TSBetreuung from '../../../models/TSBetreuung';
import EbeguUtil from '../../../utils/EbeguUtil';
import {IBetreuungStateParams} from '../../gesuch.route';
import BerechnungsManager from '../../service/berechnungsManager';
import GesuchModelManager from '../../service/gesuchModelManager';
import WizardStepManager from '../../service/wizardStepManager';
import {BetreuungViewController} from '../betreuungView/betreuungView';
import ILogService = angular.ILogService;
import IScope = angular.IScope;
import ITimeoutService = angular.ITimeoutService;
import ITranslateService = angular.translate.ITranslateService;
import TSInstitutionStammdaten from '../../../models/TSInstitutionStammdaten';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import DateUtil from '../../../utils/DateUtil';

let template = require('./betreuungTagesschuleView.html');
require('./betreuungTagesschuleView.less');
let dialogTemplate = require('../../dialog/removeDialogTemplate.html');

export class BetreuungTagesschuleViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        betreuung: '=',
        instStammId: '=',
        tagesschuleList: '&'
    };
    template = template;
    controller = BetreuungTagesschuleViewController;
    controllerAs = 'vm';
}

export class BetreuungTagesschuleViewController extends BetreuungViewController {

    instStammId: string;
    betreuung: TSBetreuung;
    tagesschuleList: () => Array<TSInstitutionStammdaten>;


    static $inject = ['$state', 'GesuchModelManager', 'EbeguUtil', 'CONSTANTS', '$scope', 'BerechnungsManager', 'ErrorService',
        'AuthServiceRS', 'WizardStepManager', '$stateParams', 'MitteilungRS', 'DvDialog', '$log', '$timeout', '$translate'];
    /* @ngInject */
    constructor($state: IStateService, gesuchModelManager: GesuchModelManager, ebeguUtil: EbeguUtil, CONSTANTS: any,
                $scope: IScope, berechnungsManager: BerechnungsManager, errorService: ErrorService,
                authServiceRS: AuthServiceRS, wizardStepManager: WizardStepManager, $stateParams: IBetreuungStateParams,
                mitteilungRS: MitteilungRS, dvDialog: DvDialog, $log: ILogService,
                $timeout: ITimeoutService, $translate: ITranslateService) {
        super($state, gesuchModelManager, ebeguUtil, CONSTANTS, $scope, berechnungsManager, errorService, authServiceRS,
            wizardStepManager, $stateParams, mitteilungRS, dvDialog, $log, $timeout, $translate);
    }

    $onInit() {
        this.model = this.betreuung;
    }

    public getTagesschuleAnmeldungNotYetReadyText(): string {
        let gp: TSGesuchsperiode = this.gesuchModelManager.getGesuch().gesuchsperiode;
        if (gp.hasTagesschulenAnmeldung()) {
            if (gp.isTagesschulenAnmeldungKonfiguriert()) {
                let terminValue: string = DateUtil.momentToLocalDateFormat(gp.datumFreischaltungTagesschule, 'DD.MM.YYYY');
                return this.$translate.instant('FREISCHALTUNG_TAGESSCHULE_AB_INFO', {
                    termin: terminValue
                });
            } else {
                return this.$translate.instant('FREISCHALTUNG_TAGESSCHULE_INFO');
            }
        }
        return '';
    }
}
