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

    static $inject: string[] = ['ZahlungRS', 'EbeguUtil', 'CONSTANTS', '$stateParams', '$state'];

    constructor(private zahlungRS: ZahlungRS, private ebeguUtil: EbeguUtil, private CONSTANTS: any, private $stateParams: IZahlungsauftragStateParams, private $state: IStateService) {
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

    private downloadDetails(zahlung: TSZahlung) {
        console.log('downloadAllDetails not yet impl ' + zahlung.id);
    }

    private gotToUebersicht(): void {
        this.$state.go('zahlungsauftrag');
    }

}
