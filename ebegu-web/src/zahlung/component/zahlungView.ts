import {IComponentOptions} from 'angular';
import TSZahlung from '../../models/TSZahlung';
import ZahlungRS from '../../core/service/zahlungRS.rest';
import EbeguUtil from '../../utils/EbeguUtil';
import {IZahlungsauftragStateParams} from '../zahlung.route';
import TSZahlungsauftrag from '../../models/TSZahlungsauftrag';
import ITimeoutService = angular.ITimeoutService;
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;
import IQService = angular.IQService;
import IStateService = angular.ui.IStateService;
import {DownloadRS} from '../../core/service/downloadRS.rest';
import {ReportRS} from '../../core/service/reportRS.rest';
import TSDownloadFile from '../../models/TSDownloadFile';
let template = require('./zahlungView.html');
require('./zahlungView.less');

export class ZahlungViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = ZahlungViewController;
    controllerAs = 'vm';
}

export class ZahlungViewController {

    private zahlungsauftrag: TSZahlungsauftrag;

    itemsByPage: number = 20;

    static $inject: string[] = ['ZahlungRS', 'EbeguUtil', 'CONSTANTS', '$stateParams', '$state', 'DownloadRS', 'ReportRS'];

    constructor(private zahlungRS: ZahlungRS, private ebeguUtil: EbeguUtil, private CONSTANTS: any,
                private $stateParams: IZahlungsauftragStateParams, private $state: IStateService,
                private downloadRS: DownloadRS, private reportRS: ReportRS) {
        this.initViewModel();
    }

    private initViewModel() {
        if (this.$stateParams.zahlungsauftrag) {
            this.zahlungsauftrag = this.$stateParams.zahlungsauftrag;
        } else if (this.$stateParams.zahlungsauftragId) {
            this.zahlungRS.getZahlungsauftrag(this.$stateParams.zahlungsauftragId).then((response) => {
                this.zahlungsauftrag = response;
            });
        }
    }

    public getZahlungsauftrag(): TSZahlungsauftrag {
        return this.zahlungsauftrag;
    }

    private gotToUebersicht(): void {
        this.$state.go('zahlungsauftrag');
    }

    public downloadDetails(zahlung: TSZahlung) {
        let win: Window = this.downloadRS.prepareDownloadWindow();
        this.reportRS.getZahlungReportExcel(zahlung.id)
            .then((downloadFile: TSDownloadFile) => {

                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
            });
    }

}
