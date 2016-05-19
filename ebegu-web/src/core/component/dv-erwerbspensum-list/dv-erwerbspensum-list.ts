import {IComponentOptions} from 'angular';
import TSErwerbspensum from '../../../models/TSErwerbspensum';
import TSErwerbspensumContainer from '../../../models/TSErwerbspensumContainer';
let template = require('./dv-erwerbspensum-list.html');

export class DVErwerbspensumListConfig implements IComponentOptions {
    transclude = false;

    bindings: any = {
        onRemove: '&',
        onAdd: '&',
        onEdit: '&',
        erwerbspensen: '<',
        tableId: '@',
        tableTitle: '@',
        addButtonVisible: '@',
        addButtonText: '@'
    };
    template = template;
    controller = DVErwerbspensumListController;
    controllerAs = 'vm';
}

export class DVErwerbspensumListController {

    erwerbspensen: TSErwerbspensum[];
    tableId: string;
    tableTitle: string;
    removeButtonTitle: string;
    addButtonText: string;
    addButtonVisible: boolean
    onRemove: (pensumToRemove: any) => void;
    onEdit: (pensumToEdit: any) => void;
    onAdd: () => void;

    static $inject: any[] = [];
    /* @ngInject */
    constructor() {
        this.removeButtonTitle = 'Eintrag entfernen';
    }

    $onInit() {
        if (!this.addButtonText) {
            this.addButtonText = 'add item';
        }
        if (this.addButtonVisible === undefined) {
            this.addButtonVisible = true;
        }
    }

    removeClicked(pensumToRemove: TSErwerbspensumContainer) {
        this.onRemove({pensum: pensumToRemove});
    }

    editClicked(pensumToEdit: any) {
        this.onEdit({pensum: pensumToEdit});
    }

    addClicked() {
        this.onAdd();
    }


}



