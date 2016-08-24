import {IComponentOptions, ILogService} from 'angular';
import IFormController = angular.IFormController;
let template = require('./kommentarView.html');
require('./kommentarView.less');

export class KommentarViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = KommentarViewController;
    controllerAs = 'vm';
}

/**
 * Controller fuer den Kommentarn Upload
 */
export class KommentarViewController {


    static $inject: string[] = ['$log'];
    /* @ngInject */
    constructor(private $log: ILogService) {

    }

}
