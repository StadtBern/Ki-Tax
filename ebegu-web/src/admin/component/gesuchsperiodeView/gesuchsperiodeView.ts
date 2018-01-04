/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

import {IComponentOptions, IFormController, ILogService} from 'angular';
import AbstractAdminViewController from '../../abstractAdminView';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import {RemoveDialogController} from '../../../gesuch/dialog/RemoveDialogController';
import moment = require('moment');
import TSEbeguParameter from '../../../models/TSEbeguParameter';
import {TSCacheTyp} from '../../../models/enums/TSCacheTyp';
import EbeguUtil from '../../../utils/EbeguUtil';
import {getTSGesuchsperiodeStatusValues, TSGesuchsperiodeStatus} from '../../../models/enums/TSGesuchsperiodeStatus';
import {EbeguParameterRS} from '../../service/ebeguParameterRS.rest';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import GlobalCacheService from '../../../gesuch/service/globalCacheService';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import {TSDateRange} from '../../../models/types/TSDateRange';
import {IGesuchsperiodeStateParams} from '../../admin.route';
import IStateService = angular.ui.IStateService;

let template = require('./gesuchsperiodeView.html');
let removeDialogTemplate = require('../../../gesuch/dialog/removeDialogTemplate.html');


export class GesuchsperiodeViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    template: string = template;
    controller: any = GesuchsperiodeViewController;
    controllerAs: string = 'vm';
}

export class GesuchsperiodeViewController extends AbstractAdminViewController {

    form: IFormController;
    gesuchsperiode: TSGesuchsperiode;
    ebeguParameterListGesuchsperiode: TSEbeguParameter[];

    initialStatus: TSGesuchsperiodeStatus;
    datumFreischaltungTagesschule: moment.Moment;


    static $inject = ['EbeguParameterRS', 'DvDialog', 'GlobalCacheService', 'GesuchsperiodeRS', '$log', '$stateParams',
        '$state', 'AuthServiceRS'];

    constructor(private ebeguParameterRS: EbeguParameterRS, private dvDialog: DvDialog, private globalCacheService: GlobalCacheService,
                private gesuchsperiodeRS: GesuchsperiodeRS, private $log: ILogService, private $stateParams: IGesuchsperiodeStateParams,
                private $state: IStateService, authServiceRS: AuthServiceRS) {
        super(authServiceRS);
    }

    $onInit() {
        if (this.$stateParams.gesuchsperiodeId) {
            this.gesuchsperiodeRS.findGesuchsperiode(this.$stateParams.gesuchsperiodeId).then((found: TSGesuchsperiode) => {
                this.setSelectedGesuchsperiode(found);
                this.initialStatus = this.gesuchsperiode.status;
            });
        } else {
            this.createGesuchsperiode();
        }
    }

    public getTSGesuchsperiodeStatusValues(): Array<TSGesuchsperiodeStatus> {
        return getTSGesuchsperiodeStatusValues();
    }

    private setSelectedGesuchsperiode(gesuchsperiode: any): void {
        this.gesuchsperiode = gesuchsperiode;
        this.readEbeguParameterByGesuchsperiode();
        this.datumFreischaltungTagesschule = undefined;
    }

    private readEbeguParameterByGesuchsperiode(): void {
        this.ebeguParameterRS.getEbeguParameterByGesuchsperiode(this.gesuchsperiode.id).then((response: TSEbeguParameter[]) => {
            this.ebeguParameterListGesuchsperiode = response;
        });
    }

    public cancelGesuchsperiode(): void {
        this.$state.go('parameter');
    }

