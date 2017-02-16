import {IComponentOptions} from 'angular';
import TSZahlung from '../../models/TSZahlung';
import ZahlungRS from '../../core/service/zahlungRS.rest';
import EbeguUtil from '../../utils/EbeguUtil';
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
    numberOfPages: number = 1;

    static $inject: string[] = ['ZahlungRS', 'EbeguUtil', 'CONSTANTS', '$state'];

    constructor(private zahlungRS: ZahlungRS, private ebeguUtil: EbeguUtil, private CONSTANTS: any, private $state: IStateService) {
        this.initViewModel();
    }

    public getZahlungen() {
        return this.zahlungen;
    }

    public addZerosToFallNummer(fallnummer: number): string {
        return this.ebeguUtil.addZerosToNumber(fallnummer, this.CONSTANTS.FALLNUMMER_LENGTH);
    }

    private initViewModel() {
        this.updateZahlung();
    }

    private updateZahlung() {
        this.zahlungRS.getAllZahlungsauftraege().then((response: any) => {
            this.zahlungen = angular.copy(response);
            this.numberOfPages = this.zahlungen.length / this.itemsByPage;
        });
    }

    private gotoZahlung(zahlung: TSZahlung) {
        /*        this.$state.go('zahlungen', {
         fallId: zahlung.fall.id
         });*/
    }

    private createZahlung() {
        this.zahlungRS.createZahlung().then((response: any) => {

        });
    }
}
