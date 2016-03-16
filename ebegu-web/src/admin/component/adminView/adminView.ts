/// <reference path="../../../../typings/browser.d.ts" />
module ebeguWeb.components {
    'use strict';

    export class ApplicationProperty {
        private _key: string;
        private _value: string;

        constructor(key: string, value: string) {
            this._key = key;
            this._value = value;
        }

        public set key(key: string) {
            this._key = key;
        }

        public set value(value: string) {
            this._value = value;
        }

        public get key():string {
            return this._key;
        }

        public get value():string {
            return this._value;
        }
    }

    export class ComponentConfig implements angular.IComponentOptions {
        transclude: boolean = false;
        bindings: any = {
            applicationProperties: '<'
        };
        templateUrl: string = 'src/admin/component/adminView/adminView.html';
        controller: any = AdminView;
        controllerAs: string = 'vm';

        constructor() {
        }
    }


    export interface IAdminView {
        length: number;
        applicationProperty: any;
        applicationPropertyRS: ebeguWeb.services.IApplicationPropertyRS;
        applicationProperties: any;

        fetchList: () => angular.IHttpPromise<any>;
        submit: () => void;
        removeRow: (row: any) => void; // todo team add type (muessen warten bis es eine DefinitelyTyped fuer smarttable gibt)
        createItem: () => void;
    }

    export class AdminView implements IAdminView {
        length: number;
        applicationProperty: ApplicationProperty;
        applicationPropertyRS: ebeguWeb.services.IApplicationPropertyRS;
        applicationProperties: Array<ApplicationProperty>;

        static $inject = ['applicationPropertyRS', 'MAX_LENGTH'];

        /* @ngInject */
        constructor(applicationPropertyRS: ebeguWeb.services.IApplicationPropertyRS, MAX_LENGTH: number) {
            this.length = MAX_LENGTH;
            this.applicationProperty = null;
            this.applicationProperties = null;
            this.applicationPropertyRS = applicationPropertyRS;
            this.fetchList();
        }

        fetchList() {
            return this.applicationPropertyRS.getAllApplicationProperties();
        }

        submit() {
            this.applicationPropertyRS.create(this.applicationProperty.key, this.applicationProperty.value)
                .then((response) => {
                    this.applicationProperty = null;
                    this.applicationProperties.push(response.data);
                });
            //todo team fehlerhandling
        }

        removeRow(row: any) {
            this.applicationPropertyRS.remove(row.name).then((reponse) => {
                var index = this.applicationProperties.indexOf(row);
                if (index !== -1) {
                    this.applicationProperties.splice(index, 1);
                }

            });
        }

        createItem() {
            this.applicationProperty = new ApplicationProperty('', '');
        }
    }


    angular.module('ebeguWeb.admin').component('adminView', new ComponentConfig());


}
