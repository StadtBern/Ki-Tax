import {IComponentOptions} from 'angular';
import IRootScopeService = angular.IRootScopeService;
let template = require('./dv-zahlungsauftrag.html');

export class DvZahlungsauftragComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = DvZahlungsauftragController;
    controllerAs = 'vm';
}

export class DvZahlungsauftragController {

    static $inject: any[] = [];

    constructor() {

    }


}
