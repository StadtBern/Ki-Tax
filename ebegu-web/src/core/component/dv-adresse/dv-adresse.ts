/// <reference path="../../../../typings/browser.d.ts" />

module app.DvAdresse {
    import EbeguRestUtil = ebeguWeb.utils.EbeguRestUtil;
    'use strict';



    export class AdresseComponentConfig implements angular.IComponentOptions {
        transclude: boolean;
        bindings: any;
        templateUrl: string | Function;
        controller: any;
        controllerAs: string;
        require: any;

        constructor() {
            this.transclude = false;
            this.bindings = {
                adresse:'<',
                prefix:'@'
            };
            this.require = {parentForm: '?^form'};
            this.templateUrl = 'src/core/component/dv-adresse/dv-adresse.html';
            this.controller = DvAdresseController;
            this.controllerAs = 'vm';
        }
    }


    export class DvAdresseController  {
        adresse:ebeguWeb.API.TSAdresse;
        prefix: string;
        adresseRS:ebeguWeb.services.IAdresseRS;
        parentForm: angular.IFormController;
        popup: any;   //todo team welchen datepicker wollen wir
        laenderList: Array<ebeguWeb.API.TSLand>;
        ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil;

        static $inject = ['adresseRS', 'listResourceRS', 'ebeguRestUtil'];
        /* @ngInject */
        constructor(adresseRS:ebeguWeb.services.IAdresseRS, listResourceRS: ebeguWeb.services.IListResourceRS,
                    ebeguRestUtil:ebeguWeb.utils.EbeguRestUtil) {

            this.adresseRS = adresseRS;
            this.popup = {opened: false}
            this.laenderList = [];
            this.ebeguRestUtil = ebeguRestUtil;
            listResourceRS.getLaenderList().then((response: any) => {
                // todo imanol in converter machen
                // Es braucht eine kleine formattierung damit es uebersetzt werden kann.
                for (var i = 0; i < response.data.length; i++) {
                    let parsedCode: string = 'Land_' + response.data[i];
                    let land: ebeguWeb.API.TSLand = ebeguRestUtil.landCodeToTSLand(parsedCode);
                    this.laenderList.push(land);
                }
            });
        }

        submit () {
            this.adresseRS.create(this.adresse)
                .then((response) => {
                    if (response.status === 201) {
                        this.resetForm();
                    }
                });
        }

        createItem() {
            this.adresse = new ebeguWeb.API.TSAdresse('', '', '', '', '', undefined, '', undefined, undefined, undefined);
        }

        resetForm() {
            this.adresse = undefined;
        }

        openPopup(){     //todo team welchen datepicker wollen wir
            this.popup.opened = true;
            console.log(this.popup.opened);
        }

    }

    angular.module('ebeguWeb.core').component('dvAdresse', new AdresseComponentConfig());


}
