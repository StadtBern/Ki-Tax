import {IComponentOptions, IFilterService} from 'angular';
import TSPendenzInstitution from '../../../models/TSPendenzInstitution';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import EbeguUtil from '../../../utils/EbeguUtil';
import {TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import TSInstitution from '../../../models/TSInstitution';
import {InstitutionRS} from '../../../core/service/institutionRS.rest';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import GesuchRS from '../../../gesuch/service/gesuchRS.rest';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import BerechnungsManager from '../../../gesuch/service/berechnungsManager';
import PendenzInstitutionRS from '../../service/PendenzInstitutionRS.rest';
import {InstitutionStammdatenRS} from '../../../core/service/institutionStammdatenRS.rest';
import ITimeoutService = angular.ITimeoutService;
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


    static $inject: string[] = ['PendenzInstitutionRS', 'EbeguUtil', '$filter', 'InstitutionRS', 'InstitutionStammdatenRS', 'GesuchsperiodeRS',
        'GesuchRS', 'GesuchModelManager', 'BerechnungsManager', '$state', 'CONSTANTS'];

    constructor(public pendenzRS: PendenzInstitutionRS, private ebeguUtil: EbeguUtil, private $filter: IFilterService,
                private institutionRS: InstitutionRS, private institutionStammdatenRS: InstitutionStammdatenRS, private gesuchsperiodeRS: GesuchsperiodeRS,
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
        this.institutionRS.getInstitutionenForCurrentBenutzer().then((response: any) => {
            this.institutionenList = angular.copy(response);
        });
    }

    public updateBetreuungsangebotTypList(): void {
        this.institutionStammdatenRS.getBetreuungsangeboteForInstitutionenOfCurrentBenutzer().then((response: any) => {
            this.betreuungsangebotTypList = angular.copy(response);
        });
    }

    public getPendenzenList(): Array<TSPendenzInstitution> {
        return this.pendenzenList;
    }

    public getGesuchsperiodeAsString(gesuchsperiode: TSGesuchsperiode): string {
        return gesuchsperiode.gesuchsperiodeString;
    }

    public editPendenzInstitution(pendenz: TSPendenzInstitution): void {
        if (pendenz) {
            this.gesuchModelManager.openGesuch(pendenz.gesuchId).then(() => {
                this.openBetreuung(pendenz);
            });
        }
    }

    //TODO (team) Hier wird mit findBetreuungById die Kind-Id auf dem GMM gespeichert, spaeter soll diese als
    // Parameter in die URL kommen. Dann kann in editPendenzInstitution() das openGesuch() entfernt werden
    private openBetreuung(pendenz: TSPendenzInstitution): void {
        if (this.gesuchModelManager.getGesuch() && pendenz) {
            let kindNumber: number = this.gesuchModelManager.findKindById(pendenz.kindId);
            let betreuungNumber: number = this.gesuchModelManager.findBetreuungById(pendenz.betreuungsId);
            if (betreuungNumber > 0) {
                this.berechnungsManager.clear(); // nur um sicher zu gehen, dass alle alte Werte geloescht sind

                // Reload Gesuch in gesuchModelManager on Init in fallCreationView because it has been changed since last time
                this.gesuchModelManager.clearGesuch();

                this.$state.go('gesuch.betreuung', {
                    betreuungNumber: betreuungNumber,
                    kindNumber: kindNumber,
                    gesuchId: pendenz.gesuchId
                });
            }
        }
    }
}
