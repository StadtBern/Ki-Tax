import {IComponentOptions} from 'angular';
let template =  require('./dv-bisher.html');

export class DvBisherComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        gs: '<',
        ja: '<',
        sv: '<'
    };
    template = template;
    controller = DvBisher;
    controllerAs = 'vm';
}

export class DvBisher {
}
