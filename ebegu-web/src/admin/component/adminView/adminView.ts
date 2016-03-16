/// <reference path="../../../../typings/browser.d.ts" />
module ebeguWeb.components {
    'use strict';


    export class ComponentConfig implements angular.IComponentOptions {
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
        length?: number;
        applicationProperty: any;
        applicationPropertyRS: ebeguWeb.services.IApplicationPropertyRS;
        applicationProperties: any;

        //fetchList: () => angular.IHttpPromise<any>;
        submit: () => void;
        removeRow: (row:any) => void; // todo team add type (muessen warten bis es eine DefinitelyTyped fuer smarttable gibt)
        createItem: () => void;
    }

    export class AdminViewController implements IAdminViewController {
        length:number;
        applicationProperty:ebeguWeb.API.ApplicationProperty;
        applicationPropertyRS:ebeguWeb.services.IApplicationPropertyRS;
        applicationProperties:Array<ebeguWeb.API.ApplicationProperty>;

        static $inject = ['applicationPropertyRS', 'MAX_LENGTH'];

        /* @ngInject */
        constructor(applicationPropertyRS:ebeguWeb.services.IApplicationPropertyRS, MAX_LENGTH:number) {
            this.length = MAX_LENGTH;
            this.applicationProperty = null;
            this.applicationProperties = null;
            this.applicationPropertyRS = applicationPropertyRS;
            //this.fetchList();
        }

        //fetchList() {
        //    return this.applicationPropertyRS.getAllApplicationProperties();
        //}

        submit() {
            this.applicationPropertyRS.create(this.applicationProperty.key, this.applicationProperty.value)
                .then((response:angular.IHttpPromiseCallbackArg<any>) => {
                    this.applicationProperty = null;
                    this.applicationProperties.push(response.data);
                });
            //todo team fehlerhandling
        }

        removeRow(row:any) {
            this.applicationPropertyRS.remove(row.name).then((reponse:angular.IHttpPromiseCallbackArg<any>) => {
                var index = this.applicationProperties.indexOf(row);
                if (index !== -1) {
                    this.applicationProperties.splice(index, 1);
                }

            });
        }

        createItem() {
            this.applicationProperty = new ebeguWeb.API.ApplicationProperty('', '');
        }
    }


    angular.module('ebeguWeb.admin').component('adminView', new ComponentConfig());


}
