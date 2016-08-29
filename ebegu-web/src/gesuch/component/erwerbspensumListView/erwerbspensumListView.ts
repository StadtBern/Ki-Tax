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
import ILogService = angular.ILogService;
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
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


export class ErwerbspensumListViewController extends AbstractGesuchViewController {

    erwerbspensenGS1: Array<TSErwerbspensumContainer> = undefined;
    erwerbspensenGS2: Array<TSErwerbspensumContainer>;

    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', '$log', 'DvDialog', 'ErrorService', 'WizardStepManager'];
    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private $log: ILogService, private dvDialog: DvDialog, private errorService: ErrorService, wizardStepManager: WizardStepManager) {
        super(state, gesuchModelManager, berechnungsManager, wizardStepManager);
        var vm = this;
        this.wizardStepManager.updateWizardStepStatus(TSWizardStepName.ERWERBSPENSUM, TSWizardStepStatus.IN_BEARBEITUNG);

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
                this.gesuchModelManager.removeErwerbspensum(pensum);

            });

    }

    editPensum(pensum: any, gesuchstellerNumber: any): void {

        let index: number = this.gesuchModelManager.findIndexOfErwerbspensum(parseInt(gesuchstellerNumber), pensum);
        this.openErwerbspensumView(gesuchstellerNumber, index);

    }

    private openErwerbspensumView(gesuchstellerNumber: number, erwerbspensumNum: number): void {
        this.state.go('gesuch.erwerbsPensum', {
            gesuchstellerNumber: gesuchstellerNumber,
            erwerbspensumNum: erwerbspensumNum
        });
    }

    previousStep() {
        this.state.go('gesuch.betreuungen');
    }

    nextStep() {
        this.errorService.clearAll();
        if (this.gesuchModelManager.isGesuchsteller2Required()) {
            this.state.go('gesuch.finanzielleSituationStart');
        } else {
            this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: 1});
        }
    }
}



