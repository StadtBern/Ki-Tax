import {IComponentOptions} from 'angular';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import TSKindContainer from '../../../models/TSKindContainer';
import AbstractGesuchViewController from '../abstractGesuchView';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import BerechnungsManager from '../../service/berechnungsManager';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import IDialogService = angular.material.IDialogService;
import ITranslateService = angular.translate.ITranslateService;
let template = require('./kinderListView.html');
let removeDialogTempl = require('../../dialog/removeDialogTemplate.html');
require('./kinderListView.less');


export class KinderListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = KinderListViewController;
    controllerAs = 'vm';
}

export class KinderListViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', '$translate', 'DvDialog', 'WizardStepManager'];
    /* @ngInject */
    constructor(private $state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private $translate: ITranslateService, private DvDialog: DvDialog,
                wizardStepManager: WizardStepManager) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.initViewModel();
    }

    private initViewModel(): void {
        this.gesuchModelManager.initKinder();
        this.wizardStepManager.setCurrentStep(TSWizardStepName.KINDER);

        if (this.gesuchModelManager.isThereAnyKindWithBetreuungsbedarf()) {
            this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK);
        } else {
            this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        }
    }

    getKinderList(): Array<TSKindContainer> {
        return this.gesuchModelManager.getKinderList();
    }

    createKind(): void {
        this.gesuchModelManager.createKind();
        this.openKindView(this.gesuchModelManager.getKindNumber());
    }

    editKind(kind: any): void {
        let kindNumber: number = this.gesuchModelManager.findKind(kind);
        if (kindNumber > 0) {
            kind.isSelected = false; // damit die row in der Tabelle nicht mehr als "selected" markiert ist
            this.openKindView(kindNumber);
        }
    }

    private openKindView(kindNumber: number): void {
        this.$state.go('gesuch.kind', {kindNumber: kindNumber, gesuchId: this.getGesuchId()});
    }

    removeKind(kind: any): void {
        var remTitleText = this.$translate.instant('KIND_LOESCHEN', {kindname: kind.kindJA.getFullName()});
        this.DvDialog.showDialog(removeDialogTempl, RemoveDialogController, {
            title: remTitleText,
            deleteText: 'KIND_LOESCHEN_BESCHREIBUNG'
        })
            .then(() => {   //User confirmed removal
                let kindNumber: number = this.gesuchModelManager.findKind(kind);
                if (kindNumber > 0) {
                    this.gesuchModelManager.setKindNumber(kindNumber);
                    this.gesuchModelManager.removeKind();
                }
            });
    }

}
