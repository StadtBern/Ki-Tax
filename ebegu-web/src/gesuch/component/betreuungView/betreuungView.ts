import {IComponentOptions, IFormController} from 'angular';
import {IStateService} from 'angular-ui-router';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSKindContainer from '../../../models/TSKindContainer';
import {getTSBetreuungsangebotTypValues, TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import {TSInstitutionStammdaten} from '../../../models/TSInstitutionStammdaten';
import TSBetreuungspensumContainer from '../../../models/TSBetreuungspensumContainer';
import TSBetreuung from '../../../models/TSBetreuung';
import TSBetreuungspensum from '../../../models/TSBetreuungspensum';
import {TSDateRange} from '../../../models/types/TSDateRange';
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';
import BerechnungsManager from '../../service/berechnungsManager';
import EbeguUtil from '../../../utils/EbeguUtil';
import ErrorService from '../../../core/errors/service/ErrorService';
import Moment = moment.Moment;
let template = require('./betreuungView.html');
require('./betreuungView.less');

export class BetreuungViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = BetreuungViewController;
    controllerAs = 'vm';
}

export class BetreuungViewController extends AbstractGesuchViewController {
    betreuungsangebot: any;
    betreuungsangebotValues: Array<any>;
    instStammId: string; //der ausgewaehlte instStammId wird hier gespeichert und dann in die entsprechende InstitutionStammdaten umgewandert

