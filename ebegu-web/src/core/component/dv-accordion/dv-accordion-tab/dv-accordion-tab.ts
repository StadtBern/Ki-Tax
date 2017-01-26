import {IComponentOptions} from 'angular';
import {DvAccordionController} from '../dv-accordion';
let template = require('./dv-accordion-tab.html');

export class DvAccordionTabComponentConfig implements IComponentOptions {
    transclude = true;
    template = template;
    controller = DvAccordionTabController;
    controllerAs = 'vmt';
    bindings: any = {
        title: '@',
        tabid: '@',
        active: '='
    };
    require: any = {vma: '^dvAccordion'};

}

export class DvAccordionTabController {
    title: string;
    tabid: string;
    vma: DvAccordionController;
    static $inject: any[] = [];
    /* @ngInject */
    constructor() {
    }
}
