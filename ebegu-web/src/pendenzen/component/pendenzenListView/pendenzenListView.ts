import {IComponentOptions, IFilterService} from 'angular';
import TSPendenzJA from '../../../models/TSPendenzJA';
import PendenzRS from '../../service/PendenzRS.rest';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import EbeguUtil from '../../../utils/EbeguUtil';
import {TSBetreuungsangebotTyp, getTSBetreuungsangebotTypValues} from '../../../models/enums/TSBetreuungsangebotTyp';
import {TSAntragTyp, getTSAntragTypValues} from '../../../models/enums/TSAntragTyp';
import TSInstitution from '../../../models/TSInstitution';
import {InstitutionRS} from '../../../core/service/institutionRS.rest';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import TSGesuch from '../../../models/TSGesuch';
import GesuchRS from '../../../gesuch/service/gesuchRS.rest';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import BerechnungsManager from '../../../gesuch/service/berechnungsManager';
import ITimeoutService = angular.ITimeoutService;
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
    selectedBetreuungsangebotTyp: string;
    selectedAntragTyp: string;
    selectedInstitution: string;
    selectedGesuchsperiode: string;
    institutionenList: Array<TSInstitution>;
    activeGesuchsperiodenList: Array<string>;
    itemsByPage: number = 20;
    numberOfPages: number = 1;


    static $inject: string[] = ['PendenzRS', 'EbeguUtil', '$filter', 'InstitutionRS', 'GesuchsperiodeRS',
        'GesuchRS', 'GesuchModelManager', 'BerechnungsManager', '$state', 'CONSTANTS', 'UserRS', 'AuthServiceRS'];

    constructor(public pendenzRS: PendenzRS, private ebeguUtil: EbeguUtil, private $filter: IFilterService,
                private institutionRS: InstitutionRS, private gesuchsperiodeRS: GesuchsperiodeRS,
                private gesuchRS: GesuchRS, private gesuchModelManager: GesuchModelManager, private berechnungsManager: BerechnungsManager,
                private $state: IStateService, private CONSTANTS: any) {
        this.initViewModel();
    }

    private initViewModel() {
        this.updatePendenzenList();
        this.updateInstitutionenList();
        this.updateActiveGesuchsperiodenList();
    }


    private updatePendenzenList() {
        this.pendenzRS.getPendenzenList().then((response: any) => {
            this.pendenzenList = angular.copy(response);
            this.numberOfPages = this.pendenzenList.length / this.itemsByPage;
        });
    }

    public getAntragTypen(): Array<TSAntragTyp> {
        return getTSAntragTypValues();
    }

    public getBetreuungsangebotTypen(): Array<TSBetreuungsangebotTyp> {
        return getTSBetreuungsangebotTypValues();
    }

    public updateActiveGesuchsperiodenList(): void {
        this.gesuchsperiodeRS.getAllActiveGesuchsperioden().then((response: any) => {
            this.activeGesuchsperiodenList = [];
            response.forEach((gesuchsperiode: TSGesuchsperiode) => {
                this.activeGesuchsperiodenList.push(this.getGesuchsperiodeAsString(gesuchsperiode));
            });
        });
    }

    public updateInstitutionenList(): void {
        this.institutionRS.getAllInstitutionen().then((response: any) => {
            this.institutionenList = angular.copy(response);
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
     * @param fallnummer
     */
    public addZerosToFallnummer(fallnummer: number): string {
        return this.ebeguUtil.addZerosToNumber(fallnummer, this.CONSTANTS.FALLNUMMER_LENGTH);
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

    public editPendenzJA(pendenz: TSPendenzJA): void {
        // todo team hier müssen wir auch MUTATIONEN bekommen koennen
        if (pendenz && pendenz.antragTyp === TSAntragTyp.GESUCH) {
            this.gesuchRS.findGesuch(pendenz.antragId).then((response) => {
                if (response) {
                    this.openGesuch(response);
                }
            });
        }
    }

    private openGesuch(gesuch: TSGesuch): void {
        if (gesuch) {
            this.berechnungsManager.clear();
            this.gesuchModelManager.setGesuch(gesuch);
            this.$state.go('gesuch.fallcreation');
        }
    }
}
