import {IComponentOptions} from 'angular';
import TSAbstractAntragEntity from '../../../models/TSAbstractAntragEntity';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
let template = require('./pendenzenListView.html');
require('./pendenzenListView.less');

export class PendenzenListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = PendenzenListViewController;
    controllerAs = 'vm';
}

export class PendenzenListViewController {


    static $inject: string[] = ['GesuchModelManager'];

    constructor(public gesuchModelManager: GesuchModelManager) {}

    public getPendenzenList(): Array<TSAbstractAntragEntity> {
        return this.gesuchModelManager.getPendenzenList();
    }
}
