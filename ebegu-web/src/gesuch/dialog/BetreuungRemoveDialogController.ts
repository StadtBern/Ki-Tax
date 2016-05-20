import {IPromise} from 'angular';
import IDialogService = angular.material.IDialogService;

export class BetreuungRemoveDialogController {

    static $inject = ['$mdDialog', 'kindName', 'betreuungsangebottyp'];

    constructor(private $mdDialog: IDialogService, private kindName: string, private betreuungsangebottyp: string) {
    }

    public hide(): IPromise<any> {
        return this.$mdDialog.hide();
    }

    public cancel(): void {
        this.$mdDialog.cancel();
    }
}
