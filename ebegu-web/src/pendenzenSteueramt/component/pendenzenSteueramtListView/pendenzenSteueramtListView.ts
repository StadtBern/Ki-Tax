import IComponentOptions = angular.IComponentOptions;
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import {TSAntragStatus, getTSAntragStatusValues} from '../../../models/enums/TSAntragStatus';
import PendenzSteueramtRS from '../../service/pendenzSteueramtRS.rest';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import TSAntragDTO from '../../../models/TSAntragDTO';
import EbeguUtil from '../../../utils/EbeguUtil';
let template = require('./pendenzenSteueramtListView.html');

export class PendenzenSteueramtListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = PendenzenSteueramtListViewController;
    controllerAs = 'vm';
}

export class PendenzenSteueramtListViewController {

    private pendenzenList: Array<TSAntragDTO>;
    activeGesuchsperiodenList: Array<string>;
    itemsByPage: number = 20;
    numberOfPages: number = 1;


    static $inject: string[] = ['GesuchsperiodeRS', 'PendenzSteueramtRS', 'GesuchModelManager', '$state', 'EbeguUtil', 'CONSTANTS'];

    constructor(private gesuchsperiodeRS: GesuchsperiodeRS, public pendenzSteueramtRS: PendenzSteueramtRS, private gesuchModelManager: GesuchModelManager,
                private $state: IStateService, private ebeguUtil: EbeguUtil, private CONSTANTS: any) {
    }

    $onInit() {
        this.initViewModel();
    }

    private initViewModel() {
        this.updatePendenzenList();
        this.updateActiveGesuchsperiodenList();
    }

    private updatePendenzenList() {
        this.pendenzSteueramtRS.getPendenzenList().then((response: any) => {
            this.pendenzenList = angular.copy(response);
            this.numberOfPages = this.pendenzenList.length / this.itemsByPage;
        });
    }

    public getPendenzenList(): Array<TSAntragDTO> {
        return this.pendenzenList;
    }

    public updateActiveGesuchsperiodenList(): void {
        this.gesuchsperiodeRS.getAllActiveGesuchsperioden().then((response: any) => {
            this.activeGesuchsperiodenList = [];
            response.forEach((gesuchsperiode: TSGesuchsperiode) => {
                this.activeGesuchsperiodenList.push(gesuchsperiode.gesuchsperiodeString);
            });
        });
    }

    public getAntragStatus(): Array<TSAntragStatus> {
        return getTSAntragStatusValues();
    }

    public addZerosToFallnummer(fallnummer: number): string {
        return this.ebeguUtil.addZerosToNumber(fallnummer, this.CONSTANTS.FALLNUMMER_LENGTH);
    }

    public editpendenzSteueramt(pendenz: TSAntragDTO, event: any): void {
        if (pendenz) {
            let isCtrlKeyPressed: boolean = (event && event.ctrlKey);
            this.openPendenz(pendenz, isCtrlKeyPressed);
        }
    }

    private openPendenz(pendenz: TSAntragDTO, isCtrlKeyPressed: boolean) {
        this.gesuchModelManager.clearGesuch();
        let navObj: any = {
            gesuchId: pendenz.antragId
        };
        if (isCtrlKeyPressed) {
            let url = this.$state.href('gesuch.familiensituation', navObj);
            window.open(url, '_blank');
        } else {
            this.$state.go('gesuch.familiensituation', navObj);
        }
    }
}
