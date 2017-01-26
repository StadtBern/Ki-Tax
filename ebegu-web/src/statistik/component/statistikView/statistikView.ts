import {IComponentOptions} from 'angular';
import EbeguUtil from '../../../utils/EbeguUtil';
import {IStateService} from 'angular-ui-router';

let template = require('./statistikView.html');
require('./statistikView.less');

export class StatistikViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = StatistikViewController;
    controllerAs = 'vm';
}

export class StatistikViewController {

    static $inject: string[] = ['EbeguUtil', '$state'];

    constructor(private ebeguUtil: EbeguUtil, private $state: IStateService) {
        this.initViewModel();
    }

    private initViewModel() {
    }
}
