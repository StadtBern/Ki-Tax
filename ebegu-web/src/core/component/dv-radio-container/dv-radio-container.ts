import {IComponentOptions} from 'angular';
import * as template from './dv-radio-container.html';

export class DvRadioContainerComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        model:'=',
        name: '@',
        ngRequired: '<',
        items: '<'
    };
    template = template;
    controller = DvRadioContainerController;
    controllerAs = 'vm';

}

export class DvRadioContainerController  {

    static $inject: any[] = [];
    /* @ngInject */
    constructor() {
    }
}
