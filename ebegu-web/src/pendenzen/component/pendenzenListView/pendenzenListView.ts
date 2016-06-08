import {IComponentOptions, IFilterService} from 'angular';
import TSPendenzJA from '../../../models/TSPendenzJA';
import PendenzRS from '../../service/PendenzRS.rest';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import EbeguUtil from '../../../utils/EbeguUtil';
import {TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
let template = require('./pendenzenListView.html');
require('./pendenzenListView.less');

export class PendenzenListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = PendenzenListViewController;
    controllerAs = 'vm';
}

export class PendenzenListViewController {

    private pendenzenList: Array<TSPendenzJA>;

    static $inject: string[] = ['PendenzRS', 'EbeguUtil', '$filter'];
    constructor(public pendenzRS: PendenzRS, private ebeguUtil: EbeguUtil, private $filter: IFilterService) {
        this.initViewModel();
    }

    private initViewModel() {
        this.pendenzRS.getPendenzenList().then((response: any) => {
            this.pendenzenList = angular.copy(response);
        });
    }

    public getPendenzenList(): Array<TSPendenzJA> {
        return this.pendenzenList;
    }

    public getGesuchsperiodeAsString(gesuchsperiode: TSGesuchsperiode): string {
        return this.ebeguUtil.getGesuchsperiodeAsString(gesuchsperiode);
    }

    /**
     * Fallnummer muss 6-stellig dargestellt werden. Deshalb muessen so viele 0s am Anfang hinzugefuegt werden
     * bis die Fallnummer ein 6-stelliges String ist
     * @param fallNummer
     */
    public addZerosToFallnummer(fallnummer: number): string {
        if (fallnummer != null) {
            let fallnummerString = '' + fallnummer;
            while (fallnummerString.length < 6) {
                fallnummerString = '0' + fallnummerString;
            }
            return fallnummerString;
        }
        return undefined;
    }

    public translateBetreuungsangebotTypList(betreuungsangebotTypList: Array<TSBetreuungsangebotTyp>): string {
        let result: string = '';
        if (betreuungsangebotTypList) {
            let prefix: string = '';
            if (betreuungsangebotTypList && Array.isArray(betreuungsangebotTypList)) {
                for (let i = 0; i < betreuungsangebotTypList.length; i++) {
                    let tsBetreuungsangebotTyp = TSBetreuungsangebotTyp[betreuungsangebotTypList[i]];
                    result = result + prefix + this.$filter('translate')(tsBetreuungsangebotTyp).toString();
                    prefix = ', ';
                }
            }
        }
        return result;
    }
}
