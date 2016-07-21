import {IDirective, IDirectiveFactory, IDirectiveLinkFn, IScope, IAugmentedJQuery, IAttributes} from 'angular';
import {TSSubmitEvent} from '../../events/TSSubmitEvent';

/**
 * this directive can be added to a form to boradcast a form submit event
 */
export default class DVSubmitevent implements IDirective {
    static $inject: string[] = [];

    restrict = 'A';
    require = 'form';
    link: IDirectiveLinkFn;

    /* @ngInject */
    constructor() {
        this.link = (scope: IScope, element: IAugmentedJQuery, attrs: IAttributes, ctrl: any) => {
            element.on('submit', function () {
                scope.$broadcast(TSSubmitEvent[TSSubmitEvent.FORM_SUBMIT]);
            });
        };
    }

    static factory(): IDirectiveFactory {
        const directive = () => new DVSubmitevent();
        return directive;
    }
}

