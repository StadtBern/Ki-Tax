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
import IScope = angular.IScope;
import TSKindDublette from '../../../models/TSKindDublette';
let template = require('./kinderListView.html');
let removeDialogTempl = require('../../dialog/removeDialogTemplate.html');
require('./kinderListView.less');


export class KinderListViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        kinderDubletten: '<'
    };
    template = template;
    controller = KinderListViewController;
    controllerAs = 'vm';
}

export class KinderListViewController extends AbstractGesuchViewController<any> {

    kinderDubletten :TSKindDublette[] = [];

    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', '$translate', 'DvDialog',
        'WizardStepManager', '$scope'];
    /* @ngInject */
    constructor(private $state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private $translate: ITranslateService, private DvDialog: DvDialog,
                wizardStepManager: WizardStepManager, $scope: IScope) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.KINDER);
        this.initViewModel();
    }

    private initViewModel(): void {
        this.gesuchModelManager.initKinder();

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
        this.openKindView(undefined); //neues kind hat noch keinen index
    }

    editKind(kind: any): void {
        if (kind) {         //check entfernt
            kind.isSelected = false; // damit die row in der Tabelle nicht mehr als "selected" markiert ist
            this.openKindView(kind.kindNummer);
        }
    }

    getDublettenInfo(kindContainer: TSKindContainer) {
        let text: string = '';
        if (this.kinderDubletten) {
            let dublettenForThisKind :TSKindDublette[] = [];
            for (let i = 0; i < this.kinderDubletten.length; i++) {
                if (this.kinderDubletten[i].kindNummer === kindContainer.kindNummer) {
                    dublettenForThisKind.push(this.kinderDubletten[i])
                }
            }
            for (let i = 0; i < dublettenForThisKind.length; i++) {
                text += dublettenForThisKind[i].fallNummer;
            }
        }
        return text;
    }

    private openKindView(kindNumber: number): void {
        this.$state.go('gesuch.kind', {kindNumber: kindNumber, gesuchId: this.getGesuchId()});
    }

    removeKind(kind: any): void {
        let remTitleText = this.$translate.instant('KIND_LOESCHEN', {kindname: kind.kindJA.getFullName()});
        this.DvDialog.showDialog(removeDialogTempl, RemoveDialogController, {
            title: remTitleText,
            deleteText: 'KIND_LOESCHEN_BESCHREIBUNG'
        })
            .then(() => {   //User confirmed removal
                let kindIndex: number = this.gesuchModelManager.findKind(kind);
                if (kindIndex >= 0) {
                    this.gesuchModelManager.setKindIndex(kindIndex);
                    this.gesuchModelManager.removeKind();
                }
            });
    }

    /**
     * Ein Kind darf geloescht werden wenn: Das Gesuch noch nicht verfuegt/verfuegen ist und das vorgaengerId null
     * ist (es ist ein neues kind) oder in einer mutation wenn es (obwohl ein altes Kind) keine Betreuungen hat
     * @param kind
     * @returns {boolean}
     */
    public canRemoveKind(kind: TSKindContainer): boolean {
        return !this.isGesuchReadonly()
            && ((this.gesuchModelManager.getGesuch().isMutation() && (!kind.betreuungen || kind.betreuungen.length <= 0))
                || !kind.kindJA.vorgaengerId);
    }

}
