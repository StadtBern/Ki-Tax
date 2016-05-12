import {IPromise} from 'angular';
import IDialogService = angular.material.IDialogService;

export class KindRemoveDialogController {
    static $inject = ['$mdDialog', 'kindName'];
    constructor(private $mdDialog: IDialogService, private kindName: string) {
    }

    public hide(): IPromise<any> {
        return this.$mdDialog.hide();
    }

    public cancel(): void {
        this.$mdDialog.cancel();
    }
}
