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

        static $inject = ['adresseRS', 'listResourceRS'];
        /* @ngInject */
        constructor(adresseRS:ebeguWeb.services.IAdresseRS, listResourceRS: ebeguWeb.services.IListResourceRS) {

            this.adresseRS = adresseRS;
            this.popup = {opened: false};
            listResourceRS.getLaenderList().then((laenderList) => {
                this.laenderList = laenderList;
            })
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
