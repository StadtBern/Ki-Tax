import {IComponentOptions} from 'angular';
let template = require('./dv-accordion.html');
require('./dv-accordion.less');

export class DvAccordionComponentConfig implements IComponentOptions {
    transclude = true;
    template = template;
    controller = DvAccordionController;
    controllerAs = 'vma';
    bindings: any = {
        onlyone: '<'
    };
}

export class DvAccordionController {
    accordion : string[] = [];
    onlyone : boolean;
    static $inject: any[] = [];
    /* @ngInject */
    constructor() {
    }
    public toggleTab(i: string ): void {
        if (this.isTagOpen(i)) {
            this.accordion.splice(this.accordion.indexOf(i), 1);
        } else {
            if (this.onlyone) {this.accordion = []; }
            this.accordion.push(i);
        }
    }
    public isTagOpen(i: string ): boolean {
        return this.accordion.indexOf(i) > -1;
    }
}
