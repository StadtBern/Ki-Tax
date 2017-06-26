import {IComponentOptions} from 'angular';
import TSSearchResultEntry from '../../../../models/dto/TSSearchResultEntry';
let template = require('./dv-search-result-icon.html');

export class DvSearchResultIconComponentConfig implements IComponentOptions {

    transclude = false;
    bindings: any = {
        entry: '<',
    };
    template = template;
    controller = DvSearchResultController;
    controllerAs = 'vm';
}

export class DvSearchResultController {

    entry: TSSearchResultEntry;

    static $inject: any[] = [];

    constructor() {

    }

    //wird von angular aufgerufen
    $onInit() {
        //initial nach aktuell eingeloggtem filtern
    }
}
