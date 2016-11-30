import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSGesuchsteller from '../../../models/TSGesuchsteller';
import TSErwerbspensumContainer from '../../../models/TSErwerbspensumContainer';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import BerechnungsManager from '../../service/berechnungsManager';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import ErrorService from '../../../core/errors/service/ErrorService';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import TSKindContainer from '../../../models/TSKindContainer';
import {TSBetreuungsangebotTypUtil} from '../../../utils/TSBetreuungsangebotTypUtil';
import ILogService = angular.ILogService;
let template = require('./erwerbspensumListView.html');
let removeDialogTemplate = require('../../dialog/removeDialogTemplate.html');
require('./erwerbspensumListView.less');


export class ErwerbspensumListViewComponentConfig implements IComponentOptions {
    transclude: boolean;
    bindings: any;
    template: string | Function;
    controller: any;
    controllerAs: string;


    constructor() {
        this.transclude = false;
        this.bindings = {};
        this.template = template;
        this.controller = ErwerbspensumListViewController;
        this.controllerAs = 'vm';
    }
}


export class ErwerbspensumListViewController extends AbstractGesuchViewController<any> {

    erwerbspensenGS1: Array<TSErwerbspensumContainer> = undefined;
    erwerbspensenGS2: Array<TSErwerbspensumContainer>;

    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', '$log', 'DvDialog', 'ErrorService', 'WizardStepManager'];
    /* @ngInject */
    constructor(private $state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private $log: ILogService, private dvDialog: DvDialog, private errorService: ErrorService, wizardStepManager: WizardStepManager) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        var vm = this;
        this.initErwerbspensumStepStatus();
    }

    private initErwerbspensumStepStatus() {
        this.wizardStepManager.setCurrentStep(TSWizardStepName.ERWERBSPENSUM);
        if (this.isSaveDisabled()) {
            this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        } else {
            this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK);
        }
    }

    getErwerbspensenListGS1(): Array<TSErwerbspensumContainer> {
        if (this.erwerbspensenGS1 === undefined) {
            //todo team, hier die daten vielleicht reingeben statt sie zu lesen
            if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().gesuchsteller1 &&
                this.gesuchModelManager.getGesuch().gesuchsteller1.erwerbspensenContainer) {
                let gesuchsteller1: TSGesuchsteller = this.gesuchModelManager.getGesuch().gesuchsteller1;
                this.erwerbspensenGS1 = gesuchsteller1.erwerbspensenContainer;

            } else {
                this.erwerbspensenGS1 = [];
            }
        }
        return this.erwerbspensenGS1;
    }

    getErwerbspensenListGS2(): Array<TSErwerbspensumContainer> {
        if (this.erwerbspensenGS2 === undefined) {
            //todo team, hier die daten vielleicht reingeben statt sie zu lesen
            if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().gesuchsteller2 &&
                this.gesuchModelManager.getGesuch().gesuchsteller2.erwerbspensenContainer) {
                let gesuchsteller2: TSGesuchsteller = this.gesuchModelManager.getGesuch().gesuchsteller2;
                this.erwerbspensenGS2 = gesuchsteller2.erwerbspensenContainer;

            } else {
                this.erwerbspensenGS2 = [];
            }
        }
        return this.erwerbspensenGS2;

    }


    createErwerbspensum(gesuchstellerNumber: number): void {
        this.openErwerbspensumView(gesuchstellerNumber, undefined);
    }

    removePensum(pensum: any, gesuchstellerNumber: number): void {
        this.errorService.clearAll();
        this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
            deleteText: '',
            title: 'ERWERBSPENSUM_LOESCHEN'
        })
            .then(() => {   //User confirmed removal
                this.gesuchModelManager.setGesuchstellerNumber(gesuchstellerNumber);
                this.gesuchModelManager.removeErwerbspensum(pensum);

            });

    }

    editPensum(pensum: any, gesuchstellerNumber: any): void {
        let index: number = this.gesuchModelManager.findIndexOfErwerbspensum(parseInt(gesuchstellerNumber), pensum);
        this.openErwerbspensumView(gesuchstellerNumber, index);
    }

    private openErwerbspensumView(gesuchstellerNumber: number, erwerbspensumNum: number): void {
        this.$state.go('gesuch.erwerbsPensum', {
            gesuchstellerNumber: gesuchstellerNumber,
            erwerbspensumNum: erwerbspensumNum,
            gesuchId: this.getGesuchId()
        });
    }

    /**
     * Erwerbspensum muss nur erfasst werden, falls mind. 1 Kita oder 1 Tageseltern Kleinkind Angebot erfasst wurde
     * und mind. eines dieser Kinder keine Fachstelle involviert hat
     * @returns {boolean}
     */
    public isErwerbspensumRequired(): boolean {
        let kinderWithBetreuungList: Array<TSKindContainer> = this.gesuchModelManager.getKinderWithBetreuungList();
        for (let kind of kinderWithBetreuungList) {
            for (let betreuung of kind.betreuungen) {
                if (betreuung.institutionStammdaten
                    && TSBetreuungsangebotTypUtil.isRequireErwerbspensum(betreuung.institutionStammdaten.betreuungsangebotTyp)
                    && !kind.kindJA.pensumFachstelle) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gibt true zurueck wenn Erwerbspensen nicht notwendig sind oder wenn sie notwendig sind aber mindestens eins bereits eingetragen wurde
     * @returns {boolean}
     */
    public isSaveDisabled(): boolean {
        let erwerbspensenNumber: number = 0;
        if (this.getErwerbspensenListGS1() && this.getErwerbspensenListGS1().length > 0) {
            erwerbspensenNumber += this.getErwerbspensenListGS1().length;
        }
        if (this.getErwerbspensenListGS2() && this.getErwerbspensenListGS2().length > 0) {
            erwerbspensenNumber += this.getErwerbspensenListGS2().length;
        }
        return this.isErwerbspensumRequired() && erwerbspensenNumber <= 0;
    }
}
