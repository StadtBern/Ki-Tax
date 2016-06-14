import {IComponentOptions} from 'angular';
let template = require('./dv-tooltip.html');
require('./dv-tooltip.less');

export class DvTooltipComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = DvTooltipController;
    controllerAs = 'vm';
    bindings: any = {
        text: '<'
    };
}

export class DvTooltipController {

    static $inject: any[] = [];
    /* @ngInject */
    constructor() {
    }
}
