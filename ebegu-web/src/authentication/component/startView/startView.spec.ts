import {EbeguWebCore} from '../../../core/core.module';
import {TSAuthEvent} from '../../../models/enums/TSAuthEvent';
import {TSRole} from '../../../models/enums/TSRole';
import TSUser from '../../../models/TSUser';
import {EbeguAuthentication} from '../../authentication.module';
import AuthServiceRS from '../../service/AuthServiceRS.rest';
import {StartViewController} from './startView';

describe('startView', function () {

    //evtl ist modulaufteilung hier nicht ganz sauber, wir brauchen sowohl core als auch auth modul
    beforeEach(angular.mock.module(EbeguWebCore.name));
    beforeEach(angular.mock.module(EbeguAuthentication.name));

    let $rootScope: angular.IRootScopeService;
    let scope: angular.IScope;
    let $componentController: angular.IComponentControllerService;
    let startViewController: StartViewController;
    let authService: AuthServiceRS;
    let mockPrincipal: TSUser;
    let state: angular.ui.IStateService;

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        $componentController = $injector.get('$componentController');
        $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
        authService = $injector.get('AuthServiceRS');
        state = $injector.get('$state');
        startViewController = new StartViewController(state, $rootScope, authService);

    }));
    beforeEach(function () {
        mockPrincipal = new TSUser();
        mockPrincipal.nachname = 'mockprincipal';
        mockPrincipal.vorname = 'tester';
        mockPrincipal.role = TSRole.GESUCHSTELLER;
    });

    it('should be defined', function () {
        /*
         To initialise your component controller you have to setup your (mock) bindings and
         pass them to $componentController.
         */
        expect(startViewController).toBeDefined();
    });

    it('should  broadcast "AUTH_EVENTS.notAuthenticated" if no principal is available', function () {
        let broadcast = spyOn($rootScope, '$broadcast');
        startViewController.$onInit();
        expect(broadcast).toHaveBeenCalledWith(TSAuthEvent.NOT_AUTHENTICATED, 'not logged in on startpage');
    });

    describe('should  redirect based on role of principal', function () {
        it('should go to gesuchstellerDashboard if role is gesuchsteller', function () {
            spyOn(authService, 'getPrincipal').and.returnValue(mockPrincipal);
            spyOn(state, 'go');
            startViewController.$onInit();
            expect(state.go).toHaveBeenCalledWith('gesuchstellerDashboard');
        });
        it('should go to pendenzen if role is sachbearbeiter ja', function () {
            mockPrincipal.role = TSRole.SACHBEARBEITER_JA;
            spyOn(authService, 'getPrincipal').and.returnValue(mockPrincipal);
            spyOn(state, 'go');
            startViewController.$onInit();
            expect(state.go).toHaveBeenCalledWith('pendenzen');
        });
    });
});
