import {IDirective, IDirectiveFactory, IDirectiveLinkFn, IScope, IAugmentedJQuery, IAttributes} from 'angular';

export default class DVMaxLength implements IDirective {
    static $inject = ['CONSTANTS'];

    restrict = 'A';
    require = 'ngModel';
    length: number;
    link: IDirectiveLinkFn;

    /* @ngInject */
    constructor(CONSTANTS: any) {
        this.length = CONSTANTS.MAX_LENGTH;
        this.link = (scope: IScope, element: IAugmentedJQuery, attrs: IAttributes, ctrl: any) => {
            if (!ctrl) {
                return;
            }

            ctrl.$validators.dvMaxLength = (modelValue: any, viewValue: any) => {
                return ctrl.$isEmpty(viewValue) || (viewValue.length <= this.length);
            };
        };
    }

    static factory(): IDirectiveFactory {
        const directive = (CONSTANTS: any) => new DVMaxLength(CONSTANTS);
        directive.$inject = ['CONSTANTS'];
        return directive;
    }
}

