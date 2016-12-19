import {
    IDirective,
    IDirectiveFactory,
    IDirectiveLinkFn,
    IScope,
    IAugmentedJQuery,
    IAttributes,
    INgModelController
} from 'angular';

/**
 * this directive can be added to an element that has an ngModel to trim the empty string to null
 */
export default class DVTrimEmpty implements IDirective {
    static $inject: string[] = [];

    restrict = 'A';
    require = '?ngModel';
    link: IDirectiveLinkFn;

    /* @ngInject */
    constructor() {
        this.link = (scope: IScope, element: IAugmentedJQuery, attrs: IAttributes, ngModel: INgModelController) => {
            if (ngModel) {
                  let emptyTrimFunc = function (value: any) {
                    return value === '' ? null : value;
                  };
                  ngModel.$parsers.push(emptyTrimFunc);
                }
        };
    }

    static factory(): IDirectiveFactory {
        const directive = () => new DVTrimEmpty();
        return directive;
    }
}

