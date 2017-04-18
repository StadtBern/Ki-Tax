import {IPromise} from 'angular';
import IDialogService = angular.material.IDialogService;
import ITranslateService = angular.translate.ITranslateService;
require('./dialogs.less');

export class BemerkungenDialogController {

    title: string;
    bemerkungen: string;

    static $inject = ['$mdDialog', '$translate', 'title', 'bemerkungen'];

    constructor(private $mdDialog: IDialogService, $translate: ITranslateService, title: string, bemerkungen: string) {
        this.title = $translate.instant(title);
        this.bemerkungen = bemerkungen;
    }

    public hide(): IPromise<any> {
        return this.$mdDialog.hide(this.bemerkungen);
    }

    public cancel(): void {
        this.$mdDialog.cancel();
    }
}
