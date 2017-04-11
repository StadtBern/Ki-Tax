import {IComponentOptions, IFilterService} from 'angular';
import {getTSAntragTypValues, TSAntragTyp} from '../../../models/enums/TSAntragTyp';
import {getTSAntragStatusPendenzValues, TSAntragStatus} from '../../../models/enums/TSAntragStatus';
import {getTSBetreuungsangebotTypValues, TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import TSInstitution from '../../../models/TSInstitution';
import TSAntragDTO from '../../../models/TSAntragDTO';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import EbeguUtil from '../../../utils/EbeguUtil';
import {InstitutionRS} from '../../service/institutionRS.rest';
import GesuchsperiodeRS from '../../service/gesuchsperiodeRS.rest';
import {IStateService} from 'angular-ui-router';
import * as moment from 'moment';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import Moment = moment.Moment;
let template = require('./dv-pendenzen-list.html');
require('./dv-pendenzen-list.less');

export class DVPendenzenListConfig implements IComponentOptions {
    transclude = false;

    bindings: any = {
        antraege: '<',
        itemsByPage: '<',
        initialAll: '=',
        showSelectionAll: '=',
    };

    template = template;
    controller = DVPendenzenListController;
    controllerAs = 'vm';
}

export class DVPendenzenListController {

    antraege: Array<TSAntragDTO> = []; //muss hier gesuch haben damit Felder die wir anzeigen muessen da sind

    itemsByPage: number;
    initialAll: boolean;
    showSelectionAll: boolean;

    selectedBetreuungsangebotTyp: string;
    selectedAntragTyp: string;
    selectedAntragStatus: string;
    selectedInstitution: string;
    selectedGesuchsperiode: string;
    institutionenList: Array<TSInstitution>;
    gesuchsperiodenList: Array<string>;


    static $inject: string[] = ['EbeguUtil', '$filter', 'InstitutionRS', 'GesuchsperiodeRS',
        '$state', 'CONSTANTS', 'AuthServiceRS'];

    constructor(private ebeguUtil: EbeguUtil, private $filter: IFilterService,
                private institutionRS: InstitutionRS, private gesuchsperiodeRS: GesuchsperiodeRS,
                private $state: IStateService, private CONSTANTS: any, private authServiceRS: AuthServiceRS) {
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
        return getTSAntragStatusPendenzValues(this.authServiceRS.getPrincipalRole());
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



