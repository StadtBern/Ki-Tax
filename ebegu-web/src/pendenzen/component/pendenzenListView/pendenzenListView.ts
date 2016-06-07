import {IComponentOptions} from 'angular';
import TSPendenz from '../../../models/TSPendenz';
import PendenzRS from '../../service/PendenzRS.rest';
let template = require('./pendenzenListView.html');
require('./pendenzenListView.less');

export class PendenzenListViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = PendenzenListViewController;
    controllerAs = 'vm';
}

export class PendenzenListViewController {


    static $inject: string[] = ['PendenzRS'];
    private pendenzenList: Array<TSPendenz>;

    constructor(public pendenzRS: PendenzRS) {
        this.initViewModel();
    }

    private initViewModel() {
        this.pendenzRS.getPendenzenList().then((response: any) => {
            this.pendenzenList = angular.copy(response);
        });
    }

    public getPendenzenList(): Array<TSPendenz> {
        return this.pendenzenList;
    }
}
