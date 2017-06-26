import {IPromise} from 'angular';
import IDialogService = angular.material.IDialogService;
import ITranslateService = angular.translate.ITranslateService;

export class ThreeButtonsDialogController {

    static $inject = ['$mdDialog', '$translate', 'title', 'confirmationText', 'cancelText', 'firstOkText', 'secondOkText'];

    confirmationText: string;
    title: string;
    cancelText: string;
    firstOkText: string;
    secondOkText: string;

    constructor(private $mdDialog: IDialogService, $translate: ITranslateService, title: string, confirmationText: string, cancelText: string,
                firstOkText: string, secondOkText: string) {
        this.title = $translate.instant(title);
        this.confirmationText = $translate.instant(confirmationText);
        this.cancelText = $translate.instant(cancelText);
        this.firstOkText = $translate.instant(firstOkText);
        this.secondOkText = $translate.instant(secondOkText);
    }

    public hide(buttonNumber: number): IPromise<any> {
        return this.$mdDialog.hide(buttonNumber);
    }

    public cancel(): void {
        this.$mdDialog.cancel();
    }
}
