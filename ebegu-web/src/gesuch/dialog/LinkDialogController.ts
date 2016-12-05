import {IPromise} from 'angular';
import IDialogService = angular.material.IDialogService;
import ITranslateService = angular.translate.ITranslateService;

export class LinkDialogController {

    static $inject = ['$mdDialog', '$translate', 'title', 'link'];

    title: string;
    link: string;

    constructor(private $mdDialog: IDialogService, $translate: ITranslateService, title: string, link: string) {

        if (title !== undefined && title !== null) {
            this.title = $translate.instant(title);

        } else {
            this.title = $translate.instant('LOESCHEN_DIALOG_TITLE');
        }

        if (link !== undefined && link !== null) {
            this.link = link;

        } else {
            this.link = '#';
        }

    }

    public hide(): IPromise<any> {
        return this.$mdDialog.hide();
    }

    public cancel(): void {
        this.$mdDialog.cancel();
    }
}
