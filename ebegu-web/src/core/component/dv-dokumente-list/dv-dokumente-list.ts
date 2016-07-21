import {IComponentOptions} from 'angular';
import TSDokumentGrund from '../../../models/TSDokumentGrund';
let template = require('./dv-dokumente-list.html');
require('./dv-dokumente-list.less');

export class DVDokumenteListConfig implements IComponentOptions {
    transclude = false;

    bindings: any = {

        dokumente: '<',
        tableId: '@',
        tableTitle: '@',
        tag: '@',
        titleValue: '<',

    };
    template = template;
    controller = DVDokumenteListController;
    controllerAs = 'vm';
}

export class DVDokumenteListController {

    dokumente: TSDokumentGrund[];
    tableId: string;
    tableTitle: string;
    tag: string;
    titleValue: string;


    static $inject: any[] = [];
    /* @ngInject */
    constructor() {

    }

    $onInit() {

    }

}



