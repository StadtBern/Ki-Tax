import TSAdresse from '../../../models/TSAdresse';
import AdresseRS from '../../service/adresseRS.rest';
import TSLand from '../../../models/types/TSLand';
import ListResourceRS from '../../service/listResourceRS.rest';
import {IComponentOptions, IFormController} from 'angular';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
require('./dv-adresse.less');

export class AdresseComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        adresse: '<',
        prefix: '@',
        organisation: '<',
        showNichtInGemeinde: '<'
    };
    template = require('./dv-adresse.html');
    controller = DvAdresseController;
    controllerAs = 'vm';
    require: any = {parentForm: '?^form'};
}


export class DvAdresseController {
    static $inject = ['AdresseRS', 'ListResourceRS', 'GesuchModelManager'];

    adresse: TSAdresse;
    prefix: string;
    adresseRS: AdresseRS;
    parentForm: IFormController;
    popup: any;   //todo team welchen datepicker wollen wir
    laenderList: TSLand[];
    organisation: boolean;
    TSRoleUtil = TSRoleUtil;
    showNichtInGemeinde: boolean;
    gesuchModelManager: GesuchModelManager;

    /* @ngInject */
    constructor(adresseRS: AdresseRS, listResourceRS: ListResourceRS, gesuchModelManager: GesuchModelManager) {
        this.TSRoleUtil = TSRoleUtil;
        this.adresseRS = adresseRS;
        this.gesuchModelManager = gesuchModelManager;
        this.popup = {opened: false};
        listResourceRS.getLaenderList().then((laenderList: TSLand[]) => {
            this.laenderList = laenderList;
        });
    }

    submit() {
        this.adresseRS.create(this.adresse)
            .then((response: any) => {
                if (response.status === 201) {
                    this.resetForm();
                }
            });
    }

    createItem() {
        this.adresse = new TSAdresse('', '', '', '', '', undefined, '', undefined, undefined, undefined, undefined);
    }

    resetForm() {
        this.adresse = undefined;
    }

    openPopup() {     //todo team welchen datepicker wollen wir
        this.popup.opened = true;
    }

    public isGesuchStatusVerfuegenVerfuegt(): boolean {
        return this.gesuchModelManager.isGesuchStatusVerfuegenVerfuegt();
    }

}

