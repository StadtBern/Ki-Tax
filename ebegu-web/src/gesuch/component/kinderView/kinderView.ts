import {IComponentOptions} from 'angular';
import * as template from './kinderView.html';

export class KinderViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = KinderViewController;
    controllerAs = 'vm';
}

export class KinderViewController  {

    static $inject: string[] = [];
    /* @ngInject */
    constructor() {

    }
}
