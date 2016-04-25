import {IComponentOptions} from 'angular';
import * as template from './dv-input-container.html';

export class DvInputContainerComponentConfig implements IComponentOptions {
    transclude = true;
    template = template;
    controller = DvInputContainerController;
    controllerAs = 'vm';

}

export class DvInputContainerController  {

    static $inject: any[] = [];
    /* @ngInject */
    constructor() {
    }
}
