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
import TSZahlung from '../../models/TSZahlung';
import ZahlungRS from '../../core/service/zahlungRS.rest';
import {IZahlungsauftragStateParams} from '../zahlung.route';
import {DownloadRS} from '../../core/service/downloadRS.rest';
import {ReportRS} from '../../core/service/reportRS.rest';
import TSDownloadFile from '../../models/TSDownloadFile';
import {TSRole} from '../../models/enums/TSRole';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import EbeguUtil from '../../utils/EbeguUtil';
import {TSZahlungsstatus} from '../../models/enums/TSZahlungsstatus';
import ITimeoutService = angular.ITimeoutService;
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;
import IQService = angular.IQService;
import IStateService = angular.ui.IStateService;
let template = require('./zahlungView.html');
require('./zahlungView.less');

export class ZahlungViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = ZahlungViewController;
    controllerAs = 'vm';
}

export class ZahlungViewController {

    private zahlungen: Array<TSZahlung>;

    itemsByPage: number = 20;

    static $inject: string[] = ['ZahlungRS', 'CONSTANTS', '$stateParams', '$state', 'DownloadRS', 'ReportRS',
        'AuthServiceRS', 'EbeguUtil'];

    constructor(private zahlungRS: ZahlungRS, private CONSTANTS: any,
                private $stateParams: IZahlungsauftragStateParams, private $state: IStateService,
                private downloadRS: DownloadRS, private reportRS: ReportRS, private authServiceRS: AuthServiceRS,
                private ebeguUtil: EbeguUtil) {
        this.initViewModel();
    }

    private initViewModel() {
        if (this.$stateParams.zahlungsauftragId) {
            switch (this.authServiceRS.getPrincipal().role) {
                case TSRole.SACHBEARBEITER_INSTITUTION:
                case TSRole.SACHBEARBEITER_TRAEGERSCHAFT: {
                    this.zahlungRS.getZahlungsauftragInstitution(this.$stateParams.zahlungsauftragId).then((response) => {
                        this.zahlungen = response.zahlungen;
                    });
                    break;
                }
                case TSRole.SUPER_ADMIN:
                case TSRole.ADMIN:
                case TSRole.SACHBEARBEITER_JA:
                case TSRole.JURIST:
                case TSRole.REVISOR: {
                    this.zahlungRS.getZahlungsauftrag(this.$stateParams.zahlungsauftragId).then((response) => {
                        this.zahlungen = response.zahlungen;
                    });
                    break;
                }
                default:
                    break;
            }
        }
    }

    private gotToUebersicht(): void {
        this.$state.go('zahlungsauftrag');
    }

    public downloadDetails(zahlung: TSZahlung) {
        let win: Window = this.downloadRS.prepareDownloadWindow();
        this.reportRS.getZahlungReportExcel(zahlung.id)
            .then((downloadFile: TSDownloadFile) => {
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
            })
            .catch((ex) => {
                win.close();
            });
    }

    public bestaetigen(zahlung: TSZahlung) {
        console.log('bestaetigen');
        this.zahlungRS.zahlungBestaetigen(zahlung.id).then((response: TSZahlung) => {
            let index = EbeguUtil.getIndexOfElementwithID(response, this.zahlungen);
            if (index > -1) {
                this.zahlungen[index] = response;
            }
            this.ebeguUtil.handleSmarttablesUpdateBug(this.zahlungen);
        });
    }

    public isBestaetigt(zahlungstatus: TSZahlungsstatus): boolean {
        return zahlungstatus === TSZahlungsstatus.BESTAETIGT;
    }
}
