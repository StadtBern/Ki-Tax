import {IPromise} from 'angular';
import IDialogService = angular.material.IDialogService;
import IDialogOptions = angular.material.IDialogOptions;

export class DvDialog {

    static $inject: any[] = ['$mdDialog'];
    /* @ngInject */
    constructor(private $mdDialog: IDialogService) {
    }

    /**
     * Erstellt einen neuen confim Dialog mit den uebergegebenen Parametern
     * @param template Man kann ein belibiges Template eingeben in dem man das Layout des ganzen Dialogs gestaltet.
     * @param controller Hier implementiert man die verschiedenen Funktionen, die benoetigt sind 
     * @param params Ein JS-Objekt {key-value}. Alle definierte Keys werden dann mit dem gegebenen Wert in Controller injected 
     * @returns {angular.IPromise<any>}
     */
    public showDialog(template: string, controller?: any, params?: any): IPromise<any> {
        let confirm: IDialogOptions = {
            template: template,
            controller: controller,
            controllerAs: 'vm',
            locals: params
        };

        return this.$mdDialog.show(confirm);
    }

}