    static $inject = ['$state', 'GesuchModelManager', 'EbeguUtil', 'CONSTANTS', '$scope', 'BerechnungsManager', 'ErrorService'];
    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager, private ebeguUtil: EbeguUtil, private CONSTANTS: any,
                private $scope: any, berechnungsManager: BerechnungsManager, private errorService: ErrorService) {
        super(state, gesuchModelManager, berechnungsManager);
        this.setBetreuungsangebotTypValues();
        this.betreuungsangebot = undefined;
        this.initViewModel();

        //Wenn die Maske KindView verlassen wird, werden automatisch die Kinder entfernt, die noch nicht in der DB gespeichert wurden
        $scope.$on('$stateChangeStart', () => {
            this.removeBetreuungFromKind();
        });
    }

    private initViewModel() {
        if (this.getInstitutionSD()) {
            this.instStammId = this.getInstitutionSD().id;
            this.betreuungsangebot = this.getBetreuungsangebotFromInstitutionList();
        }
        if (!this.getBetreuungspensen() || this.getBetreuungspensen().length === 0) {
            this.createBetreuungspensum();
        }
    }

    public getGesuchsperiodeBegin(): Moment {
        return this.gesuchModelManager.getGesuchsperiodeBegin();
    }

    private getBetreuungsangebotFromInstitutionList() {
        return $.grep(this.betreuungsangebotValues, (value: any) => {
            return value.key === this.getInstitutionSD().betreuungsangebotTyp;
        })[0];
    }

    public getKindModel(): TSKindContainer {
        return this.gesuchModelManager.getKindToWorkWith();
    }

    public getBetreuungModel(): TSBetreuung {
        return this.gesuchModelManager.getBetreuungToWorkWith();
    }

    public changedAngebot() {
        if (this.getBetreuungModel()) {
            if (this.isTagesschule()) {
                this.getBetreuungModel().betreuungsstatus = TSBetreuungsstatus.SCHULAMT;
            } else {
                this.getBetreuungModel().betreuungsstatus = TSBetreuungsstatus.AUSSTEHEND;
            }
        }
    }

    submit(form: IFormController): void {
        if (form.$valid) {
            if (this.getBetreuungModel()) {
                if (this.isTagesschule()) {
                    this.getBetreuungModel().betreuungspensumContainers = []; // fuer Tagesschule werden keine Betreuungspensum benoetigt, deswegen löschen wir sie vor dem Speichern
                }
                if (!this.isTageseltern()) {
                    this.getBetreuungModel().schulpflichtig = undefined;
                } else if (!this.getBetreuungModel().schulpflichtig) {
                    this.getBetreuungModel().schulpflichtig = false; // sollte es undefined sein setzen wir es direkt auf false
                }
            }
            this.errorService.clearAll();
            this.gesuchModelManager.updateBetreuung().then((betreuungResponse: any) => {
                this.state.go('gesuch.betreuungen');
            }).catch((exception) => {
                //todo team Fehler anzeigen
                return undefined;
            });
        }
    }

    private setBetreuungsangebotTypValues(): void {
        this.betreuungsangebotValues = this.ebeguUtil.translateStringList(getTSBetreuungsangebotTypValues());
    }

    cancel() {
        this.removeBetreuungFromKind();
        this.state.go('gesuch.betreuungen');
    }

    private removeBetreuungFromKind(): void {
        if (this.gesuchModelManager.getBetreuungToWorkWith() && !this.gesuchModelManager.getBetreuungToWorkWith().timestampErstellt) {
            //wenn das Kind noch nicht erstellt wurde, löschen wir das Kind vom Array
            this.gesuchModelManager.removeBetreuungFromKind();
        }
    }

    public getInstitutionenSDList(): Array<TSInstitutionStammdaten> {
        let result: Array<TSInstitutionStammdaten> = [];
        if (this.betreuungsangebot) {
            this.gesuchModelManager.institutionenList.forEach((instStamm: TSInstitutionStammdaten) => {
                if (instStamm.betreuungsangebotTyp === this.betreuungsangebot.key) {
                    result.push(instStamm);
                }
            });
        }
        return result;
    }

    public getInstitutionSD(): TSInstitutionStammdaten {
        if (this.getBetreuungModel()) {
            return this.getBetreuungModel().institutionStammdaten;
        }
        return undefined;
    }

    public getBetreuungspensen(): Array<TSBetreuungspensumContainer> {
        if (this.getBetreuungModel()) {
            return this.getBetreuungModel().betreuungspensumContainers;
        }
        return undefined;
    }

    public getBetreuungspensum(index: number): TSBetreuungspensumContainer {
        if (this.getBetreuungspensen() && index >= 0 && index < this.getBetreuungspensen().length) {
            return this.getBetreuungspensen()[index];
        }
        return undefined;
    }

    public createBetreuungspensum(): void {
        if (this.getBetreuungModel() && (this.getBetreuungspensen() === undefined || this.getBetreuungspensen() === null)) {
            this.getBetreuungModel().betreuungspensumContainers = [];
        }
        this.getBetreuungspensen().push(new TSBetreuungspensumContainer(undefined, new TSBetreuungspensum(undefined, new TSDateRange())));
    }

    public setSelectedInstitutionStammdaten(): void {
        let instStamList = this.gesuchModelManager.institutionenList;
        for (let i: number = 0; i < instStamList.length; i++) {
            if (instStamList[i].id === this.instStammId) {
                this.gesuchModelManager.getBetreuungToWorkWith().institutionStammdaten = instStamList[i];
            }
        }
    }

    public platzAnfordern(form: IFormController): void {
        this.gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus = TSBetreuungsstatus.WARTEN;
        this.submit(form);
    }

    /**
     * Returns true when the user is allowed to edit the content. This happens when the status is AUSSTEHEHND or SCHULAMT
     * @returns {boolean}
     */
    public isEnabled(): boolean {
        if (this.getBetreuungModel()) {
            return (this.getBetreuungModel().betreuungsstatus === TSBetreuungsstatus.AUSSTEHEND
                || this.getBetreuungModel().betreuungsstatus === TSBetreuungsstatus.SCHULAMT);
        }
        return false;
    }

    public isBetreuungsstatusWarten(): boolean {
        return this.isBetreuungsstatus(TSBetreuungsstatus.WARTEN);
    }

    public isBetreuungsstatusAbgewiesen(): boolean {
        return this.isBetreuungsstatus(TSBetreuungsstatus.ABGEWIESEN);
    }

    public isBetreuungsstatusBestaetigt(): boolean {
        return this.isBetreuungsstatus(TSBetreuungsstatus.BESTAETIGT);
    }

    private isBetreuungsstatus(status: TSBetreuungsstatus): boolean {
        if (this.getBetreuungModel()) {
            return this.getBetreuungModel().betreuungsstatus === status;
        }
        return false;
    }

    public isTagesschule(): boolean {
        return this.isBetreuungsangebottyp(TSBetreuungsangebotTyp.TAGESSCHULE);
    }

    public isTageseltern(): boolean {
        return this.isBetreuungsangebottyp(TSBetreuungsangebotTyp.TAGESELTERN);
    }

    private isBetreuungsangebottyp(betAngTyp: TSBetreuungsangebotTyp): boolean {
        if (this.betreuungsangebot) {
            return this.betreuungsangebot.key === TSBetreuungsangebotTyp[betAngTyp];
        }
        return false;
    }

}
