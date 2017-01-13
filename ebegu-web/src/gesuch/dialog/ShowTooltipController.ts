import {IPromise} from 'angular';
import IDialogService = angular.material.IDialogService;
import ITranslateService = angular.translate.ITranslateService;

export class ShowTooltipController {

    static $inject = ['$mdDialog', '$translate', 'title', 'text'];

    title: string;
    text: string;

    constructor(private $mdDialog: IDialogService, $translate: ITranslateService, title: string, text: string) {

        /* TODO: Klaeren ob title verwendet werden soll, ist im Moment nicht im Template. Falls nicht: Ausbauen

         if (title !== undefined && title !== null) {
         this.title = $translate.instant(title);

         } else {
         this.title = $translate.instant('');
         }*/
        if (text !== undefined && text !== null) {
            this.text = $translate.instant(text);

        } else {
            this.text = 'test';
        }
    }

    public hide(): IPromise<any> {
        return this.$mdDialog.hide();
    }

    public cancel(): void {
        this.$mdDialog.cancel();
    }
}
