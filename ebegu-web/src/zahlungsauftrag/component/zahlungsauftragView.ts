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
import * as moment from 'moment';
import {ApplicationPropertyRS} from '../../admin/service/applicationPropertyRS.rest';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import {DvDialog} from '../../core/directive/dv-dialog/dv-dialog';
import {DownloadRS} from '../../core/service/downloadRS.rest';
import {ReportRS} from '../../core/service/reportRS.rest';
import ZahlungRS from '../../core/service/zahlungRS.rest';
import {RemoveDialogController} from '../../gesuch/dialog/RemoveDialogController';
import {TSRole} from '../../models/enums/TSRole';
import {TSZahlungsauftragsstatus} from '../../models/enums/TSZahlungsauftragstatus';
import {TSZahlungsstatus} from '../../models/enums/TSZahlungsstatus';
import TSDownloadFile from '../../models/TSDownloadFile';
import TSZahlungsauftrag from '../../models/TSZahlungsauftrag';
import EbeguUtil from '../../utils/EbeguUtil';
import {TSRoleUtil} from '../../utils/TSRoleUtil';
import IStateService = angular.ui.IStateService;
import IFormController = angular.IFormController;
import Moment = moment.Moment;
import ITranslateService = angular.translate.ITranslateService;
let template = require('./zahlungsauftragView.html');
require('./zahlungsauftragView.less');

let removeDialogTemplate = require('../../gesuch/dialog/removeDialogTemplate.html');

export class ZahlungsauftragViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = ZahlungsauftragViewController;
    controllerAs = 'vm';
}

export class ZahlungsauftragViewController {

    form: IFormController;
    private zahlungsauftragen: Array<TSZahlungsauftrag>;
    private zahlungsauftragToEdit: TSZahlungsauftrag;

    beschrieb: string;
    faelligkeitsdatum: Moment;
    datumGeneriert: Moment;
    itemsByPage: number = 12;
    testMode: boolean = false;
    minDateForTestlauf: Moment;

    static $inject: string[] = ['ZahlungRS', 'CONSTANTS', '$state', 'DownloadRS', 'ApplicationPropertyRS', 'ReportRS',
        'AuthServiceRS', 'EbeguUtil', 'DvDialog', '$translate'];

    constructor(private zahlungRS: ZahlungRS, private CONSTANTS: any,
                private $state: IStateService, private downloadRS: DownloadRS,
                private applicationPropertyRS: ApplicationPropertyRS,
                private reportRS: ReportRS, private authServiceRS: AuthServiceRS, private ebeguUtil: EbeguUtil,
                private dvDialog: DvDialog, private $translate: ITranslateService) {
        this.initViewModel();
    }

    public getZahlungsauftragen() {
        return this.zahlungsauftragen;
    }

    private initViewModel() {
        this.minDateForTestlauf = moment(moment.now()).subtract(1, 'days'); // Testlauf darf auch nur in die Zukunft gemacht werden!
        this.updateZahlungsauftrag();
        this.applicationPropertyRS.isZahlungenTestMode().then((response: any) => {
            this.testMode = response;
        });
    }

    private updateZahlungsauftrag() {

        switch (this.authServiceRS.getPrincipal().role) {

            case TSRole.SACHBEARBEITER_INSTITUTION:
            case TSRole.SACHBEARBEITER_TRAEGERSCHAFT: {
                this.zahlungRS.getAllZahlungsauftraegeInstitution().then((response: any) => {
                    this.zahlungsauftragen = angular.copy(response);

                });
                break;
            }
            case TSRole.SUPER_ADMIN:
            case TSRole.ADMIN:
            case TSRole.SACHBEARBEITER_JA:
            case TSRole.JURIST:
            case TSRole.REVISOR: {
                this.zahlungRS.getAllZahlungsauftraege().then((response: any) => {
                    this.zahlungsauftragen = angular.copy(response);

                });
                break;
            }
            default:
                break;
        }
    }

    public gotoZahlung(zahlungsauftrag: TSZahlungsauftrag) {
        this.$state.go('zahlung', {
            zahlungsauftragId: zahlungsauftrag.id
        });
    }

