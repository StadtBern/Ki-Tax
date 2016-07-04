import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSKindContainer from '../../../models/TSKindContainer';
import TSBetreuung from '../../../models/TSBetreuung';
import {TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import BerechnungsManager from '../../service/berechnungsManager';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import EbeguUtil from '../../../utils/EbeguUtil';
import ErrorService from '../../../core/errors/service/ErrorService';
import IDialogService = angular.material.IDialogService;
import ITranslateService = angular.translate.ITranslateService;
let template = require('./betreuungListView.html');
require('./betreuungListView.less');
let removeDialogTemplate = require('../../dialog/removeDialogTemplate.html');


export class BetreuungListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = BetreuungListViewController;
    controllerAs = 'vm';
}

/**
 * View fuer die Liste der Betreeungen der eingegebenen Kinder
 */
export class BetreuungListViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$state', 'GesuchModelManager', '$translate', 'DvDialog', 'EbeguUtil', 'BerechnungsManager', 'ErrorService'];
    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager, private $translate: ITranslateService,
                private DvDialog: DvDialog, private ebeguUtil: EbeguUtil, berechnungsManager: BerechnungsManager,
                private errorService: ErrorService) {
        super(state, gesuchModelManager, berechnungsManager);
    }

    submit(): void {
        this.nextStep();
    }

    previousStep(): void {
        this.state.go('gesuch.kinder');
    }

    nextStep(): void {
        this.state.go('gesuch.erwerbsPensen');
    }

    public editBetreuung(kind: TSKindContainer, betreuung: any): void {
        this.gesuchModelManager.findKind(kind);
        let betreuungNumber: number = this.gesuchModelManager.findBetreuung(betreuung);
        if (betreuungNumber > 0) {
            betreuung.isSelected = false; // damit die row in der Tabelle nicht mehr als "selected" markiert ist
            this.openBetreuungView();
        }
    }

    public getKinderWithBetreuungList(): Array<TSKindContainer> {
        return this.gesuchModelManager.getKinderWithBetreuungList();
    }

    public createBetreuung(kind: TSKindContainer): void {
        let kindNumber: number = this.gesuchModelManager.findKind(kind);
        if (kindNumber > 0) {
            this.gesuchModelManager.setKindNumber(kindNumber);
            this.gesuchModelManager.createBetreuung();
            this.openBetreuungView();
        }
    }

    public removeBetreuung(kind: TSKindContainer, betreuung: TSBetreuung): void {
        this.gesuchModelManager.findKind(kind);
        var remTitleText: any = this.$translate.instant('BETREUUNG_LOESCHEN', {
            kindname: this.gesuchModelManager.getKindToWorkWith().kindJA.getFullName(),
            betreuungsangebottyp: this.ebeguUtil.translateString(TSBetreuungsangebotTyp[betreuung.institutionStammdaten.betreuungsangebotTyp])
        });
        this.DvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
            title: remTitleText,
            deleteText: 'BETREUUNG_LOESCHEN_BESCHREIBUNG'
        }).then(() => {   //User confirmed removal
            this.errorService.clearAll();
            let betreuungNumber: number = this.gesuchModelManager.findBetreuung(betreuung);
            if (betreuungNumber > 0) {
                this.gesuchModelManager.setBetreuungNumber(betreuungNumber);
                this.gesuchModelManager.removeBetreuung();
            }
        });
    }

    private openBetreuungView(): void {
        this.state.go('gesuch.betreuung');
    }

    /**
     * Gibt den Betreuungsangebottyp der Institution, die mit der gegebenen Betreuung verknuepft ist zurueck.
     * By default wird ein Leerzeichen zurueckgeliefert.
     * @param betreuung
     * @returns {string}
     */
    public getBetreuungsangebotTyp(betreuung: TSBetreuung): string {
        if (betreuung && betreuung.institutionStammdaten) {
            return TSBetreuungsangebotTyp[betreuung.institutionStammdaten.betreuungsangebotTyp];
        }
        return '';
    }
}