    public saveGesuchsperiode(): void {
        if (this.form.$valid && this.statusHaveChanged()) {
            // Den Dialog nur aufrufen, wenn der Status geändert wurde (oder die GP neu ist)
            if (this.gesuchsperiode.isNew() || this.initialStatus !== this.gesuchsperiode.status) {
                let dialogText = this.getGesuchsperiodeSaveDialogText();
                this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
                    title: 'GESUCHSPERIODE_DIALOG_TITLE',
                    deleteText: dialogText,
                    parentController: undefined,
                    elementID: undefined
                }).then(() => {
                    this.saveGesuchsperiodeFreischaltungTagesschule();
                });
            } else {
                this.saveGesuchsperiodeFreischaltungTagesschule();
            }
        }
    }

    public saveGesuchsperiodeFreischaltungTagesschule(): void {
        // Zweite Rückfrage falls neu ein Datum für die Freischaltung der Tagesschulen gesetzt wurde
        if (!this.gesuchsperiode.isTagesschulenAnmeldungKonfiguriert() && this.datumFreischaltungTagesschule) {
            this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
                title: 'FREISCHALTUNG_TAGESSCHULE_DIALOG_TITLE',
                deleteText: 'FREISCHALTUNG_TAGESSCHULE_DIALOG_TEXT',
                parentController: undefined,
                elementID: undefined
            }).then(() => {
                this.gesuchsperiode.datumFreischaltungTagesschule = this.datumFreischaltungTagesschule;
                this.doSave();
            });
        } else {
            this.doSave();
        }
    }

    private doSave(): void {
        this.gesuchsperiodeRS.updateGesuchsperiode(this.gesuchsperiode).then((response: TSGesuchsperiode) => {
            this.gesuchsperiode = response;
            this.datumFreischaltungTagesschule = undefined;
            this.globalCacheService.getCache(TSCacheTyp.EBEGU_PARAMETER).removeAll();
            // Die E-BEGU-Parameter für die neue Periode lesen bzw. erstellen, wenn noch nicht vorhanden
            this.readEbeguParameterByGesuchsperiode();
            this.gesuchsperiodeRS.updateActiveGesuchsperiodenList(); //reset gesuchperioden in manager
            this.gesuchsperiodeRS.updateNichtAbgeschlosseneGesuchsperiodenList();
            this.initialStatus = this.gesuchsperiode.status;
        });
    }

    public createGesuchsperiode(): void {
        this.gesuchsperiodeRS.getNewestGesuchsperiode().then(newestGeuschsperiode => {
            this.gesuchsperiode = new TSGesuchsperiode(TSGesuchsperiodeStatus.ENTWURF, new TSDateRange());
            this.initialStatus = undefined; //initialStatus ist undefined for new created Gesuchsperioden
            this.datumFreischaltungTagesschule = undefined;
            this.gesuchsperiode.gueltigkeit.gueltigAb = newestGeuschsperiode.gueltigkeit.gueltigAb.clone().add(1, 'years');
            this.gesuchsperiode.gueltigkeit.gueltigBis = newestGeuschsperiode.gueltigkeit.gueltigBis.clone().add(1, 'years');
            this.gesuchsperiode.datumFreischaltungTagesschule = this.gesuchsperiode.gueltigkeit.gueltigAb;
        });
        this.gesuchsperiode = undefined;
    }

    public saveParameterByGesuchsperiode(): void {
        for (let i = 0; i < this.ebeguParameterListGesuchsperiode.length; i++) {
            let param = this.ebeguParameterListGesuchsperiode[i];
            this.ebeguParameterRS.saveEbeguParameter(param);
        }
        this.globalCacheService.getCache(TSCacheTyp.EBEGU_PARAMETER).removeAll();
        this.gesuchsperiodeRS.updateActiveGesuchsperiodenList();
        this.gesuchsperiodeRS.updateNichtAbgeschlosseneGesuchsperiodenList();
    }

    private getGesuchsperiodeSaveDialogText(): string {
        if (this.gesuchsperiode.status === TSGesuchsperiodeStatus.ENTWURF) {
            return 'GESUCHSPERIODE_DIALOG_TEXT_ENTWURF';
        }
        if (this.gesuchsperiode.status === TSGesuchsperiodeStatus.AKTIV) {
            return 'GESUCHSPERIODE_DIALOG_TEXT_AKTIV';
        }
        if (this.gesuchsperiode.status === TSGesuchsperiodeStatus.INAKTIV) {
            return 'GESUCHSPERIODE_DIALOG_TEXT_INAKTIV';
        }
        if (this.gesuchsperiode.status === TSGesuchsperiodeStatus.GESCHLOSSEN) {
            return 'GESUCHSPERIODE_DIALOG_TEXT_GESCHLOSSEN';
        }
        this.$log.warn('Achtung, Status unbekannt: ', this.gesuchsperiode.status);
        return null;
    }

    public ersterSchultagRequired(): boolean {
        return (EbeguUtil.isNotNullOrUndefined(this.gesuchsperiode.datumFreischaltungTagesschule)
            &&  this.gesuchsperiode.datumFreischaltungTagesschule.isBefore(this.gesuchsperiode.gueltigkeit.gueltigAb))
            || (EbeguUtil.isNotNullOrUndefined(this.datumFreischaltungTagesschule)
                && this.datumFreischaltungTagesschule.isBefore(this.gesuchsperiode.gueltigkeit.gueltigAb));
    }

    public periodenParamsEditable(): boolean {
        return this.periodenParamsEditableForPeriode(this.gesuchsperiode);
    }

    /**
     * Gibt true zurueck wenn der Status sich geaendert hat oder wenn der Status AKTIV ist, da in Status AKTIV, die Parameter (Tagesschule)
     * noch geaendert werden koennen.
     */
    private statusHaveChanged() {
        return this.initialStatus !== this.gesuchsperiode.status || this.gesuchsperiode.status === TSGesuchsperiodeStatus.AKTIV;
    }
}
