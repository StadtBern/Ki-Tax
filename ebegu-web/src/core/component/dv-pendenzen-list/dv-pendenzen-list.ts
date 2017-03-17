import {IComponentOptions, IFilterService} from 'angular';
import {TSAntragTyp, getTSAntragTypValues} from '../../../models/enums/TSAntragTyp';
import {TSAntragStatus, getTSAntragStatusPendenzValues} from '../../../models/enums/TSAntragStatus';
import {TSBetreuungsangebotTyp, getTSBetreuungsangebotTypValues} from '../../../models/enums/TSBetreuungsangebotTyp';
import TSInstitution from '../../../models/TSInstitution';
import TSAntragDTO from '../../../models/TSAntragDTO';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import EbeguUtil from '../../../utils/EbeguUtil';
import {InstitutionRS} from '../../service/institutionRS.rest';
import GesuchsperiodeRS from '../../service/gesuchsperiodeRS.rest';
import PendenzRS from '../../../pendenzen/service/PendenzRS.rest';
import GesuchRS from '../../../gesuch/service/gesuchRS.rest';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import BerechnungsManager from '../../../gesuch/service/berechnungsManager';
import {IStateService} from 'angular-ui-router';
import Moment = moment.Moment;
let template = require('./dv-pendenzen-list.html');
require('./dv-pendenzen-list.less');

export class DVPendenzenListConfig implements IComponentOptions {
    transclude = false;

    bindings: any = {
        antraege: '<',
        itemsByPage: '<',
        initialAll: '=',
    };

    template = template;
    controller = DVPendenzenListController;
    controllerAs = 'vm';
}

export class DVPendenzenListController {

    antraege: Array<TSAntragDTO> = []; //muss hier gesuch haben damit Felder die wir anzeigen muessen da sind

    itemsByPage: number;
    initialAll: boolean;

    selectedBetreuungsangebotTyp: string;
    selectedAntragTyp: string;
    selectedAntragStatus: string;
    selectedInstitution: string;
    selectedGesuchsperiode: string;
    institutionenList: Array<TSInstitution>;
    gesuchsperiodenList: Array<string>;


    static $inject: string[] = ['PendenzRS', 'EbeguUtil', '$filter', 'InstitutionRS', 'GesuchsperiodeRS',
        'GesuchRS', 'GesuchModelManager', 'BerechnungsManager', '$state', 'CONSTANTS', 'UserRS', 'AuthServiceRS'];

    constructor(public pendenzRS: PendenzRS, private ebeguUtil: EbeguUtil, private $filter: IFilterService,
                private institutionRS: InstitutionRS, private gesuchsperiodeRS: GesuchsperiodeRS,
                private gesuchRS: GesuchRS, private gesuchModelManager: GesuchModelManager, private berechnungsManager: BerechnungsManager,
                private $state: IStateService, private CONSTANTS: any) {
    }
    $onInit() {
        this.initViewModel();
    }

    private initViewModel() {
        this.updateInstitutionenList();
        this.updateGesuchsperiodenList();
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
        return this.antraege;
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
}



