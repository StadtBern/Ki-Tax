import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import TSBetreuung from '../../../models/TSBetreuung';
import TSKindContainer from '../../../models/TSKindContainer';
let template = require('./verfuegenListView.html');
require('./verfuegenListView.less');


export class VerfuegenListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = VerfuegenListViewController;
    controllerAs = 'vm';
}

export class VerfuegenListViewController extends AbstractGesuchViewController {

    static $inject: string[] = ['$state', 'GesuchModelManager'];
    private kinderWithBetreuungList: Array<TSKindContainer>;


    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager) {
        super(state, gesuchModelManager, undefined);
        this.initViewModel();
    }

    private initViewModel(): void {
        this.kinderWithBetreuungList = this.gesuchModelManager.getKinderWithBetreuungList();
    }

    public getKinderWithBetreuungList(): Array<TSKindContainer> {
        return this.kinderWithBetreuungList;
    }

    public openVerfuegung(betreuung: TSBetreuung): void {

    }

    public calculateBetreuungsId(kindContainer: TSKindContainer, betreuung: TSBetreuung): string {
        // console.log('kindContainer', kindContainer);
        // console.log('betreuung', betreuung);
        return 'EErrdFF';
    }
}
