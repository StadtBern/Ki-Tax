import {IComponentOptions} from 'angular';
import IRootScopeService = angular.IRootScopeService;
let template = require('./dv-zahlung.html');

export class DvZahlungComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = DvZahlungController;
    controllerAs = 'vm';
}

export class DvZahlungController {

    static $inject: any[] = [];

    constructor() {

    }


}
