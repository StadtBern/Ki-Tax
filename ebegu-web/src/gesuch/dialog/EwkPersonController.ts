import {IPromise} from 'angular';
import IDialogService = angular.material.IDialogService;
import ITranslateService = angular.translate.ITranslateService;
import TSEWKPerson from '../../models/TSEWKPerson';

export class EwkPersonController {

    static $inject = ['$mdDialog', '$translate', 'person'];

    person: TSEWKPerson;

    constructor(private $mdDialog: IDialogService, $translate: ITranslateService, person: TSEWKPerson) {
        this.person = person;
    }

    public hide(): IPromise<any> {
        return this.$mdDialog.hide();
    }

    public cancel(): void {
        this.$mdDialog.cancel();
    }
}
