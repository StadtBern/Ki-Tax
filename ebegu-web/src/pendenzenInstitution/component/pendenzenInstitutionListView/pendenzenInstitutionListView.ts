import {IComponentOptions, IFilterService} from 'angular';
import TSPendenzInstitution from '../../../models/TSPendenzInstitution';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import EbeguUtil from '../../../utils/EbeguUtil';
import {TSBetreuungsangebotTyp, getTSBetreuungsangebotTypValues} from '../../../models/enums/TSBetreuungsangebotTyp';
import TSInstitution from '../../../models/TSInstitution';
import {InstitutionRS} from '../../../core/service/institutionRS.rest';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import GesuchRS from '../../../gesuch/service/gesuchRS.rest';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import BerechnungsManager from '../../../gesuch/service/berechnungsManager';
import ITimeoutService = angular.ITimeoutService;
import PendenzInstitutionRS from '../../service/PendenzInstitutionRS.rest';
let template = require('./pendenzenInstitutionListView.html');
require('./pendenzenInstitutionListView.less');

export class PendenzenInstitutionListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = PendenzenInstitutionListViewController;
    controllerAs = 'vm';
}

export class PendenzenInstitutionListViewController {

    private pendenzenList: Array<TSPendenzInstitution>;
    selectedBetreuungsangebotTyp: string;
    selectedInstitution: string;
    selectedGesuchsperiode: string;
    institutionenList: Array<TSInstitution>;
    betreuungsangebotTypList: Array<TSBetreuungsangebotTyp>;
    activeGesuchsperiodenList: Array<string>;
    itemsByPage: number = 20;
    numberOfPages: number = 1;


    static $inject: string[] = ['PendenzInstitutionRS', 'EbeguUtil', '$filter', 'InstitutionRS', 'GesuchsperiodeRS',
        'GesuchRS', 'GesuchModelManager', 'BerechnungsManager', '$state', 'CONSTANTS', 'UserRS', 'AuthServiceRS'];

    constructor(public pendenzRS: PendenzInstitutionRS, private ebeguUtil: EbeguUtil, private $filter: IFilterService,
                private institutionRS: InstitutionRS, private gesuchsperiodeRS: GesuchsperiodeRS,
                private gesuchRS: GesuchRS, private gesuchModelManager: GesuchModelManager, private berechnungsManager: BerechnungsManager,
                private $state: IStateService, private CONSTANTS: any) {
        this.initViewModel();
    }

    private initViewModel() {
        this.updatePendenzenList();
        this.updateInstitutionenList();
        this.updateBetreuungsangebotTypList();
        this.updateActiveGesuchsperiodenList();
    }

    private updatePendenzenList() {
        this.pendenzRS.getPendenzenList().then((response: any) => {
            this.pendenzenList = angular.copy(response);
            this.numberOfPages = this.pendenzenList.length / this.itemsByPage;
        });
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
        //TODO (Team) Nur die Institution(en) des eingeloggten Benutzers!
        this.institutionRS.getAllInstitutionen().then((response: any) => {
            this.institutionenList = angular.copy(response);
        });
    }

    public updateBetreuungsangebotTypList(): void {
        //TODO (Team) Nur die Betreuungsangebote der Institution(en) des eingeloggten Benutzers!
        this.betreuungsangebotTypList = getTSBetreuungsangebotTypValues();
    }

    public getPendenzenList(): Array<TSPendenzInstitution> {
        return this.pendenzenList;
    }

    public getGesuchsperiodeAsString(gesuchsperiode: TSGesuchsperiode): string {
        return this.ebeguUtil.getGesuchsperiodeAsString(gesuchsperiode);
    }

    public editPendenzInstitution(pendenz: TSPendenzInstitution): void {
        console.log('open Pendenz');
    }
}
