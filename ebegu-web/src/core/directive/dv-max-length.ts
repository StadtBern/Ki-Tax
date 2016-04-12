import {IDirective, IDirectiveFactory, IDirectiveLinkFn, IScope, IAugmentedJQuery, IAttributes} from "angular";

class DVMaxLength implements IDirective {
    static $inject = ['MAX_LENGTH'];

    restrict = 'A';
    require = 'ngModel';
    length: number;
    link: IDirectiveLinkFn;

    constructor(MAX_LENGTH: number) {
        this.length = MAX_LENGTH;
        this.link = this.linkFunction;
    }

    static factory(): IDirectiveFactory {
        const directive = (MAX_LENGTH: number) => new DVMaxLength(MAX_LENGTH);
        directive.$inject = ['MAX_LENGTH'];
        return directive;
    }

    private linkFunction(scope: IScope, element: IAugmentedJQuery, attrs: IAttributes, ctrl: any) {
        if (!ctrl) {
            return;
        }

        ctrl.$validators.dvMaxLength = (modelValue: any, viewValue: any) => {
            return ctrl.$isEmpty(viewValue) || (viewValue.length <= this.length);
        };
    }
}

angular.module('ebeguWeb.core').directive('dvMaxLength', DVMaxLength.factory());
