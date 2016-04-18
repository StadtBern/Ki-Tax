import {IComponentOptions} from 'angular';
import * as template from './dv-container.html';

export class DvContainerComponentConfig implements IComponentOptions {
    transclude = true;
    template = template;
    controller = DvContainerController;
    controllerAs = 'vm';

}

export class DvContainerController  {

    static $inject: any[] = [];
    /* @ngInject */
    constructor() {
    }
}

