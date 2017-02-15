import {IPromise} from 'angular';
import {FreigabeViewController} from '../component/freigabeView/freigabeView';
import IDialogService = angular.material.IDialogService;
import ITranslateService = angular.translate.ITranslateService;

export class FreigabeDialogController {

    static $inject = ['$mdDialog', '$translate', 'parentController'];

    deleteText: string;
    title: string;

    constructor(private $mdDialog: IDialogService, $translate: ITranslateService, private parentController: FreigabeViewController) {
        this.title = $translate.instant('CONFIRM_GESUCH_FREIGEBEN');
        this.deleteText = $translate.instant('CONFIRM_GESUCH_FREIGEBEN_DESCRIPTION');
    }

    public hide(): IPromise<any> {
        this.parentController.confirmationCallback();
        return this.$mdDialog.hide();
    }

    public cancel(): void {
        this.$mdDialog.cancel();
    }
}
