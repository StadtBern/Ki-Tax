import {IComponentOptions, IFilterService, IPromise, ILogService} from 'angular';
import TSAbstractAntragEntity from '../../../models/TSAbstractAntragEntity';
import {TSAntragTyp, getTSAntragTypValues} from '../../../models/enums/TSAntragTyp';
import {
    TSAntragStatus, getTSAntragStatusValuesByRole
} from '../../../models/enums/TSAntragStatus';
import {TSBetreuungsangebotTyp, getTSBetreuungsangebotTypValues} from '../../../models/enums/TSBetreuungsangebotTyp';
import TSInstitution from '../../../models/TSInstitution';
import TSAntragDTO from '../../../models/TSAntragDTO';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import EbeguUtil from '../../../utils/EbeguUtil';
import TSAntragSearchresultDTO from '../../../models/TSAntragSearchresultDTO';
import {InstitutionRS} from '../../service/institutionRS.rest';
import GesuchsperiodeRS from '../../service/gesuchsperiodeRS.rest';
import * as moment from 'moment';
import Moment = moment.Moment;
import IDocumentService = angular.IDocumentService;
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
let template = require('./dv-antrag-list.html');
require('./dv-antrag-list.less');

export class DVAntragListConfig implements IComponentOptions {
    transclude = false;

    bindings: any = {
        onRemove: '&',
        onAdd: '&',
        onEdit: '&',
        onFilterChange: '&',
        antraege: '<',
        tableId: '@',
        tableTitle: '@',
        actionVisible: '@',
        addButtonVisible: '@',
        addButtonText: '@'
    };
    template = template;
    controller = DVAntragListController;
    controllerAs = 'vm';
}

export class DVAntragListController {

    antraege: Array<TSAntragDTO> = []; //muss hier gesuch haben damit Felder die wir anzeigen muessen da sind
    displayedCollection: Array<TSAntragDTO> = []; //Liste die im Gui angezeigt wird
    pagination: any;
    gesuchsperiodenList: Array<string>;
    institutionenList: Array<TSInstitution>;

    selectedBetreuungsangebotTyp: string;
    selectedAntragTyp: string;
    selectedAntragStatus: string;
    selectedInstitution: string;
    selectedGesuchsperiode: string;

    tableId: string;
    tableTitle: string;
    actionVisible: string;

    removeButtonTitle: string;
    addButtonText: string;
    addButtonVisible: string = 'false';
    onRemove: (pensumToRemove: any) => void;
    onFilterChange: (changedTableState: any) => IPromise<any>;
    onEdit: (pensumToEdit: any) => void;
    onAdd: () => void;
    TSRoleUtil: any;

    static $inject: any[] = ['EbeguUtil', '$filter', '$log', 'InstitutionRS', 'GesuchsperiodeRS', 'CONSTANTS', 'AuthServiceRS'];
    /* @ngInject */
    constructor(private ebeguUtil: EbeguUtil, private $filter: IFilterService, private $log: ILogService,
                private institutionRS: InstitutionRS, private gesuchsperiodeRS: GesuchsperiodeRS,
                private CONSTANTS: any, private authServiceRS: AuthServiceRS) {
        this.removeButtonTitle = 'Eintrag entfernen';
        this.initViewModel();
        this.TSRoleUtil = TSRoleUtil;
    }

    private initViewModel() {
        //statt diese Listen zu laden koenne man sie auch von aussen setzen
        this.updateInstitutionenList();
        this.updateGesuchsperiodenList();
    }

    $onInit() {
        if (!this.addButtonText) {
            this.addButtonText = 'add item';
        }
        if (this.addButtonVisible === undefined) {
            this.addButtonVisible = 'false';
        }
        //clear selected
        if (this.antraege) {
            for (let i = 0; i < this.antraege.length; i++) {
                let obj: any = this.antraege[i];
                obj.isSelected = false;

            }
        }
        this.displayedCollection.concat(this.antraege);
        // this.callServer(undefined);
    }

    public updateInstitutionenList(): void {
        this.institutionRS.getInstitutionenForCurrentBenutzer().then((response: any) => {
            this.institutionenList = angular.copy(response);
        });
    }

    public updateGesuchsperiodenList(): void {
        this.gesuchsperiodeRS.getAllGesuchsperioden().then((response: any) => {
            this.gesuchsperiodenList = [];
            response.forEach((gesuchsperiode: TSGesuchsperiode) => {
                this.gesuchsperiodenList.push(gesuchsperiode.gesuchsperiodeString);
            });
        });
    }

    removeClicked(antragToRemove: TSAbstractAntragEntity) {
        this.onRemove({antrag: antragToRemove});
    }

    editClicked(antragToEdit: any, event: any) {
        this.onEdit({antrag: antragToEdit, event: event});
    }

    addClicked() {
        this.onAdd();
    }

    private callServer = (tableFilterState: any) => {
        let pagination = tableFilterState.pagination;
        this.pagination = pagination;

        // this.displaydAntraege = this.antraege;

        if (this.onFilterChange && angular.isFunction(this.onFilterChange)) {
            this.onFilterChange({tableState: tableFilterState}).then((result: TSAntragSearchresultDTO) => {
                // this.pagination.totalItemCount = result.totalResultSize;
                if (result) {
                    pagination.totalItemCount = result.totalResultSize;
                    pagination.numberOfPages = Math.ceil(result.totalResultSize / pagination.number);
                    this.displayedCollection = [].concat(result.antragDTOs);
                }
            });
        } else {
            this.$log.info('no callback function spcified for filtering');
        }
    }

    public getAntragTypen(): Array<TSAntragTyp> {
        return getTSAntragTypValues();
    }

    /**
     * Alle TSAntragStatus fuer das Filterdropdown
     * @returns {Array<TSAntragStatus>}
     */
    public getAntragStatus(): Array<TSAntragStatus> {
        return getTSAntragStatusValuesByRole(this.authServiceRS.getPrincipalRole());
    }

    /**
     * Alle Betreuungsangebot typen fuer das Filterdropdown
     * @returns {Array<TSBetreuungsangebotTyp>}
     */
    public getBetreuungsangebotTypen(): Array<TSBetreuungsangebotTyp> {
        return getTSBetreuungsangebotTypValues();
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

    public isAddButtonVisible(): boolean {
        return this.addButtonVisible === 'true';
    }

    public isActionsVisible() {
        return this.actionVisible === 'true';
    }

}



