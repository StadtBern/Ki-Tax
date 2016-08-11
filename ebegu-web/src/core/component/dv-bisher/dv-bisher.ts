import {IComponentOptions} from 'angular';
let template =  require('./dv-bisher.html');
require('./dv-bisher.less');

export class DvBisherComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        gs: '<',
        ja: '<',
    };
    template = template;
    controller = DvBisher;
    controllerAs = 'vm';
}

export class DvBisher {
}
