import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import TSBetreuung from '../../../models/TSBetreuung';
import TSKindContainer from '../../../models/TSKindContainer';
import EbeguUtil from '../../../utils/EbeguUtil';
let template = require('./verfuegenListView.html');
require('./verfuegenListView.less');


export class VerfuegenListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = VerfuegenListViewController;
    controllerAs = 'vm';
}

export class VerfuegenListViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$state', 'GesuchModelManager', 'EbeguUtil'];
    private kinderWithBetreuungList: Array<TSKindContainer>;


    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager, private ebeguUtil: EbeguUtil) {
        super(state, gesuchModelManager, undefined);
        this.initViewModel();
    }

    private initViewModel(): void {
        this.kinderWithBetreuungList = this.gesuchModelManager.getKinderWithBetreuungList();
    }

    public getKinderWithBetreuungList(): Array<TSKindContainer> {
        return this.kinderWithBetreuungList;
    }

    public openVerfuegung(kind: TSKindContainer, betreuung: TSBetreuung): void {
        let kindNumber: number = this.gesuchModelManager.findKind(kind);
        if (kindNumber > 0) {
            this.gesuchModelManager.setKindNumber(kindNumber);
            let betreuungNumber: number = this.gesuchModelManager.findBetreuung(betreuung);
            if (betreuungNumber > 0) {
                this.gesuchModelManager.setBetreuungNumber(betreuungNumber);
                this.state.go('gesuch.verfuegenView');
            }
        }
    }

    public getFall() {
        if (this.gesuchModelManager && this.gesuchModelManager.gesuch) {
            return this.gesuchModelManager.gesuch.fall;
        }
        return undefined;
    }

    public getGesuchsperiode() {
        if (this.gesuchModelManager) {
            return this.gesuchModelManager.getGesuchsperiode();
        }
        return undefined;
    }

}
