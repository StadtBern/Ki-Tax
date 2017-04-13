import {IComponentOptions, IFilterService} from 'angular';
import TSAntragDTO from '../../../models/TSAntragDTO';
import PendenzRS from '../../service/PendenzRS.rest';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import EbeguUtil from '../../../utils/EbeguUtil';
import {TSBetreuungsangebotTyp, getTSBetreuungsangebotTypValues} from '../../../models/enums/TSBetreuungsangebotTyp';
import {TSAntragTyp, getTSAntragTypValues} from '../../../models/enums/TSAntragTyp';
import TSInstitution from '../../../models/TSInstitution';
import {InstitutionRS} from '../../../core/service/institutionRS.rest';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import GesuchRS from '../../../gesuch/service/gesuchRS.rest';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import BerechnungsManager from '../../../gesuch/service/berechnungsManager';
import {TSAntragStatus, getTSAntragStatusPendenzValues} from '../../../models/enums/TSAntragStatus';
import ITimeoutService = angular.ITimeoutService;
import Moment = moment.Moment;
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import TSUser from '../../../models/TSUser';
let template = require('./pendenzenListView.html');
require('./pendenzenListView.less');

export class PendenzenListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = PendenzenListViewController;
    controllerAs = 'vm';
}

export class PendenzenListViewController {

    private pendenzenList: Array<TSAntragDTO>;
    selectedBetreuungsangebotTyp: string;
    selectedAntragTyp: string;
    selectedAntragStatus: string;
    selectedInstitution: string;
    selectedGesuchsperiode: string;
    institutionenList: Array<TSInstitution>;
    gesuchsperiodenList: Array<string>;
    itemsByPage: number = 20;
    numberOfPages: number = 1;


    static $inject: string[] = ['PendenzRS', 'EbeguUtil', '$filter', 'InstitutionRS', 'GesuchsperiodeRS',
        'GesuchRS', 'GesuchModelManager', 'BerechnungsManager', '$state', 'CONSTANTS', 'AuthServiceRS'];

    constructor(public pendenzRS: PendenzRS, private ebeguUtil: EbeguUtil, private $filter: IFilterService,
                private institutionRS: InstitutionRS, private gesuchsperiodeRS: GesuchsperiodeRS,
                private gesuchRS: GesuchRS, private gesuchModelManager: GesuchModelManager, private berechnungsManager: BerechnungsManager,
                private $state: IStateService, private CONSTANTS: any, private authServiceRS: AuthServiceRS) {
        this.initViewModel();
    }

    private initViewModel() {
        // Initial werden die Pendenzen des eingeloggten Benutzers geladen
        this.updatePendenzenList(this.authServiceRS.getPrincipal().username);
        this.updateInstitutionenList();
        this.updateGesuchsperiodenList();
    }


    private updatePendenzenList(username: string) {
        this.pendenzRS.getPendenzenListForUser(username).then((response: any) => {
            this.pendenzenList = angular.copy(response);
            this.numberOfPages = this.pendenzenList.length / this.itemsByPage;
        });
    }

    public getAntragTypen(): Array<TSAntragTyp> {
        return getTSAntragTypValues();
    }

    /**
     * Alle TSAntragStatus ausser VERFUEGT. Da es in der Pendenzenliste nicht notwendig ist
     * @returns {Array<TSAntragStatus>}
     */
    public getAntragStatus(): Array<TSAntragStatus> {
        return getTSAntragStatusPendenzValues();
    }

    public getBetreuungsangebotTypen(): Array<TSBetreuungsangebotTyp> {
        return getTSBetreuungsangebotTypValues();
    }

    public updateGesuchsperiodenList(): void {
        this.gesuchsperiodeRS.getAllGesuchsperioden().then((response: any) => {
            this.gesuchsperiodenList = [];
            response.forEach((gesuchsperiode: TSGesuchsperiode) => {
                this.gesuchsperiodenList.push(gesuchsperiode.gesuchsperiodeString);
            });
        });
    }

    public updateInstitutionenList(): void {
        this.institutionRS.getAllInstitutionen().then((response: any) => {
            this.institutionenList = angular.copy(response);
        });
    }

    public getPendenzenList(): Array<TSAntragDTO> {
        return this.pendenzenList;
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

    public editPendenzJA(pendenz: TSAntragDTO, event: any): void {
        if (pendenz) {
            let isCtrlKeyPressed: boolean = (event && event.ctrlKey);
            let navObj: any = {
                createNew: false,
                gesuchId: pendenz.antragId
            };
            if (isCtrlKeyPressed) {
                let url = this.$state.href('gesuch.fallcreation', navObj);
                window.open(url, '_blank');
            } else {
                this.$state.go('gesuch.fallcreation', navObj);
            }
        }
    }

    public userChanged(user: TSUser): void {
        if (user) {
            this.updatePendenzenList(user.username);
        }
    }
}
