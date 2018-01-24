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
import TSInstitutionStammdaten from '../../../models/TSInstitutionStammdaten';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import {IAngebotStateParams} from '../../gesuchstellerDashboard.route';
import ILogService = angular.ILogService;
import {TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import TSKindContainer from '../../../models/TSKindContainer';
import TSBetreuung from '../../../models/TSBetreuung';
import TSBelegungTagesschule from '../../../models/TSBelegungTagesschule';
import * as moment from 'moment';
import DateUtil from '../../../utils/DateUtil';
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';
import TSAnmeldungDTO from '../../../models/TSAnmeldungDTO';
import BetreuungRS from '../../../core/service/betreuungRS.rest';
import IFormController = angular.IFormController;
import {OkDialogController} from '../../../gesuch/dialog/OkDialogController';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';

let template = require('./createAngebotView.html');
require('./createAngebotView.less');
let okDialogTempl = require('../../../gesuch/dialog/okDialogTemplate.html');

export class CreateAngebotListViewConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = CreateAngebotListViewController;
    controllerAs = 'vm';
}

export class CreateAngebotListViewController {

    form: IFormController;
    private ts: boolean;
    private fi: boolean;
    private kindContainer: TSKindContainer;
    private institution: TSInstitutionStammdaten;
    private anmeldungDTO: TSAnmeldungDTO = new TSAnmeldungDTO;

    static $inject: string[] = ['$state', '$log', 'GesuchModelManager', '$stateParams', 'BetreuungRS', 'DvDialog'];

    constructor(private $state: IStateService, private $log: ILogService,
                private gesuchModelManager: GesuchModelManager, private $stateParams: IAngebotStateParams,
                private betreuungRS: BetreuungRS, private dvDialog: DvDialog) {
    }

    $onInit() {
        this.anmeldungDTO = new TSAnmeldungDTO;
        if (this.$stateParams.type === 'TS') {
            this.ts = true;
        } else if (this.$stateParams.type === 'FI') {
            this.fi = true;
        } else {
            console.error('type must be set!');
            this.backToHome();
        }
    }

    public getInstitutionenSDList(): Array<TSInstitutionStammdaten> {
        let result: Array<TSInstitutionStammdaten> = [];
        /*if (this.betreuungsangebot) {*/
        this.gesuchModelManager.getActiveInstitutionenList().forEach((instStamm: TSInstitutionStammdaten) => {
            if (this.ts) {
                if (instStamm.betreuungsangebotTyp === TSBetreuungsangebotTyp.TAGESSCHULE) {
                    result.push(instStamm);
                }
            } else if (this.fi) {
                if (instStamm.betreuungsangebotTyp === TSBetreuungsangebotTyp.FERIENINSEL) {
                    result.push(instStamm);
                }
            }
        });
        return result;
    }

    public getKindContainerList(): Array<TSKindContainer> {

        return this.gesuchModelManager.getGesuch().kindContainers;

    }

    public showInstitutionSelect(): boolean {
        return !!this.kindContainer;
    }

    public displayModuleTagesschule(): boolean {
        return this.ts && !!this.institution;
    }

    public displayModuleFerieninsel(): boolean {
        return this.fi && !!this.institution;
    }

    public selectedInstitutionStammdatenChanged(): void {
        this.anmeldungDTO.betreuung = new TSBetreuung();
        this.anmeldungDTO.betreuung.institutionStammdaten = this.institution;
        this.anmeldungDTO.betreuung.belegungTagesschule = new TSBelegungTagesschule();
        // Default Eintrittsdatum ist erster Schultag, wenn noch in Zukunft

        if (this.ts) {
            // Nur fuer die neuen Gesuchsperiode kann die Belegung erfast werden
            if (this.gesuchModelManager.getGesuchsperiode().hasTagesschulenAnmeldung()
                && this.isTageschulenAnmeldungAktiv()) {
                this.anmeldungDTO.betreuung.betreuungsstatus = TSBetreuungsstatus.SCHULAMT_ANMELDUNG_ERFASST;
                if (!this.anmeldungDTO.betreuung.belegungTagesschule) {
                    this.anmeldungDTO.betreuung.belegungTagesschule = new TSBelegungTagesschule();
                    // Default Eintrittsdatum ist erster Schultag, wenn noch in Zukunft
                    let ersterSchultag: moment.Moment = this.gesuchModelManager.getGesuchsperiode().datumErsterSchultag;
                    if (DateUtil.today().isBefore(ersterSchultag)) {
                        this.anmeldungDTO.betreuung.belegungTagesschule.eintrittsdatum = ersterSchultag;
                    }
                }
            } else {
                // "Alte" Tagesschule: Noch keine Modulanmeldung moeglich. Wir setzen Default-Institution
                this.anmeldungDTO.betreuung.betreuungsstatus = TSBetreuungsstatus.SCHULAMT;

            }
            this.anmeldungDTO.betreuung.belegungFerieninsel = undefined;
        } else {
            this.anmeldungDTO.betreuung.belegungTagesschule = undefined;
        }

    }

    public isTageschulenAnmeldungAktiv() {
        return this.gesuchModelManager.getGesuchsperiode().isTageschulenAnmeldungAktiv();
    }

    public selectedKindChanged(): void {
        if (this.kindContainer) {
            this.anmeldungDTO.additionalKindQuestions = !this.kindContainer.kindJA.familienErgaenzendeBetreuung;
            this.anmeldungDTO.kindContainerId = this.kindContainer.id;
        }
    }

    public getDatumEinschulung(): moment.Moment {
        return this.gesuchModelManager.getGesuchsperiodeBegin();
    }

    public anmeldenSchulamt(): void {
        if (this.ts) {
            this.anmeldungDTO.betreuung.betreuungsstatus = TSBetreuungsstatus.SCHULAMT_ANMELDUNG_AUSGELOEST;

            this.anmeldungDTO.betreuung.belegungTagesschule.moduleTagesschule = this.anmeldungDTO.betreuung.belegungTagesschule.moduleTagesschule
                .filter(modul => modul.angemeldet === true);

            this.betreuungRS.createAngebot(this.anmeldungDTO).then((response: any) => {
                this.dvDialog.showDialog(okDialogTempl, OkDialogController, {
                    title: 'TAGESSCHULE_ANMELDUNG_GESPEICHERT'
                }).then(() => {
                    this.backToHome();
                });
            });
        } else if (this.fi) {
            this.anmeldungDTO.betreuung.betreuungsstatus = TSBetreuungsstatus.SCHULAMT_ANMELDUNG_AUSGELOEST;
            this.betreuungRS.createAngebot(this.anmeldungDTO).then((response: any) => {
                this.kindContainer.kindJA.familienErgaenzendeBetreuung = true;
                this.dvDialog.showDialog(okDialogTempl, OkDialogController, {
                    title: 'FERIENINSEL_ANMELDUNG_GESPEICHERT'
                }).then(() => {
                    this.backToHome();
                });
            });

        }
    }

    public backToHome() {
        this.form.$setPristine();
        this.$state.go('gesuchstellerDashboard');
    }

}
