import {IPromise} from 'angular';
import IDialogService = angular.material.IDialogService;
import ITranslateService = angular.translate.ITranslateService;

export class RemoveDialogController {

    static $inject = ['$mdDialog', '$translate', 'title', 'deleteText'];

    deleteText: string;
    title: string;

    constructor(private $mdDialog: IDialogService, $translate: ITranslateService, title: string, deleteText: string) {
        if (deleteText !== undefined && deleteText !== null) {
            this.deleteText = $translate.instant(deleteText);
        } else {
            this.deleteText = $translate.instant('LOESCHEN_DIALOG_TEXT');
        }

        if (title !== undefined && title !== null) {
            this.title = $translate.instant(title);

        } else {
            this.title = $translate.instant('LOESCHEN_DIALOG_TITLE');
        }
    }

    public hide(): IPromise<any> {
        return this.$mdDialog.hide();
    }

    public cancel(): void {
        this.$mdDialog.cancel();
    }
}
