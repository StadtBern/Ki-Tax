import {IPromise} from 'angular';
import IDialogService = angular.material.IDialogService;
import ITranslateService = angular.translate.ITranslateService;
import {DvTooltipController} from '../../core/component/dv-tooltip/dv-tooltip';

export class ShowTooltipController {

    static $inject = ['$mdDialog', '$translate', 'title', 'text', 'parentController'];

    title: string;
    text: string;

    constructor(private $mdDialog: IDialogService, $translate: ITranslateService, title: string, text: string,
                private parentController: DvTooltipController) {
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
        this.parentController.setFocusBack();
        this.$mdDialog.cancel();
    }
}
