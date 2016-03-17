/// <reference path="../../../typings/browser.d.ts" />
module ebeguWeb.directive {
    'use strict';
    class DVMaxLength implements ng.IDirective {
        restrict = 'A';
        require = 'ngModel';
        length:number;

        constructor(private MAX_LENGTH:number) {

            this.length = MAX_LENGTH;
        }

        link = (scope:ng.IScope, element:ng.IAugmentedJQuery, attrs:ng.IAttributes, ctrl:any) => {
            if (!ctrl) {
                return;
            }

            ctrl.$validators.dvMaxLength = function (modelValue, viewValue) {
                return ctrl.$isEmpty(viewValue) || (viewValue.length <= length);
            };
        };

        static factory():ng.IDirectiveFactory {
            const directive = (MAX_LENGTH:number) => new DVMaxLength(MAX_LENGTH);
            directive.$inject = ['MAX_LENGTH'];
            return directive;
        }
    }

    angular.module('ebeguWeb.core').directive('dvMaxLength', DVMaxLength.factory());

}


