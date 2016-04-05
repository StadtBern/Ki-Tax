/// <reference path="../../../../typings/browser.d.ts" />
module ebeguWeb.components {
    'use strict';


    export class AdminViewComponentConfig implements angular.IComponentOptions {
        transclude:boolean = false;
        bindings:any = {
            applicationProperties: '<'
        };
        templateUrl:string = 'src/admin/component/adminView/adminView.html';
        controller:any = AdminViewController;
        controllerAs:string = 'vm';

        constructor() {
        }
    }


    export interface IAdminViewController {
        length?:number;
        applicationProperty:any;
        applicationPropertyRS:ebeguWeb.services.IApplicationPropertyRS;
        applicationProperties:any;
        ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil;

        //fetchList: () => angular.IHttpPromise<any>;
        submit:() => void;
        removeRow:(row:any) => void; // todo team add type (muessen warten bis es eine DefinitelyTyped fuer smarttable gibt)
        createItem:() => void;
        editRow:(row:any) => void;
        resetForm:() => void;
    }

    export class AdminViewController implements IAdminViewController {
        length:number;
        applicationProperty:ebeguWeb.API.TSApplicationProperty;
        applicationPropertyRS:ebeguWeb.services.IApplicationPropertyRS;
        applicationProperties:Array<ebeguWeb.API.TSApplicationProperty>;
        ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil;

        static $inject = ['applicationPropertyRS', 'MAX_LENGTH', 'ebeguRestUtil'];

        /* @ngInject */
        constructor(applicationPropertyRS:ebeguWeb.services.IApplicationPropertyRS, MAX_LENGTH:number,
                    ebeguRestUtil: ebeguWeb.utils.EbeguRestUtil) {
            this.length = MAX_LENGTH;
            this.applicationProperty = null;
            this.applicationProperties = null;
            this.applicationPropertyRS = applicationPropertyRS;
            this.ebeguRestUtil = ebeguRestUtil;
            //this.fetchList();
        }

        //fetchList() {
        //    return this.applicationPropertyRS.getAllApplicationProperties();
        //}

        submit() {
            //testen ob aktuelles property schon gespeichert ist
            if (this.applicationProperty.timestampErstellt) {
                this.applicationPropertyRS.update(this.applicationProperty.name, this.applicationProperty.value)
                    .then((response) => {
                        var index = this.getIndexOfElementwithID(response.data);
                        var items:Array<ebeguWeb.API.TSApplicationProperty> = this.ebeguRestUtil.parseApplicationProperties(response.data);
                        if (items != null && items.length > 0) {
                            this.applicationProperties[index] = items[0];
                        }
                    });

            } else {
                this.applicationPropertyRS.create(this.applicationProperty.name, this.applicationProperty.value)
                    .then((response) => {
                        var items:Array<ebeguWeb.API.TSApplicationProperty> = this.ebeguRestUtil.parseApplicationProperties(response.data);
                        if (items != null && items.length > 0) {
                            //todo pruefen ob das item schon existiert hat wie oben
                            this.applicationProperties.push(items[0]);
                        }
                    });
            }
            this.resetForm();
            //todo team fehlerhandling
        }

        removeRow(row:any) {
            this.applicationPropertyRS.remove(row.name).then((reponse:angular.IHttpPromiseCallbackArg<any>) => {
                var index = this.applicationProperties.indexOf(row);
                if (index !== -1) {
                    this.applicationProperties.splice(index, 1);
                    this.resetForm();
                }
            });
        }

        createItem() {
            this.applicationProperty = new ebeguWeb.API.TSApplicationProperty('', '');
        }

        editRow(row) {
            this.applicationProperty = row;
        }

        resetForm() {
            this.applicationProperty = null;
        }

        private  getIndexOfElementwithID(prop : ebeguWeb.API.TSApplicationProperty) {
            var idToSearch = prop.id;
            for (var i = 0; i < this.applicationProperties.length; i++) {
                if (this.applicationProperties[i].id === idToSearch) {
                    return i;
                }
            }
            return -1;

        }
    }

    angular.module('ebeguWeb.admin').component('adminView', new AdminViewComponentConfig());

}
