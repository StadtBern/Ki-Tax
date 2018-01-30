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

import {IComponentOptions, IFormController, IPromise} from 'angular';
import {IStateService} from 'angular-ui-router';
import {FerieninselStammdatenRS} from '../../../admin/service/ferieninselStammdatenRS.rest';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import ErrorService from '../../../core/errors/service/ErrorService';
import MitteilungRS from '../../../core/service/mitteilungRS.rest';
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';
import {getTSFeriennameValues, TSFerienname} from '../../../models/enums/TSFerienname';
import TSBelegungFerieninsel from '../../../models/TSBelegungFerieninsel';
import TSBetreuung from '../../../models/TSBetreuung';
import TSFerieninselStammdaten from '../../../models/TSFerieninselStammdaten';
import DateUtil from '../../../utils/DateUtil';
import EbeguUtil from '../../../utils/EbeguUtil';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import {IBetreuungStateParams} from '../../gesuch.route';
import BerechnungsManager from '../../service/berechnungsManager';
import GesuchModelManager from '../../service/gesuchModelManager';
import WizardStepManager from '../../service/wizardStepManager';
import {BetreuungViewController} from '../betreuungView/betreuungView';
import {TSAnmeldungMutationZustand} from '../../../models/enums/TSAnmeldungMutationZustand';
import * as moment from 'moment';
import TSBelegungFerieninselTag from '../../../models/TSBelegungFerieninselTag';
import ILogService = angular.ILogService;
import IScope = angular.IScope;
import ITimeoutService = angular.ITimeoutService;
import ITranslateService = angular.translate.ITranslateService;

declare let require: any;
let template = require('./betreuungFerieninselView.html');
require('./betreuungFerieninselView.less');
let dialogTemplate = require('../../dialog/removeDialogTemplate.html');

export class BetreuungFerieninselViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        betreuung: '=',
        onSave: '&',
        anmeldungSchulamtUebernehmen: '&',
        anmeldungSchulamtAblehnen: '&',
        anmeldungSchulamtFalscheInstitution: '&',
        cancel: '&',
        form: '='
    };
    template = template;
    controller = BetreuungFerieninselViewController;
    controllerAs = 'vm';
}

export class BetreuungFerieninselViewController extends BetreuungViewController {

    betreuung: TSBetreuung;
    onSave: () => void;
    form: IFormController;
    showErrorMessage: boolean;

    ferieninselStammdaten: TSFerieninselStammdaten;
    showNochNichtFreigegeben: boolean = false;
    showMutiert: boolean = false;
    aktuellGueltig: boolean = true;


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
        if (this.getBetreuungModel().anmeldungMutationZustand) {
            if (this.getBetreuungModel().anmeldungMutationZustand === TSAnmeldungMutationZustand.MUTIERT) {
                this.showMutiert = true;
                this.aktuellGueltig = false;
            } else if (this.getBetreuungModel().anmeldungMutationZustand === TSAnmeldungMutationZustand.NOCH_NICHT_FREIGEGEBEN) {
                this.showNochNichtFreigegeben = true;
                this.aktuellGueltig = false;
            }
        }
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
        if (this.betreuung.belegungFerieninsel && this.betreuung.belegungFerieninsel.ferienname) {
            // Die Stammdaten und potentiellen Ferientage der gewaehlten Ferieninsel lesen
            this.ferieninselStammdatenRS.findFerieninselStammdatenByGesuchsperiodeAndFerien(
                this.gesuchModelManager.getGesuchsperiode().id, this.betreuung.belegungFerieninsel.ferienname).then((response: TSFerieninselStammdaten) => {
                this.ferieninselStammdaten = response;
                // Bereits gespeicherte Daten wieder ankreuzen
                for (let obj of this.ferieninselStammdaten.potenzielleFerieninselTageFuerBelegung) {
                    for (let tagAngemeldet of this.betreuung.belegungFerieninsel.tage) {
                        if (tagAngemeldet.tag.isSame(obj.tag)) {
                            obj.angemeldet = true;
                        }
                    }
                }
            });
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
            && !this.isAnmeldeschlussAbgelaufen()
            && !this.isAnmeldungNichtFreigegeben();
    }

    public getButtonTextSpeichern(): string {
        return this.direktAnmeldenSchulamt() ? 'ANMELDEN_FERIENINSEL' : 'SPEICHERN_UPPER';
    }

    public anmelden(): IPromise<any> {
        if (this.form.$valid) {
            // Validieren, dass mindestens 1 Tag ausgewählt war
            this.setChosenFerientage();
            if (this.betreuung.belegungFerieninsel.tage.length <= 0) {
                if (this.isAnmeldungMoeglich()) {
                    this.showErrorMessage = true;
                }
                return undefined;
            }
            if (this.direktAnmeldenSchulamt()) {
                return this.dvDialog.showDialog(dialogTemplate, RemoveDialogController, {
                    title: 'CONFIRM_SAVE_FERIENINSEL',
                    deleteText: 'BESCHREIBUNG_SAVE_FERIENINSEL',
                    parentController: undefined,
                    elementID: undefined
                }).then(() => {
                    this.onSave();
                });
            } else {
                this.onSave();
            }
        }
        return undefined;
    }

    private setChosenFerientage(): void {
        this.betreuung.belegungFerieninsel.tage = [];
        for (let tag of this.ferieninselStammdaten.potenzielleFerieninselTageFuerBelegung) {
            if (tag.angemeldet) {
                this.betreuung.belegungFerieninsel.tage.push(tag);
            }
        }
    }

    public showButtonsInstitution(): boolean {
        return this.betreuung.betreuungsstatus === TSBetreuungsstatus.SCHULAMT_ANMELDUNG_AUSGELOEST && !this.gesuchModelManager.isGesuchReadonlyForRole();
    }

    /**
     * Muss ueberschrieben werden, damit die richtige betreuung zurueckgegeben wird
     */
    public getBetreuungModel(): TSBetreuung {
        return this.betreuung;
    }

    public getMomentWeekdays() {
        let weekdays = moment.weekdays();
        weekdays.splice(0, 1);
        weekdays.splice(5, 1);
        return weekdays;
    }

    public displayBreak(tag: TSBelegungFerieninselTag, index: number, dayArray: Array<TSBelegungFerieninselTag>): boolean {
        if (dayArray[index + 1]) {
            return tag.tag.week() !== dayArray[index + 1].tag.week();
        } else {
            return false;
        }
    }

    public displayWeekRow(tag: TSBelegungFerieninselTag, index: number, dayArray: Array<TSBelegungFerieninselTag>): boolean {
        if (dayArray[index + 1]) {
            if (tag.tag.weekday() === 1 && dayArray[index + 1].tag.weekday() === 5 && dayArray[index + 1].tag.diff(tag.tag, 'days') === 11) {
                return false;
            }
            return dayArray[index + 1].tag.diff(tag.tag, 'days') > 7;
        } else {
            return false;
        }
    }

    public displayMonthRow(tag: TSBelegungFerieninselTag, index: number, dayArray: Array<TSBelegungFerieninselTag>): boolean {
        if (dayArray[index + 1]) {
            return !tag.tag.isSame(dayArray[index + 1].tag, 'month');
        } else {
            return false;
        }
    }
}
