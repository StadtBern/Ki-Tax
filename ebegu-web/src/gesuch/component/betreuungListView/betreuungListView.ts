import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSKindContainer from '../../../models/TSKindContainer';
import TSBetreuung from '../../../models/TSBetreuung';
import {TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import IDialogService = angular.material.IDialogService;
let template = require('./betreuungListView.html');


export class BetreuungListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = BetreuungListViewController;
    controllerAs = 'vm';
}

export class BetreuungListViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$state', 'GesuchModelManager', '$mdDialog', 'DvDialog'];
    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager) {
        super(state, gesuchModelManager);
    }

    submit(): void {
        this.nextStep();
    }

    previousStep(): void {
        this.state.go('gesuch.kinder');
    }

    // TODO (team) vorübergehend direkt auf FinanzSit navigieren
    nextStep(): void  {
        this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: 1});
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
