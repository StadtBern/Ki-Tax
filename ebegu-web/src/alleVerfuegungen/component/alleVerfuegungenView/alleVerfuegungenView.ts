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

import IComponentOptions = angular.IComponentOptions;
import TSFall from '../../../models/TSFall';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import TSAntragStatusHistory from '../../../models/TSAntragStatusHistory';
import {IAlleVerfuegungenStateParams} from '../../alleVerfuegungen.route';
import FallRS from '../../../gesuch/service/fallRS.rest';
import TSBetreuung from '../../../models/TSBetreuung';
import BetreuungRS from '../../../core/service/betreuungRS.rest';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';
import TSDownloadFile from '../../../models/TSDownloadFile';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import IStateService = angular.ui.IStateService;
import ITimeoutService = angular.ITimeoutService;
import ILogService = angular.ILogService;
import EbeguUtil from '../../../utils/EbeguUtil';

let template = require('./alleVerfuegungenView.html');
require('./alleVerfuegungenView.less');

export class AlleVerfuegungenViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = AlleVerfuegungenViewController;
    controllerAs = 'vm';
}

export class AlleVerfuegungenViewController {

    fall: TSFall;
    alleVerfuegungen: Array<any> = [];
    itemsByPage: number = 20;
    TSRoleUtil = TSRoleUtil;

    static $inject: string[] = ['$state', '$stateParams', 'AuthServiceRS', 'FallRS', 'EbeguUtil', 'BetreuungRS',
        'GesuchModelManager', 'DownloadRS', '$log', '$timeout'];

    /* @ngInject */
    constructor(private $state: IStateService, private $stateParams: IAlleVerfuegungenStateParams,
                private authServiceRS: AuthServiceRS, private fallRS: FallRS, private ebeguUtil: EbeguUtil,
                private betreuungRS: BetreuungRS, private gesuchModelManager: GesuchModelManager,
                private downloadRS: DownloadRS, private $log: ILogService, private $timeout: ITimeoutService) {
    }

    $onInit() {
        if (this.$stateParams.fallId) {
            this.fallRS.findFall(this.$stateParams.fallId).then((response) => {
                this.fall = response;
                if (this.fall === undefined) {
                    this.cancel();
                }

                this.betreuungRS.findAllBetreuungenWithVerfuegungFromFall(this.fall.id).then((response) => {
                    response.forEach((item) => {
                        this.alleVerfuegungen.push(item);
                    });
                    this.alleVerfuegungen = response;
                });
            });
        } else {
            this.cancel();
        }
    }

    public getAlleVerfuegungen(): Array<TSAntragStatusHistory> {
        return this.alleVerfuegungen;
    }

    public openVerfuegung(betreuungNummer: string, kindNummer: number, gesuchId: string): void {
        if (betreuungNummer && kindNummer && gesuchId) {
            this.$state.go('gesuch.verfuegenView', {
                betreuungNumber: betreuungNummer,
                kindNumber: kindNummer,
                gesuchId: gesuchId
            });
        }
    }

    public cancel(): void {
        if (this.authServiceRS.isOneOfRoles(this.TSRoleUtil.getGesuchstellerOnlyRoles())) {
            this.$state.go('gesuchstellerDashboard');
        } else {
            this.$state.go('pendenzen');
        }
    }

    public showVerfuegungPdfLink(betreuung: TSBetreuung): boolean {
        return !(TSBetreuungsstatus.NICHT_EINGETRETEN === betreuung.betreuungsstatus);
    }

    public openVerfuegungPDF(betreuung: TSBetreuung): void {
        let win: Window = this.downloadRS.prepareDownloadWindow();
        this.downloadRS.getAccessTokenVerfuegungGeneratedDokument(betreuung.gesuchId,
            betreuung.id, false, '')
            .then((downloadFile: TSDownloadFile) => {
                this.$log.debug('accessToken: ' + downloadFile.accessToken);
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
            })
            .catch((ex) => {
                win.close();
                this.$log.error('An error occurred downloading the document, closing download window.');
            });
    }

    public openNichteintretenPDF(betreuung: TSBetreuung): void {
        let win: Window = this.downloadRS.prepareDownloadWindow();
        this.downloadRS.getAccessTokenNichteintretenGeneratedDokument(betreuung.id, false)
            .then((downloadFile: TSDownloadFile) => {
                this.$log.debug('accessToken: ' + downloadFile.accessToken);
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
            })
            .catch((ex) => {
                win.close();
                this.$log.error('An error occurred downloading the document, closing download window.');
            });
    }

    $postLink() {
        this.$timeout(() => {
            EbeguUtil.selectFirst();
        }, 500); // this is the only way because it needs a little until everything is loaded
    }
}
