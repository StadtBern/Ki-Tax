import {IComponentOptions} from 'angular';

export class KinderViewComponentConfig implements IComponentOptions {
    transclude = false;
    templateUrl = 'src/gesuch/component/kinderView/kinderView.html';
    controller = KinderViewController;
    controllerAs = 'vm';
}

export class KinderViewController  {

    static $inject: string[] = [];
    /* @ngInject */
    constructor() {

    }
}
