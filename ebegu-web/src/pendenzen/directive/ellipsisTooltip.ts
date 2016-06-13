import {IDirective, IDirectiveFactory, IDirectiveLinkFn, IScope, IAugmentedJQuery} from 'angular';

export default class EllipsisTooltip implements IDirective {

    link: IDirectiveLinkFn;

    constructor() {
        this.link = (scope: IScope, element: IAugmentedJQuery, attrs: any) => {
            // Die Direktive wird vor dem ng-bind ausgefuehrt, deswegen muessen wir ein watch einfuegen, sodass der Code erst
            // ausgefuehrt wird, wenn das ng-bind vom Element fertig ist und der Text deshalb stimmt.
            scope.$watch(attrs.ngBind, (interpolatedValue: any) => {
                if (element[0].offsetWidth < element[0].scrollWidth) {
                    //hiere wird vergliechen ob der Text groesser als der offsetWidth ist, in dem Fall wissen wir dass ellipsis hinzugefuegt wurden und fuegen title hinzu
                    element[0].title = interpolatedValue;
                }
            });
        };
    }

    static factory(): IDirectiveFactory {
        const directive = () => new EllipsisTooltip();
        return directive;
    }
}
