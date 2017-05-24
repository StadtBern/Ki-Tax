import IDirective = angular.IDirective;
import IDirectiveLinkFn = angular.IDirectiveLinkFn;
import {DVsTPersistService} from '../../service/dVsTPersistService';
import IScope = angular.IScope;
import {IAugmentedJQuery, IDirectiveFactory} from 'angular';
import IAttributes = angular.IAttributes;

export default class DVSTResetSearch implements IDirective {
    static $inject: string[] = ['UserRS', 'InstitutionRS', 'DVsTPersistService'];

    restrict = 'A';
    require = '^stTable';
    link: IDirectiveLinkFn;

    /* @ngInject */
    constructor(private dVsTPersistService: DVsTPersistService) {
        this.link = (scope: IScope, element: IAugmentedJQuery, attrs: IAttributes, ctrl: any) => {
            let nameSpace: string = attrs.dvStPersist;
            return element.bind('click', function() {
                return scope.$apply(function() {
                    let tableState = ctrl.tableState();
                    tableState.search.predicateObject = {};
                    tableState.pagination.start = 0;
                    dVsTPersistService.deleteData(nameSpace);
                    return ctrl.pipe();
                });
            });
        };
    }

    static factory(): IDirectiveFactory {
        const directive = (dVsTPersistService: any) => new DVSTResetSearch(dVsTPersistService);
        directive.$inject = ['DVsTPersistService'];
        return directive;
    }
}

