import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import TSBetreuung from '../../../models/TSBetreuung';
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
    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager) {
        super(state, gesuchModelManager, undefined);
        this.initViewModel();
    }

    private initViewModel(): void {
    }

    public getBetreuungenList(): Array<TSBetreuung> {
        return new Array<TSBetreuung>();
    }

    public openVerfuegung(): void {

    }
}
