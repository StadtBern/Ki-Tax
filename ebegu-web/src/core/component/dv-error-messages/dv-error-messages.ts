/// <reference path="../../../../typings/browser.d.ts" />
module ebeguWeb.components {
    'use strict';


    export class DvErrorMessagesComponentConfig implements angular.IComponentOptions {
        transclude:boolean = false;
        bindings:any = {
            errorObject: '<for'
        };
        templateUrl:string = 'src/core/component/dv-error-messages/dv-error-messages.html';
        controller:any  = DvErrorMessages;
        controllerAs:string = 'vm';

        constructor() {
        }
    }

    export class DvErrorMessages {
    }
    angular.module('ebeguWeb.core').component('dvErrorMessages', new DvErrorMessagesComponentConfig());
}


