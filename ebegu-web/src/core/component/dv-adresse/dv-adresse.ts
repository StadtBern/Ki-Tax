/// <reference path="../../../../typings/browser.d.ts" />

module app.DvAdresse {
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
        laenderList: any;
        filter:any;

        static $inject = ['adresseRS', 'listResourceRS', '$filter'];
        /* @ngInject */
        constructor(adresseRS:ebeguWeb.services.IAdresseRS, listResourceRS: ebeguWeb.services.IListResourceRS, $filter:any) {
            this.adresseRS = adresseRS;
            this.popup = {opened: false}
            this.filter = $filter;
            listResourceRS.getLaenderList().then((response: any) => {
                this.laenderList = response.data;
                // todo imanol in converter machen
                // Es braucht eine kleine formattierung damit es uebersetzt werden kann.
                for (var i = 0; i < this.laenderList.length; i++) {
                    this.laenderList[i] = 'Land_' + this.laenderList[i];
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
            this.adresse = new ebeguWeb.API.TSAdresse('', '', '', '', '', '', '', undefined, undefined);
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




    export function translatedOrder($filter) {
        return function(item : Array<string>) {
            let result = [];
            if (item !== undefined) {
                for (var i = 0; i < item.length; i++) {
                    result[i] = $filter('translate')(item[i]).toString();
                }

            }
            return result.sort();
        }
    }
    angular.module("ebeguWeb.core").filter('dvTranslatedOrder', translatedOrder);

}
