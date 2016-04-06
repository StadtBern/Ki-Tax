import TSAdresse from '../../../models/TSAdresse';
import AdresseRS from '../../service/adresseRS';
import TSLand from '../../../models/TSLand';
import ListResourceRS from '../../service/listResourceRS';

class AdresseComponentConfig implements angular.IComponentOptions {
    transclude = false;
    bindings: any = {
        adresse: '<',
        prefix: '@'
    };
    templateUrl = 'src/core/component/dv-adresse/dv-adresse.html';
    controller = DvAdresseController;
    controllerAs = 'vm';
    require: any = {parentForm: '?^form'};
}


export default class DvAdresseController {
    static $inject = ['adresseRS', 'listResourceRS'];

    adresse: TSAdresse;
    prefix: string;
    adresseRS: AdresseRS;
    parentForm: angular.IFormController;
    popup: any;   //todo team welchen datepicker wollen wir
    laenderList: TSLand[];

    /* @ngInject */
    constructor(adresseRS: AdresseRS, listResourceRS: ListResourceRS) {
        this.adresseRS = adresseRS;
        this.popup = {opened: false};
        listResourceRS.getLaenderList().then((laenderList) => {
            this.laenderList = laenderList;
        });
    }

    submit() {
        this.adresseRS.create(this.adresse)
            .then((response) => {
                if (response.status === 201) {
                    this.resetForm();
                }
            });
    }

    createItem() {
        this.adresse = new TSAdresse('', '', '', '', '', undefined, '', undefined, undefined, undefined);
    }

    resetForm() {
        this.adresse = undefined;
    }

    openPopup() {     //todo team welchen datepicker wollen wir
        this.popup.opened = true;
        console.log(this.popup.opened);
    }

}

angular.module('ebeguWeb.core').component('dvAdresse', new AdresseComponentConfig());
