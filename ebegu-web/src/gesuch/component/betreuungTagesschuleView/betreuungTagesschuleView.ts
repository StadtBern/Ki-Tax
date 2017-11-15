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
import IFormController = angular.IFormController;
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;
import IScope = angular.IScope;
import ITimeoutService = angular.ITimeoutService;
import ITranslateService = angular.translate.ITranslateService;
import TSInstitutionStammdaten from '../../../models/TSInstitutionStammdaten';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import DateUtil from '../../../utils/DateUtil';
import {getTSModulTagesschuleNameValues, TSModulTagesschuleName} from '../../../models/enums/TSModulTagesschuleName';
import {getWeekdaysValues, TSDayOfWeek} from '../../../models/enums/TSDayOfWeek';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import TSModulTagesschule from '../../../models/TSModulTagesschule';

let template = require('./betreuungTagesschuleView.html');
require('./betreuungTagesschuleView.less');
let dialogTemplate = require('../../dialog/removeDialogTemplate.html');

export class BetreuungTagesschuleViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        betreuung: '=',
        onSave: '&',
        instStammId: '=',
        tagesschuleList: '&',
        cancel: '&',
        form: '='
    };
    template = template;
    controller = BetreuungTagesschuleViewController;
    controllerAs = 'vm';
}

export class BetreuungTagesschuleViewController extends BetreuungViewController {

    instStammId: string;
    onSave: () => void;
    form: IFormController;
    betreuung: TSBetreuung;
    tagesschuleList: () => Array<TSInstitutionStammdaten>;
    showErrorMessageNoModule: boolean;


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
        this.copyModuleToBelegung();
    }

    /**
     * Kopiert alle Module der ausgewaehlten Tagesschule in die Belegung, sodass man direkt in die Belegung die Module auswaehlen kann.
     */
    private copyModuleToBelegung() {
        if (this.getBetreuungModel().institutionStammdaten && this.getBetreuungModel().institutionStammdaten.institutionStammdatenTagesschule
            && this.getBetreuungModel().institutionStammdaten.institutionStammdatenTagesschule.moduleTagesschule) {

            let angemeldeteModule: TSModulTagesschule[] = angular.copy(this.getBetreuungModel().belegungTagesschule.moduleTagesschule);
            this.getBetreuungModel().belegungTagesschule.moduleTagesschule = angular.copy(this.getBetreuungModel().institutionStammdaten.institutionStammdatenTagesschule.moduleTagesschule);
            if (angemeldeteModule) {
                angemeldeteModule.forEach(angemeldetesModul => {
                    this.getBetreuungModel().belegungTagesschule.moduleTagesschule.forEach(instModul => {
                        if (angemeldetesModul.isSameModul(instModul)) {
                            instModul.angemeldet = true;
                        }
                    });
                });
            }
        }
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

    public getModulTagesschuleNameList(): TSModulTagesschuleName[] {
        return getTSModulTagesschuleNameValues();
    }

    public getWeekDays(): TSDayOfWeek[] {
        return getWeekdaysValues();
    }

    public isTagesschuleAlreadySelected(): boolean {
        return this.instStammId !== null && this.instStammId !== undefined;
    }

    public isModulEnabled(modulName: TSModulTagesschuleName, weekday: TSDayOfWeek): boolean {
        return this.isEnabled() && this.isModulDefinedInSelectedTS(modulName, weekday);
    }

    /**
     * Gibt true zurueck wenn das gegebene Modul fuer die ausgewaehlte TS definiert wurde und zwar mit zeitBis und zeitVon.
     */
    public isModulDefinedInSelectedTS(modulName: TSModulTagesschuleName, weekday: TSDayOfWeek): boolean {
        let modulTS: TSModulTagesschule = this.getModul(modulName, weekday);
        return !!(modulTS && modulTS.zeitBis && modulTS.zeitVon);
    }

    public getModul(modulName: TSModulTagesschuleName, weekday: TSDayOfWeek): TSModulTagesschule {
        if (this.getBetreuungModel().belegungTagesschule && this.getBetreuungModel().belegungTagesschule.moduleTagesschule) {
            for (let modulTS of this.getBetreuungModel().belegungTagesschule.moduleTagesschule) {
                if (modulTS.modulTagesschuleName === modulName && modulTS.wochentag === weekday) {
                    return modulTS;
                }
            }
        }
        return null;
    }

    public anmelden(): IPromise<any> {
        if (this.form.$valid) {
            // Validieren, dass mindestens 1 Modul ausgewählt war
            if (!this.isThereAnyAnmeldung()) {
                this.showErrorMessageNoModule = true;
                return undefined;
            }
            this.filterOnlyAngemeldeteModule();
            return this.dvDialog.showDialog(dialogTemplate, RemoveDialogController, {
                title: 'CONFIRM_SAVE_TAGESSCHULE',
                deleteText: 'BESCHREIBUNG_SAVE_TAGESSCHULE',
                parentController: undefined,
                elementID: undefined
            }).then(() => {
                this.onSave();
            });
        }
        return undefined;
    }

    public setSelectedInstitutionStammdaten(): void {
        super.setSelectedInstitutionStammdaten();
        this.filterOnlyAngemeldeteModule();
        this.copyModuleToBelegung();
    }

    /**
     * Entfernt alle Module die nicht als angemeldet markiert sind
     */
    private filterOnlyAngemeldeteModule() {
        // noinspection UnnecessaryLocalVariableJS
        let angemeldeteModule: TSModulTagesschule[] = this.getBetreuungModel().belegungTagesschule.moduleTagesschule
            .filter(modul => modul.angemeldet === true);
        this.getBetreuungModel().belegungTagesschule.moduleTagesschule = angemeldeteModule;
    }

    private isThereAnyAnmeldung(): boolean {
        return this.getBetreuungModel().belegungTagesschule.moduleTagesschule
            .filter(modul => modul.angemeldet === true).length > 0;
    }

    public getModulName(modulName: TSModulTagesschuleName): string {
        let modul: TSModulTagesschule = this.getModul(modulName, TSDayOfWeek.MONDAY); // monday ist der Vertreter fuer die ganze Woche
        return this.$translate.instant(TSModulTagesschuleName[modulName]) + this.getModulTimeAsString(modul);
    }

    public getModulTimeAsString(modul: TSModulTagesschule): string {
        if (modul) {
            return ' (' + modul.zeitVon.format('HH:mm') + ' - ' + modul.zeitBis.format('HH:mm') + ')';
        }
        return '';
    }
}
