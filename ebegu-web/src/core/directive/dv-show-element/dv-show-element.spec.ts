import '../../../bootstrap.ts';
import 'angular-mocks';
import {EbeguWebCore} from '../../core.module';
import {DVShowElement} from './dv-show-element';
import {ICompileService, IScope, IAugmentedJQuery} from 'angular';
import {EbeguAuthentication} from '../../../authentication/authentication.module';
import IInjectorService = angular.auto.IInjectorService;

describe('DVShowElement', function () {

    // let controller: DVShowElementController;
    // let dvShowElement: DVShowElement;
    let $rootScope: IScope;
    let $compile: ICompileService;
    let element: IAugmentedJQuery;

    beforeEach(angular.mock.module(EbeguAuthentication.name));
    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: IInjectorService) {
        $rootScope = $injector.get('$rootScope');
        // scope = $rootScope.$new();

        $compile = $injector.get('$compile');

    }));

    describe('checkRoles', function() {
        it('should return true for the same role as the user', function () {
            // element = $compile('<div>Show me</div>')($rootScope);
            element = $compile('<div dv-show-element dv-allowed-roles="E" dv-expression="true">Show me</div>')($rootScope);


            // let authServiceRS: any = $injector.get('AuthServiceRS');
            // controller = new DVShowElementController(authServiceRS);
            $rootScope.$digest();

            expect(element).toBeDefined();

            // expect(element.hasClass('ng-hide')).toBe(true);
        });
    });
});
