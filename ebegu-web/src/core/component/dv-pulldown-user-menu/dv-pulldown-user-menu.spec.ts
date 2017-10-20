import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {EbeguWebCore} from '../../core.module';
import {DvPulldownUserMenuController} from './dv-pulldown-user-menu';

describe('DvPulldownUserMenuController', function () {

    let authServiceRS: AuthServiceRS;
    let $state: angular.ui.IStateService;
    let controller: DvPulldownUserMenuController;
    let $q: angular.IQService;
    let scope: angular.IScope;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        authServiceRS = $injector.get('AuthServiceRS');
        scope = $injector.get('$rootScope').$new();
        $q = $injector.get('$q');
        $state = $injector.get('$state');
    }));

    beforeEach(() => {
        controller = new DvPulldownUserMenuController($state, authServiceRS);
    });

    describe('API Usage', function () {
        describe('logout()', () => {
            it('must call the logout function and redirect to the login page', () => {
                spyOn($state, 'go');

                controller.logout();
                scope.$apply();
                //actual logout happens on login page
                expect($state.go).toHaveBeenCalledWith('login', {type: 'logout'});
            });
        });
    });

});
