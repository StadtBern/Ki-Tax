import {IComponentOptions} from 'angular';
import {DvAccordionController} from '../dv-accordion';
let template = require('./dv-accordion-tab.html');

export class DvAccordionTabComponentConfig implements IComponentOptions {
    transclude: any = {
        title: '?tabTitle',
        body: '?tabBody'
    };
    template = template;
    controller = DvAccordionTabController;
    controllerAs = 'vmt';
    bindings: any = {
        tabid: '@',
    };
    require: any = {vma: '^dvAccordion'};

}

export class DvAccordionTabController {
    tabid: string;
    vma: DvAccordionController;
    static $inject: any[] = [];
    /* @ngInject */
    constructor() {
    }
}
