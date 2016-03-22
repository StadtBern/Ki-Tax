/// <reference path="../../../../typings/browser.d.ts" />
module app.DvAdresse {
    'use strict';

    export class AdresseComponentConfig implements angular.IComponentOptions {
        transclude: boolean;
        bindings: any;
        templateUrl: string | Function;
        controller: any;
        controllerAs: string;

        constructor() {
            this.transclude = false;
            this.bindings = {};
            this.templateUrl = 'src/core/component/dv-adresse/dv-adresse.html';
            this.controller = DvAdresseController;
            this.controllerAs = 'vm';
        }
    }


    export class DvAdresseController  {
        maxLength: number;
        adresse:ebeguWeb.API.TSAdresse;
        adresseRS:ebeguWeb.services.IAdresseRS;
        adressen:Array<ebeguWeb.API.TSAdresse>;

        static $inject = ['adresseRS', 'MAX_LENGTH'];
        /* @ngInject */
        constructor(adresseRS:ebeguWeb.services.IAdresseRS, MAX_LENGTH:number) {
            this.maxLength = MAX_LENGTH;
            this.adresse = null;
            this.adressen = null;
            this.adresseRS = adresseRS;
        }

        submit () {
            this.adresseRS.create(this.adresse)
                .then((response) => {
                    if (response.status === 201) {
                        this.resetForm();
                    }
                });
        }

        removeRow(row:any) {
            //this.adresseRS.remove(row.name).then((reponse:angular.IHttpPromiseCallbackArg<any>) => {
            //    var index = this.adressen.indexOf(row);
            //    if (index !== -1) {
            //        this.adressen.splice(index, 1);
            //        this.resetForm();
            //    }
            //});
        }

        createItem() {
            this.adresse = new ebeguWeb.API.TSAdresse('', '', '', '', '', '', '', null, null);
        }

        resetForm() {
            this.adresse = null;
        }

    }

    angular.module('ebeguWeb.core').component('dvAdresse', new AdresseComponentConfig());

}
