import {IComponentOptions} from 'angular';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import TSKindContainer from '../../../models/TSKindContainer';
import AbstractGesuchViewController from '../abstractGesuchView';
let template = require('./kinderListView.html');

export class KinderListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = KinderListViewController;
    controllerAs = 'vm';
}

export class KinderListViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$state', 'GesuchModelManager'];
    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager) {
        super(state, gesuchModelManager);
        this.initViewModel();
    }

    private initViewModel(): void {
        this.gesuchModelManager.initKinder();
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
        this.state.go('gesuch.kind', {kindNumber: kindNumber});
    }

    submit(): void {
        this.nextStep();
    }

    previousStep(): void {
        if ((this.gesuchModelManager.gesuchstellerNumber === 2)) {
            this.state.go('gesuch.stammdaten', {gesuchstellerNumber: 2});
        } else {
            this.state.go('gesuch.stammdaten', {gesuchstellerNumber: 1});
        }
    }

    // TODO (team) vorübergehend direkt auf FinanzSit navigieren
    nextStep()  {
        this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: 1});
    }
}