    public createZahlungsauftrag() {
        if (this.form.$valid) {
            this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
                title: this.$translate.instant('ZAHLUNG_ERSTELLEN_CONFIRM'),
                deleteText: this.$translate.instant('ZAHLUNG_ERSTELLEN_INFO'),
                parentController: undefined,
                elementID: undefined
            }).then(() => {   //User confirmed removal
                this.zahlungRS.createZahlungsauftrag(this.beschrieb, this.faelligkeitsdatum, this.datumGeneriert).then((response: TSZahlungsauftrag) => {
                    this.zahlungsauftragen.push(response);
                    this.resetEditZahlungsauftrag();
                    this.resetForm();
                });
            });
        }
    }

    public downloadPain(zahlungsauftrag: TSZahlungsauftrag) {
        let win: Window = this.downloadRS.prepareDownloadWindow();
        return this.downloadRS.getPain001AccessTokenGeneratedDokument(zahlungsauftrag.id)
            .then((downloadFile: TSDownloadFile) => {
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, true, win);
            })
            .catch((ex) => {
                win.close();
            });
    }

    public downloadAllDetails(zahlungsauftrag: TSZahlungsauftrag) {
        let win: Window = this.downloadRS.prepareDownloadWindow();
        this.reportRS.getZahlungsauftragReportExcel(zahlungsauftrag.id)
            .then((downloadFile: TSDownloadFile) => {
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
            })
            .catch((ex) => {
                win.close();
            });
    }

    public ausloesen(zahlungsauftragId: string) {
        this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
            title: this.$translate.instant('ZAHLUNG_AUSLOESEN_CONFIRM'),
            deleteText: this.$translate.instant('ZAHLUNG_AUSLOESEN_INFO'),
            parentController: undefined,
            elementID: undefined
        }).then(() => {   //User confirmed removal
            this.zahlungRS.zahlungsauftragAusloesen(zahlungsauftragId).then((response: TSZahlungsauftrag) => {
                let index = EbeguUtil.getIndexOfElementwithID(response, this.zahlungsauftragen);
                if (index > -1) {
                    this.zahlungsauftragen[index] = response;
                }
                this.ebeguUtil.handleSmarttablesUpdateBug(this.zahlungsauftragen);
            });
        });
    }

    public edit(zahlungsauftrag: TSZahlungsauftrag) {
        this.zahlungsauftragToEdit = zahlungsauftrag;
    }

    public save(zahlungsauftrag: TSZahlungsauftrag) {
        if (this.isEditValid()) {
            this.zahlungRS.updateZahlungsauftrag(
                this.zahlungsauftragToEdit.beschrieb, this.zahlungsauftragToEdit.datumFaellig, this.zahlungsauftragToEdit.id).then((response: TSZahlungsauftrag) => {
                let index = EbeguUtil.getIndexOfElementwithID(response, this.zahlungsauftragen);
                if (index > -1) {
                    this.zahlungsauftragen[index] = response;
                }
                this.form.$setPristine(); // nach dem es gespeichert wird, muessen wir das Form wieder auf clean setzen
                this.resetEditZahlungsauftrag();
            });

        }
    }

    public isEditable(status: TSZahlungsauftragsstatus): boolean {
        if (status === TSZahlungsauftragsstatus.ENTWURF) {
            return true;
        }
        return false;
    }

    public isEditMode(zahlungsauftragId: string): boolean {
        if (this.zahlungsauftragToEdit && this.zahlungsauftragToEdit.id === zahlungsauftragId) {
            return true;
        }
        return false;
    }

    public isEditValid(): boolean {
        if (this.zahlungsauftragToEdit) {
            return this.zahlungsauftragToEdit.beschrieb && this.zahlungsauftragToEdit.beschrieb.length > 0 &&
                this.zahlungsauftragToEdit.datumFaellig !== null && this.zahlungsauftragToEdit.datumFaellig !== undefined;
        }
        return false;
    }

    private resetEditZahlungsauftrag() {
        this.zahlungsauftragToEdit = null;
    }

    public rowClass(zahlungsauftragId: string) {
        if (this.isEditMode(zahlungsauftragId) && !this.isEditValid()) {
            return 'errorrow';
        }
        return '';
    }

    /**
     * resets all three variables needed to create a Zahlung.
     */
    private resetForm(): void {
        this.beschrieb = undefined;
        this.faelligkeitsdatum = undefined;
        this.datumGeneriert = undefined;
        this.form.$setPristine();
        this.form.$setUntouched();
    }

    public getCalculatedStatus(zahlungsauftrag: TSZahlungsauftrag) {
        if (zahlungsauftrag.status !== TSZahlungsauftragsstatus.BESTAETIGT && this.authServiceRS.isOneOfRoles(TSRoleUtil.getTraegerschaftInstitutionOnlyRoles())) {
            if (zahlungsauftrag.zahlungen.every(zahlung => zahlung.status === TSZahlungsstatus.BESTAETIGT)) {
                return TSZahlungsstatus.BESTAETIGT;
            }
        }
        return zahlungsauftrag.status;
    }
}
