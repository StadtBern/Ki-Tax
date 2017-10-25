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
import {getTSFeriennameValues, TSFerienname} from '../../../models/enums/TSFerienname';
import TSBelegungFerieninsel from '../../../models/TSBelegungFerieninsel';
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
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';
import {FerieninselStammdatenRS} from '../../../admin/service/ferieninselStammdatenRS.rest';
import TSFerieninselStammdaten from '../../../models/TSFerieninselStammdaten';
import DateUtil from '../../../utils/DateUtil';

let template = require('./betreuungFerieninselView.html');
require('./betreuungFerieninselView.less');
let removeDialogTemplate = require('../../dialog/removeDialogTemplate.html');

export class BetreuungFerieninselViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        betreuung: '='
    };
    template = template;
    controller = BetreuungFerieninselViewController;
    controllerAs = 'vm';
}

export class BetreuungFerieninselViewController extends BetreuungViewController {

    betreuung: TSBetreuung;
    ferieninselStammdaten: TSFerieninselStammdaten;

    //TODO (hefr) am schluss die injects aufraeumen
    static $inject = ['$state', 'GesuchModelManager', 'EbeguUtil', 'CONSTANTS', '$scope', 'BerechnungsManager', 'ErrorService',
        'AuthServiceRS', 'WizardStepManager', '$stateParams', 'MitteilungRS', 'DvDialog', '$log', '$timeout', '$translate', 'FerieninselStammdatenRS'];
    /* @ngInject */
    constructor($state: IStateService, gesuchModelManager: GesuchModelManager, ebeguUtil: EbeguUtil, CONSTANTS: any,
                $scope: IScope, berechnungsManager: BerechnungsManager, errorService: ErrorService,
                authServiceRS: AuthServiceRS, wizardStepManager: WizardStepManager, $stateParams: IBetreuungStateParams,
                mitteilungRS: MitteilungRS, dvDialog: DvDialog, $log: ILogService,
                $timeout: ITimeoutService, $translate: ITranslateService, private ferieninselStammdatenRS: FerieninselStammdatenRS) {
        super($state, gesuchModelManager, ebeguUtil, CONSTANTS, $scope, berechnungsManager, errorService, authServiceRS, wizardStepManager, $stateParams,
            mitteilungRS, dvDialog, $log, $timeout, $translate);
    }

    $onInit() {
        this.initFerieninselViewModel();
    }

    getFeriennamen(): Array<TSFerienname> {
        return getTSFeriennameValues();
    }

    private initFerieninselViewModel() {
        this.betreuung.betreuungsstatus = TSBetreuungsstatus.SCHULAMT;
        if (EbeguUtil.isNullOrUndefined(this.betreuung.belegungFerieninsel)) {
            this.betreuung.belegungFerieninsel = new TSBelegungFerieninsel();
        }
    }

    public changedFerien() {
        this.ferieninselStammdatenRS.findFerieninselStammdatenByGesuchsperiodeAndFerien(
            this.gesuchModelManager.getGesuchsperiode().id, this.betreuung.belegungFerieninsel.ferienname).then((response: TSFerieninselStammdaten) => {
            this.ferieninselStammdaten = response;
        });
        //TODO (hefr) Tage der Ferieninsel zur Auswahl stellen
    }

    public anmeldungNichtFreigegeben(): boolean {
        // Ferien sind ausgewaehlt, aber es gibt keine Stammdaten dazu
        return EbeguUtil.isNotNullOrUndefined(this.betreuung.belegungFerieninsel.ferienname)
            && EbeguUtil.isNullOrUndefined(this.ferieninselStammdaten);
    }

    public anmeldeschlussAbgelaufen(): boolean {
        // Ferien sind ausgewaehlt, es gibt Stammdaten, aber das Anmeldedatum ist abgelaufen
        return EbeguUtil.isNotNullOrUndefined(this.betreuung.belegungFerieninsel.ferienname)
            && EbeguUtil.isNotNullOrUndefined(this.ferieninselStammdaten)
            && this.ferieninselStammdaten.anmeldeschluss.isBefore(DateUtil.today());
    }
}
