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
import * as moment from 'moment';
import {FerieninselStammdatenRS} from '../../../admin/service/ferieninselStammdatenRS.rest';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import ErrorService from '../../../core/errors/service/ErrorService';
import MitteilungRS from '../../../core/service/mitteilungRS.rest';
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';
import {getTSFeriennameValues, TSFerienname} from '../../../models/enums/TSFerienname';
import TSBelegungFerieninsel from '../../../models/TSBelegungFerieninsel';
import TSBelegungFerieninselTag from '../../../models/TSBelegungFerieninselTag';
import TSBetreuung from '../../../models/TSBetreuung';
import TSFerieninselStammdaten from '../../../models/TSFerieninselStammdaten';
import TSFerieninselZeitraum from '../../../models/TSFerieninselZeitraum';
import DateUtil from '../../../utils/DateUtil';
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
import {RemoveDialogController} from '../../dialog/RemoveDialogController';

let template = require('./betreuungFerieninselView.html');
require('./betreuungFerieninselView.less');
let dialogTemplate = require('../../dialog/removeDialogTemplate.html');

export class BetreuungFerieninselViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        betreuung: '=',
        onSave: '&'
    };
    template = template;
    controller = BetreuungFerieninselViewController;
    controllerAs = 'vm';
}

export class BetreuungFerieninselViewController extends BetreuungViewController {

    betreuung: TSBetreuung;
    onSave: () => void;

    ferieninselStammdaten: TSFerieninselStammdaten;
    ferieninselTage: TSBelegungFerieninselTag[] = [];


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
        if (EbeguUtil.isNullOrUndefined(this.betreuung.belegungFerieninsel)) {
            this.betreuung.betreuungsstatus = TSBetreuungsstatus.SCHULAMT_ANMELDUNG_ERFASST;
            this.betreuung.belegungFerieninsel = new TSBelegungFerieninsel();
            this.betreuung.belegungFerieninsel.tage = [];
        } else {
            this.changedFerien();
        }
    }

    public changedFerien() {
        this.ferieninselStammdatenRS.findFerieninselStammdatenByGesuchsperiodeAndFerien(
            this.gesuchModelManager.getGesuchsperiode().id, this.betreuung.belegungFerieninsel.ferienname).then((response: TSFerieninselStammdaten) => {
            this.ferieninselStammdaten = response;
            this.createPossibleFerieninselTageList();
        });
    }

    private createPossibleFerieninselTageList(): void {
        // TODO (hefr) Diese Logik muss auf den Server gezuegelt werden: Wochentage und Feiertage beruecksichtigen
        this.ferieninselTage = [];
        if (this.ferieninselStammdaten) {
            if (this.ferieninselStammdaten.zeitraum) {
                this.createPossibleFerieninselTageForZeitraum(this.ferieninselStammdaten.zeitraum);
            }
            if (this.ferieninselStammdaten.zeitraumList) {
                for (let zeitraum of this.ferieninselStammdaten.zeitraumList) {
                    this.createPossibleFerieninselTageForZeitraum(zeitraum);
                }
            }
        }
    }

    private createPossibleFerieninselTageForZeitraum(zeitraum: TSFerieninselZeitraum): void {
        let currentDate: moment.Moment = zeitraum.gueltigkeit.gueltigAb;
        while (!currentDate.isAfter(zeitraum.gueltigkeit.gueltigBis)) {
            let newDay: TSBelegungFerieninselTag = new TSBelegungFerieninselTag();
            newDay.tag = angular.copy(currentDate);
            this.ferieninselTage.push(newDay);
            currentDate = currentDate.add(1, 'days');
        }
    }

    public isAnmeldungNichtFreigegeben(): boolean {
        // Ferien sind ausgewaehlt, aber es gibt keine Stammdaten dazu
        return EbeguUtil.isNotNullOrUndefined(this.betreuung.belegungFerieninsel.ferienname)
            && EbeguUtil.isNullOrUndefined(this.ferieninselStammdaten);
    }

    public isAnmeldeschlussAbgelaufen(): boolean {
        // Ferien sind ausgewaehlt, es gibt Stammdaten, aber das Anmeldedatum ist abgelaufen
        return EbeguUtil.isNotNullOrUndefined(this.betreuung.belegungFerieninsel.ferienname)
            && EbeguUtil.isNotNullOrUndefined(this.ferieninselStammdaten)
            && this.ferieninselStammdaten.anmeldeschluss.isBefore(DateUtil.today());
    }

    public isAnmeldungMoeglich(): boolean {
        return EbeguUtil.isNotNullOrUndefined(this.betreuung.belegungFerieninsel.ferienname)
            && !this.isAnmeldeschlussAbgelaufen();
    }

    public anmelden() {
        return this.dvDialog.showDialog(dialogTemplate, RemoveDialogController, {
            title: 'CONFIRM_SAVE_FERIENINSEL',
            deleteText: 'BESCHREIBUNG_SAVE_FERIENINSEL',
            parentController: undefined,
            elementID: undefined
        }).then(() => {
            this.betreuung.belegungFerieninsel.tage = [];
            for (let tag of this.ferieninselTage) {
                if (tag.angemeldet) {
                    this.betreuung.belegungFerieninsel.tage.push(tag);
                }
            }
            this.onSave();
        });
    }
}
