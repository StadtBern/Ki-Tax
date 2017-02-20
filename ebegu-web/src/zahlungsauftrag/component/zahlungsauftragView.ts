import {IComponentOptions} from 'angular';
import TSZahlungsauftrag from '../../models/TSZahlungsauftrag';
import EbeguUtil from '../../utils/EbeguUtil';
import ZahlungRS from '../../core/service/zahlungRS.rest';
import ITimeoutService = angular.ITimeoutService;
import IPromise = angular.IPromise;
import ILogService = angular.ILogService;
import IQService = angular.IQService;
import IStateService = angular.ui.IStateService;
let template = require('./zahlungsauftragView.html');
require('./zahlungsauftragView.less');

export class ZahlungsauftragViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = ZahlungsauftragViewController;
    controllerAs = 'vm';
}

export class ZahlungsauftragViewController {

    private zahlungsauftragen: Array<TSZahlungsauftrag>;

    itemsByPage: number = 20;
    numberOfPages: number = 1;

    static $inject: string[] = ['ZahlungRS', 'EbeguUtil', 'CONSTANTS', '$state'];

    constructor(private zahlungRS: ZahlungRS, private ebeguUtil: EbeguUtil, private CONSTANTS: any, private $state: IStateService) {
        this.initViewModel();
    }

    public getZahlungsauftragen() {
        return this.zahlungsauftragen;
    }

    public addZerosToFallNummer(fallnummer: number): string {
        return this.ebeguUtil.addZerosToNumber(fallnummer, this.CONSTANTS.FALLNUMMER_LENGTH);
    }

    private initViewModel() {
        this.updateZahlungsauftrag();
    }

    private updateZahlungsauftrag() {
        this.zahlungRS.getAllZahlungsauftraege().then((response: any) => {
            this.zahlungsauftragen = angular.copy(response);
            this.numberOfPages = this.zahlungsauftragen.length / this.itemsByPage;
        });
    }

    public gotoZahlung(zahlungsauftrag: TSZahlungsauftrag) {
        //stateparams übergeben
        this.$state.go('zahlung', {
            zahlungsauftragId: zahlungsauftrag.id
        });
    }

    public createZahlung() {
        this.zahlungRS.createZahlung().then((response: any) => {

        });
    }
}
